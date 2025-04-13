package io.github.talelin.knowledge.service.impl;

import io.github.talelin.knowledge.common.util.BeanCopyUtil;
import io.github.talelin.knowledge.dto.PromotionRecordDTO;
import io.github.talelin.knowledge.mapper.PromotionRecordMapper;
import io.github.talelin.knowledge.model.PromotionRecordDO;
import io.github.talelin.knowledge.service.PromotionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionRecordServiceImpl implements PromotionRecordService {
    @Autowired
    private PromotionRecordMapper promotionRecordMapper;
    @Override
    public List<PromotionRecordDO> getAllPromotionRecords() {
        return promotionRecordMapper.selectList(null);
    }

    @Override
    public Boolean createPromotionRecord(PromotionRecordDTO validator) {
        PromotionRecordDO promotionRecordDO = BeanCopyUtil.copyProperties(validator, PromotionRecordDO::new);
        return promotionRecordMapper.insert(promotionRecordDO) > 0;
    }
}
