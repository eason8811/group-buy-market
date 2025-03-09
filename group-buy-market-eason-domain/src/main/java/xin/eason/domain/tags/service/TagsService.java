package xin.eason.domain.tags.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xin.eason.domain.tags.adapter.repository.ITagsRepository;
import xin.eason.domain.tags.model.entity.CrowdTagsJobEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 人群标签领域服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagsService implements ITagsService{

    /**
     * 人群标签领域仓储
     */
    private final ITagsRepository repository;

    /**
     * 根据 tagId 标签 ID, 和 batchId 批次 ID执行人群标签批次任务
     *
     * @param tagId   标签 ID
     * @param batchId 批次 ID
     */
    @Override
    @Transactional
    public void execTagBatchJob(String tagId, String batchId) {
        log.info("人群标签批次任务 tagId:{} batchId:{}", tagId, batchId);

        // 1. 查询批次任务
        CrowdTagsJobEntity crowdTagsJobEntity = repository.queryCrowdTagsJobEntity(tagId, batchId);

        // 2. 采集用户数据 - 这部分需要采集用户的消费类数据，后续有用户发起拼单后再处理。

        // 3. 数据写入记录
        List<String> userIdList = new ArrayList<String>() {{
            add("Eason1");
            add("Eason2");
            add("Eason3");
        }};

        // 将 userId 信息写入 crowd_tags_detail 表
        repository.addCrowdTagsUserId(tagId, userIdList);

        // 更新 crowd_tags 表中的 statistics 字段
        repository.updateCrowdTagsStatistics(tagId, userIdList.size());
    }
}
