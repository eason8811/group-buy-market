package xin.eason.domain.tags.adapter.repository;

import xin.eason.domain.tags.model.entity.CrowdTagsJobEntity;

import java.util.List;

/**
 * 人群标签领域仓储接口
 */
public interface ITagsRepository {

    /**
     * 根据人群标签 tagId 和 batchId 查询人群标签任务
     * @param tagId 标签 ID
     * @param batchId 批次 ID
     * @return 人群标签任务实体
     */
    CrowdTagsJobEntity queryCrowdTagsJobEntity(String tagId, String batchId);

    /**
     * 根据 tagId 将 userId 批量写入 crowd_tags_detail 表
     * @param tagId 人群标签 ID
     * @param userIdList 用户 ID 列表
     */
    void addCrowdTagsUserId(String tagId, List<String> userIdList);

    /**
     * 根据 tagId 更新 crowd_tags 表内的 statistics 字段, 使其与本批次统计的用户数量相等
     * @param tagId 人群标签 ID
     * @param statistics 统计的用户数量
     */
    void updateCrowdTagsStatistics(String tagId, Integer statistics);
}
