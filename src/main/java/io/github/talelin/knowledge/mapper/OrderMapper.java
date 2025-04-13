package io.github.talelin.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.talelin.knowledge.model.OrderDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface OrderMapper extends BaseMapper<OrderDO> {
    @Insert("INSERT INTO `order` (user_id, course_id, status, create_time) VALUES (#{userId}, #{courseId}, #{status}, #{createTime})")
    int insertOrder(OrderDO orderDO);

    @Select("SELECT * FROM `order`")
    List<OrderDO> getAllOrders();
}
