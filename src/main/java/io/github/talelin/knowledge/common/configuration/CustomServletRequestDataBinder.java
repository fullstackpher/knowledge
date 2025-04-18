package io.github.talelin.knowledge.common.configuration;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Gadfly
 *
 * 自定义servlet请求参数绑定类
 */

public class CustomServletRequestDataBinder extends ServletRequestDataBinder {

    public CustomServletRequestDataBinder(final Object target) {
        super(target);
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        List<PropertyValue> pvs = mpvs.getPropertyValueList();
        List<PropertyValue> adds = new LinkedList<>();
        for (PropertyValue pv : pvs) {
            String name = pv.getName();
            String camel = StringUtils.underlineToCamel(name);
            if (!name.equals(camel)) {
                adds.add(new PropertyValue(camel, pv.getValue()));
            }
        }
        pvs.addAll(adds);
    }

}
