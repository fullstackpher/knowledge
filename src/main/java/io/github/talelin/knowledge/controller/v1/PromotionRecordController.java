package io.github.talelin.knowledge.controller.v1;

import io.github.talelin.knowledge.dto.PromotionRecordDTO;
import io.github.talelin.knowledge.model.PromotionRecordDO;
import io.github.talelin.knowledge.service.PromotionRecordService;
import io.github.talelin.knowledge.vo.CreatedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/promotion_record")
public class PromotionRecordController {

    @Autowired
    private PromotionRecordService promotionRecordService;

    @GetMapping("/list")
    public List<PromotionRecordDO> getAllPromotionRecords() {
        return promotionRecordService.getAllPromotionRecords();
    }

    @PostMapping("/create")
    public CreatedVO created(@RequestBody PromotionRecordDTO validator) {
        promotionRecordService.createPromotionRecord(validator);
        return new CreatedVO("推广记录创建成功");
    }
}
