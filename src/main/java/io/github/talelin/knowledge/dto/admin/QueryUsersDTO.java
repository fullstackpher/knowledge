package io.github.talelin.knowledge.dto.admin;

import io.github.talelin.knowledge.dto.query.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;

/**
 * @author Gadfly
 * @since 2021-06-28 18:48
 * 用户查询数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryUsersDTO extends BasePageDTO {

    @Min(value = 1, message = "{group.id.positive}")
    private Integer groupId;
}
