package io.github.talelin.knowledge.common.interceptor;

import io.github.talelin.autoconfigure.interfaces.LoggerResolver;
import io.github.talelin.core.annotation.Logger;
import io.github.talelin.core.annotation.PermissionMeta;
import io.github.talelin.core.util.BeanUtil;
import io.github.talelin.knowledge.common.LocalUser;
import io.github.talelin.knowledge.model.UserDO;
import io.github.talelin.knowledge.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pedro@TaleLin
 * @author Juzi@TaleLin
 * @author colorful@TaleLin
 * 行为日志实现类
 */
@Slf4j
@Component
public class LoggerImpl implements LoggerResolver {

    @Autowired
    private LogService logService;

    /**
     * 日志格式匹配正则
     */
    private static final Pattern LOG_PATTERN = Pattern.compile("(?<=\\{)[^}]*(?=})");

    @Override
    public void handle(PermissionMeta meta, Logger logger, HttpServletRequest request, HttpServletResponse response) {
        String template = logger.template();
        UserDO user = LocalUser.getLocalUser();
        template = this.parseTemplate(template, user, request, response);
        String permission = "";
        if (meta != null) {
            permission = meta.value();
        }
        Integer userId = user.getId();
        String username = user.getUsername();
        String method = request.getMethod();
        String path = request.getServletPath();
        Integer status = response.getStatus();
        logService.createLog(template, permission, userId, username, method, path, status);
    }

    private String parseTemplate(String template, UserDO user, HttpServletRequest request, HttpServletResponse response) {
        // 调用 get 方法
        Matcher m = LOG_PATTERN.matcher(template);
        while (m.find()) {
            String group = m.group();
            String property = this.extractProperty(group, user, request, response);
            template = template.replace("{" + group + "}", property);
        }
        return template;
    }

    private String extractProperty(String item, UserDO user, HttpServletRequest request, HttpServletResponse response) {
        int i = item.lastIndexOf('.');
        String obj = item.substring(0, i);
        String prop = item.substring(i + 1);
        switch (obj) {
            case "user":
                if (user == null) {
                    return "";
                }
                return BeanUtil.getValueByPropName(user, prop);
            case "request":
                return BeanUtil.getValueByPropName(request, prop);
            case "response":
                return BeanUtil.getValueByPropName(response, prop);
            default:
                return "";
        }
    }
}
