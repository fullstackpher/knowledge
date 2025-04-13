package io.github.talelin.knowledge.service;

import io.github.talelin.knowledge.dto.PromotionRecordDTO;
import io.github.talelin.knowledge.model.PromotionRecordDO;

import java.util.List;

public interface PromotionRecordService {

    List<PromotionRecordDO> getAllPromotionRecords();

    Boolean createPromotionRecord(PromotionRecordDTO validator);
}
