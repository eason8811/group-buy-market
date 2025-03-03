package xin.eason.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBitSet;
import org.springframework.stereotype.Component;
import xin.eason.domain.tags.adapter.repository.ITagsRepository;
import xin.eason.domain.tags.model.entity.CrowdTagsJobEntity;
import xin.eason.infrastructure.dao.ICrowdTags;
import xin.eason.infrastructure.dao.ICrowdTagsDetail;
import xin.eason.infrastructure.dao.ICrowdTagsJob;
import xin.eason.infrastructure.dao.po.CrowdTagsDetailPO;
import xin.eason.infrastructure.dao.po.CrowdTagsJobPO;
import xin.eason.infrastructure.dao.po.CrowdTagsPO;
import xin.eason.infrastructure.redis.IRedisService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 人群标签领域仓储实现类
 */
@Component
@RequiredArgsConstructor
public class TagRepository implements ITagsRepository {

    /**
     * crowd_tags_job 表对应 Mapper
     */
    private final ICrowdTagsJob crowdTagsJob;
    /**
     * crowd_tags_detail 表对应 Mapper
     */
    private final ICrowdTagsDetail crowdTagsDetail;
    /**
     * crowd_tags 表对应 Mapper
     */
    private final ICrowdTags crowdTags;
    /**
     * Redis 服务
     */
    private final IRedisService redisService;

    /**
     * 根据人群标签 tagId 和 batchId 查询人群标签任务
     *
     * @param tagId   标签 ID
     * @param batchId 批次 ID
     * @return 人群标签任务实体
     */
    @Override
    public CrowdTagsJobEntity queryCrowdTagsJobEntity(String tagId, String batchId) {
        LambdaQueryWrapper<CrowdTagsJobPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrowdTagsJobPO::getTagId, tagId).eq(CrowdTagsJobPO::getBatchId, batchId);
        CrowdTagsJobPO crowdTagsJobPO = crowdTagsJob.selectOne(wrapper);

        return CrowdTagsJobEntity.builder()
                .batchId(batchId)
                .tagType(crowdTagsJobPO.getTagType())
                .tagRule(crowdTagsJobPO.getTagRule())
                .statStartTime(crowdTagsJobPO.getStatStartTime())
                .statEndTime(crowdTagsJobPO.getStatEndTime())
                .status(crowdTagsJobPO.getStatus())
                .build();
    }

    /**
     * 根据 tagId 将 userId 批量写入 crowd_tags_detail 表
     *
     * @param tagId      人群标签 ID
     * @param userIdList 用户 ID 列表
     */
    @Override
    public void addCrowdTagsUserId(String tagId, List<String> userIdList) {
        List<CrowdTagsDetailPO> crowdTagsDetailList = userIdList.stream()
                .map(
                        userId -> {
                            CrowdTagsDetailPO crowdTagsDetail = CrowdTagsDetailPO.builder()
                                    .userId(userId)
                                    .tagId(tagId)
                                    .createTime(LocalDateTime.now())
                                    .updateTime(LocalDateTime.now())
                                    .build();
                            // 将用户添加到 Redis 的 bitMap
                            RBitSet bitSet = redisService.getBitSet(tagId);
                            bitSet.set(redisService.getIndexFromUserId(userId), true);
                            return crowdTagsDetail;
                        }
                )
                .toList();

        crowdTagsDetail.insert(crowdTagsDetailList, 10000);
    }

    /**
     * 根据 tagId 更新 crowd_tags 表内的 statistics 字段, 使其与本批次统计的用户数量相等
     *
     * @param tagId      人群标签 ID
     * @param statistics 统计的用户数量
     */
    @Override
    public void updateCrowdTagsStatistics(String tagId, Integer statistics) {
        LambdaUpdateWrapper<CrowdTagsPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CrowdTagsPO::getTagId, tagId).set(CrowdTagsPO::getStatistics, statistics);
        crowdTags.update(wrapper);
    }
}
