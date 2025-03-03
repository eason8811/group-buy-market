package xin.eason.domain.tags.service;

/**
 * 人群标签领域服务接口
 */
public interface ITagsService {

    /**
     * 根据 tagId 标签 ID, 和 batchId 批次 ID执行人群标签批次任务
     * @param tagId 标签 ID
     * @param batchId 批次 ID
     */
    void execTagBatchJob(String tagId, String batchId);
}
