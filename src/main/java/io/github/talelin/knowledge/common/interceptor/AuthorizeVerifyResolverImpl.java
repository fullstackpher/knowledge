package io.github.talelin.knowledge.common.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import io.github.talelin.autoconfigure.bean.MetaInfo;
import io.github.talelin.autoconfigure.exception.AuthenticationException;
import io.github.talelin.autoconfigure.exception.AuthorizationException;
import io.github.talelin.autoconfigure.exception.NotFoundException;
import io.github.talelin.autoconfigure.exception.TokenInvalidException;
import io.github.talelin.autoconfigure.interfaces.AuthorizeVerifyResolver;
import io.github.talelin.core.token.DoubleJWT;
import io.github.talelin.knowledge.common.LocalUser;
import io.github.talelin.knowledge.model.PermissionDO;
import io.github.talelin.knowledge.model.UserDO;
import io.github.talelin.knowledge.service.GroupService;
import io.github.talelin.knowledge.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author pedro@TaleLin
 * @author Juzi@TaleLin
 * 鉴权实现类
 */
@Component
public class AuthorizeVerifyResolverImpl implements AuthorizeVerifyResolver {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String BEARER_PATTERN = "^Bearer$";

    @Autowired
    private DoubleJWT jwt;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Value("${lin.file.domain}")
    private String domain;

    @Value("${lin.file.serve-path:assets/**}")
    private String servePath;


    @Override
    public boolean handleLogin(HttpServletRequest request, HttpServletResponse response, MetaInfo meta) {
        String tokenStr = verifyHeader(request);
        Map<String, Claim> claims;
        try {
            claims = jwt.decodeAccessToken(tokenStr);
        } catch (TokenExpiredException e) {
            throw new io.github.talelin.autoconfigure.exception.TokenExpiredException(10051);
        } catch (AlgorithmMismatchException | SignatureVerificationException | JWTDecodeException | InvalidClaimException e) {
            throw new TokenInvalidException(10041);
        }
        return getClaim(claims);
    }

    @Override
    public boolean handleGroup(HttpServletRequest request, HttpServletResponse response, MetaInfo meta) {
        handleLogin(request, response, meta);
        UserDO user = LocalUser.getLocalUser();
        if (verifyAdmin(user)) {
            return true;
        }
        Integer userId = user.getId();
        String permission = meta.getPermission();
        String module = meta.getModule();
        List<PermissionDO> permissions = userService.getUserPermissions(userId);
        boolean matched = permissions.stream().anyMatch(it -> it.getModule().equals(module) && it.getName().equals(permission));
        if (!matched) {
            throw new AuthenticationException(10001);
        }
        return true;
    }

    @Override
    public boolean handleAdmin(HttpServletRequest request, HttpServletResponse response, MetaInfo meta) {
        handleLogin(request, response, meta);
        UserDO user = LocalUser.getLocalUser();
        if (!verifyAdmin(user)) {
            throw new AuthenticationException(10001);
        }
        return true;
    }


    @Override
    public boolean handleRefresh(HttpServletRequest request, HttpServletResponse response, MetaInfo meta) {
        String tokenStr = verifyHeader(request);
        Map<String, Claim> claims;
        try {
            claims = jwt.decodeRefreshToken(tokenStr);
        } catch (TokenExpiredException e) {
            throw new io.github.talelin.autoconfigure.exception.TokenExpiredException(10052);
        } catch (AlgorithmMismatchException | SignatureVerificationException | JWTDecodeException | InvalidClaimException e) {
            throw new TokenInvalidException(10042);
        }
        return getClaim(claims);
    }

    @Override
    public boolean handleNotHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void handleAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 记住：很重要，请求结束后，一定要清理 ThreadLocal 中的用户信息
        LocalUser.clearLocalUser();
    }

    private boolean getClaim(Map<String, Claim> claims) {
        if (claims == null) {
            throw new TokenInvalidException(10041);
        }
        int identity = claims.get("identity").asInt();
        UserDO user = userService.getById(identity);
        if (user == null) {
            throw new NotFoundException(10021);
        }
        String avatarUrl;
        final String protocolPrefix = "http";
        if (user.getAvatar() == null) {
            avatarUrl = null;
        } else if (user.getAvatar().startsWith(protocolPrefix)) {
            avatarUrl = user.getAvatar();
        } else {
            avatarUrl = domain + servePath.split("/")[0] + "/" + user.getAvatar();
        }
        user.setAvatar(avatarUrl);
        LocalUser.setLocalUser(user);
        return true;
    }

    /**
     * 检查用户是否为管理员
     *
     * @param user 用户
     */
    private boolean verifyAdmin(UserDO user) {
        return groupService.checkIsRootByUserId(user.getId());
    }

    private String verifyHeader(HttpServletRequest request) {
        // 处理头部header,带有access_token的可以访问
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || Strings.isBlank(authorization)) {
            throw new AuthorizationException(10012);
        }
        String[] splits = authorization.split(" ");
        final int tokenSplitLen = 2;
        if (splits.length != tokenSplitLen) {
            throw new AuthorizationException(10013);
        }
        // Bearer 字段
        String scheme = splits[0];
        // token 字段
        String tokenStr = splits[1];
        if (!Pattern.matches(BEARER_PATTERN, scheme)) {
            throw new AuthorizationException(10013);
        }
        return tokenStr;
    }
}
