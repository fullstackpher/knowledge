package io.github.talelin.knowledge.dto.query;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Gadfly
 * 基础分页数据传输对象
 */
@Data
public class BasePageDTO {

    @Min(value = 1, message = "{page.count.min}")
    @Max(value = 30, message = "{page.count.max}")
    private Integer count;

    @Min(value = 0, message = "{page.number.min}")
    private Integer page;

    public Integer getCount() {
        if (null == count) {
            return 10;
        }
        return count;
    }

    public Integer getPage() {
        if (null == page) {
            return 0;
        }
        return page;
    }

}
