package io.github.talelin.knowledge.controller.cms;

import io.github.talelin.autoconfigure.exception.NotFoundException;
import io.github.talelin.autoconfigure.exception.ParameterException;
import io.github.talelin.core.annotation.AdminRequired;
import io.github.talelin.core.annotation.LoginRequired;
import io.github.talelin.core.annotation.PermissionModule;
import io.github.talelin.core.annotation.RefreshRequired;
import io.github.talelin.core.token.DoubleJWT;
import io.github.talelin.core.token.Tokens;
import io.github.talelin.knowledge.common.LocalUser;
import io.github.talelin.knowledge.common.configuration.LoginCaptchaProperties;
import io.github.talelin.knowledge.dto.user.ChangePasswordDTO;
import io.github.talelin.knowledge.dto.user.LoginDTO;
import io.github.talelin.knowledge.dto.user.RegisterDTO;
import io.github.talelin.knowledge.dto.user.UpdateInfoDTO;
import io.github.talelin.knowledge.model.GroupDO;
import io.github.talelin.knowledge.model.UserDO;
import io.github.talelin.knowledge.service.GroupService;
import io.github.talelin.knowledge.service.UserIdentityService;
import io.github.talelin.knowledge.service.UserService;
import io.github.talelin.knowledge.vo.CreatedVO;
import io.github.talelin.knowledge.vo.LoginCaptchaVO;
import io.github.talelin.knowledge.vo.UpdatedVO;
import io.github.talelin.knowledge.vo.UserInfoVO;
import io.github.talelin.knowledge.vo.UserPermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * @author pedro@TaleLin
 * @author Juzi@TaleLin
 */
@RestController
@RequestMapping("/cms/user")
@PermissionModule(value = "用户")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserIdentityService userIdentityService;

    @Autowired
    private DoubleJWT jwt;

    @Autowired
    private LoginCaptchaProperties captchaConfig;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @AdminRequired
    public CreatedVO register(@RequestBody @Validated RegisterDTO validator) {
        userService.createUser(validator);
        return new CreatedVO(11);
    }

    /**
     * 用户登陆
     */
    @PostMapping("/login")
    public Tokens login(@RequestBody @Validated LoginDTO validator, @RequestHeader(required = false) String tag) {
        if (Boolean.TRUE.equals(captchaConfig.getEnabled())) {
            if (!StringUtils.hasText(validator.getCaptcha()) || !StringUtils.hasText(tag)) {
                throw new ParameterException(10260);
            }
            if (!userService.verifyCaptcha(validator.getCaptcha(), tag)) {
                throw new ParameterException(10260);
            }
        }
        UserDO user = userService.getUserByUsername(validator.getUsername());
        if (user == null) {
            throw new NotFoundException(10021);
        }
        boolean valid = userIdentityService.verifyUsernamePassword(
                user.getId(),
                user.getUsername(),
                validator.getPassword());
        if (!valid) {
            throw new ParameterException(10031);
        }
        return jwt.generateTokens(user.getId());
    }

    @PostMapping("/captcha")
    public LoginCaptchaVO userCaptcha() throws Exception {
        if (Boolean.TRUE.equals(captchaConfig.getEnabled())) {
            return userService.generateCaptcha();
        }
        return new LoginCaptchaVO();
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    @LoginRequired
    public UpdatedVO update(@RequestBody @Validated UpdateInfoDTO validator) {
        userService.updateUserInfo(validator);
        return new UpdatedVO(6);
    }

    /**
     * 修改密码
     */
    @PutMapping("/change_password")
    @LoginRequired
    public UpdatedVO updatePassword(@RequestBody @Validated ChangePasswordDTO validator) {
        userService.changeUserPassword(validator);
        return new UpdatedVO(4);
    }

    /**
     * 刷新令牌
     */
    @GetMapping("/refresh")
    @RefreshRequired
    public Tokens getRefreshToken() {
        UserDO user = LocalUser.getLocalUser();
        return jwt.generateTokens(user.getId());
    }

    /**
     * 查询拥有权限
     */
    @GetMapping("/permissions")
    @LoginRequired
    public UserPermissionVO getPermissions() {
        UserDO user = LocalUser.getLocalUser();
        boolean admin = groupService.checkIsRootByUserId(user.getId());
        List<Map<String, List<Map<String, String>>>> permissions = userService.getStructuralUserPermissions(user.getId());
        UserPermissionVO userPermissions = new UserPermissionVO(user, permissions);
        userPermissions.setAdmin(admin);
        return userPermissions;
    }

    /**
     * 查询自己信息
     */
    @LoginRequired
    @GetMapping("/information")
    public UserInfoVO getInformation() {
        UserDO user = LocalUser.getLocalUser();
        List<GroupDO> groups = groupService.getUserGroupsByUserId(user.getId());
        return new UserInfoVO(user, groups);
    }
}
