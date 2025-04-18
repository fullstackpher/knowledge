package io.github.talelin.knowledge.common.enumeration;


import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author colorful@TaleLin
 * @author Juzi@TaleLin
 *
 * 分组级别枚举类
 */
public enum GroupLevelEnum implements IEnum<Integer> {
    /**
     * 超级管理员
     */
    ROOT(1),
    /**
     * 游客
     */
    GUEST(2),
    /**
     * 普通用户
     */
    USER(3);

    private final Integer value;

    GroupLevelEnum(Integer value) {
        this.value = value;
    }

    /**
     * MybatisEnumTypeHandler 转换时调用此方法
     *
     * @return 枚举对应的 code 值
     * @see com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler
     */
    @Override
    public Integer getValue() {
        return this.value;
    }

}
