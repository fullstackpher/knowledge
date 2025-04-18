package io.github.talelin.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.talelin.autoconfigure.exception.FailedException;
import io.github.talelin.autoconfigure.exception.ForbiddenException;
import io.github.talelin.autoconfigure.exception.NotFoundException;
import io.github.talelin.autoconfigure.exception.ParameterException;
import io.github.talelin.knowledge.bo.LoginCaptchaBO;
import io.github.talelin.knowledge.common.LocalUser;
import io.github.talelin.knowledge.common.configuration.LoginCaptchaProperties;
import io.github.talelin.knowledge.common.enumeration.GroupLevelEnum;
import io.github.talelin.knowledge.common.mybatis.LinPage;
import io.github.talelin.knowledge.common.util.BeanCopyUtil;
import io.github.talelin.knowledge.common.util.CaptchaUtil;
import io.github.talelin.knowledge.dto.user.ChangePasswordDTO;
import io.github.talelin.knowledge.dto.user.RegisterDTO;
import io.github.talelin.knowledge.dto.user.UpdateInfoDTO;
import io.github.talelin.knowledge.mapper.UserGroupMapper;
import io.github.talelin.knowledge.mapper.UserMapper;
import io.github.talelin.knowledge.model.GroupDO;
import io.github.talelin.knowledge.model.PermissionDO;
import io.github.talelin.knowledge.model.UserDO;
import io.github.talelin.knowledge.model.UserGroupDO;
import io.github.talelin.knowledge.service.GroupService;
import io.github.talelin.knowledge.service.PermissionService;
import io.github.talelin.knowledge.service.UserIdentityService;
import io.github.talelin.knowledge.service.UserService;
import io.github.talelin.knowledge.vo.LoginCaptchaVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author pedro@TaleLin
 * @author colorful@TaleLin
 * @author Juzi@TaleLin
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Autowired
    private UserIdentityService userIdentityService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private LoginCaptchaProperties captchaConfig;

    @Transactional
    @Override
    public UserDO createUser(RegisterDTO dto) {
        boolean exist = this.checkUserExistByUsername(dto.getUsername());
        if (exist) {
            throw new ForbiddenException(10071);
        }
        if (StringUtils.hasText(dto.getEmail())) {
            exist = this.checkUserExistByEmail(dto.getEmail());
            if (exist) {
                throw new ForbiddenException(10076);
            }
        } else {
            // bug 前端如果传入的email为 "" 时，由于数据库中存在""的email，会报duplication错误
            // 所以如果email为blank，必须显示设置为 null
            dto.setEmail(null);
        }
        UserDO user = new UserDO();
        BeanUtils.copyProperties(dto, user);
        this.baseMapper.insert(user);
        if (dto.getGroupIds() != null && !dto.getGroupIds().isEmpty()) {
            checkGroupsValid(dto.getGroupIds());
            checkGroupsExist(dto.getGroupIds());
            List<UserGroupDO> relations = dto.getGroupIds()
                    .stream()
                    .map(groupId -> new UserGroupDO(user.getId(), groupId))
                    .collect(Collectors.toList());
            userGroupMapper.insertBatch(relations);
        } else {
            // id为2的分组为游客分组
            Integer guestGroupId = groupService.getParticularGroupIdByLevel(GroupLevelEnum.GUEST);
            UserGroupDO relation = new UserGroupDO(user.getId(), guestGroupId);
            userGroupMapper.insert(relation);
        }
        userIdentityService.createUsernamePasswordIdentity(user.getId(), dto.getUsername(), dto.getPassword());
        return user;
    }

    @Transactional
    @Override
    public UserDO updateUserInfo(UpdateInfoDTO dto) {
        UserDO user = LocalUser.getLocalUser();
        if (StringUtils.hasText(dto.getUsername())) {
            boolean exist = this.checkUserExistByUsername(dto.getUsername());
            if (exist) {
                throw new ForbiddenException(10071);
            }

            boolean changeSuccess = userIdentityService.changeUsername(user.getId(), dto.getUsername());
            if (changeSuccess) {
                user.setUsername(dto.getUsername());
            }
        }
        BeanCopyUtil.copyNonNullProperties(dto, user);

        this.baseMapper.updateById(user);
        return user;
    }

    @Override
    public UserDO changeUserPassword(ChangePasswordDTO dto) {
        UserDO user = LocalUser.getLocalUser();
        boolean valid = userIdentityService.verifyUsernamePassword(user.getId(), user.getUsername(), dto.getOldPassword());
        if (!valid) {
            throw new ParameterException(10032);
        }
        valid = userIdentityService.changePassword(user.getId(), dto.getNewPassword());
        if (!valid) {
            throw new FailedException(10011);
        }
        return user;
    }

    @Override
    public List<GroupDO> getUserGroups(Integer userId) {
        return groupService.getUserGroupsByUserId(userId);
    }

    @Override
    public List<Map<String, List<Map<String, String>>>> getStructuralUserPermissions(Integer userId) {
        List<PermissionDO> permissions = getUserPermissions(userId);
        return permissionService.structuringPermissions(permissions);
    }

    @Override
    public List<PermissionDO> getUserPermissions(Integer userId) {
        // 查找用户搜索分组，查找分组下的所有权限
        List<Integer> groupIds = groupService.getUserGroupIdsByUserId(userId);
        if (groupIds == null || groupIds.isEmpty()) {
            return new ArrayList<>();
        }
        return permissionService.getPermissionByGroupIds(groupIds);
    }

    @Override
    public List<PermissionDO> getUserPermissionsByModule(Integer userId, String module) {
        List<Integer> groupIds = groupService.getUserGroupIdsByUserId(userId);
        if (groupIds == null || groupIds.isEmpty()) {
            return new ArrayList<>();
        }
        return permissionService.getPermissionByGroupIdsAndModule(groupIds, module);
    }

    @Override
    public UserDO getUserByUsername(String username) {
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserDO::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public boolean checkUserExistByUsername(String username) {
        int rows = this.baseMapper.selectCountByUsername(username);
        return rows > 0;
    }

    @Override
    public boolean checkUserExistByEmail(String email) {
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserDO::getEmail, email);
        int rows = this.baseMapper.selectCount(wrapper);
        return rows > 0;
    }

    @Override
    public boolean checkUserExistById(Integer id) {
        int rows = this.baseMapper.selectCountById(id);
        return rows > 0;
    }

    @Override
    public IPage<UserDO> getUserPageByGroupId(LinPage<UserDO> pager, Integer groupId) {
        Integer rootGroupId = groupService.getParticularGroupIdByLevel(GroupLevelEnum.ROOT);
        return this.baseMapper.selectPageByGroupId(pager, groupId, rootGroupId);
    }

    @Override
    public Integer getRootUserId() {
        Integer rootGroupId = groupService.getParticularGroupIdByLevel(GroupLevelEnum.ROOT);
        UserGroupDO userGroupDO = null;
        if (rootGroupId != 0) {
            QueryWrapper<UserGroupDO> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(UserGroupDO::getGroupId, rootGroupId);
            userGroupDO = userGroupMapper.selectOne(wrapper);
        }
        return userGroupDO == null ? 0 : userGroupDO.getUserId();
    }

    @Override
    public LoginCaptchaVO generateCaptcha() throws IOException, FontFormatException, GeneralSecurityException {
        String code = CaptchaUtil.getRandomString(CaptchaUtil.RANDOM_STR_NUM);
        String base64String = CaptchaUtil.getRandomCodeBase64(code);
        String tag = CaptchaUtil.getTag(code, captchaConfig.getSecret(), captchaConfig.getIv());
        return new LoginCaptchaVO(tag, "data:image/png;base64," + base64String);
    }

    @Override
    public boolean verifyCaptcha(String captcha, String tag) {
        try {
            LoginCaptchaBO captchaBO = CaptchaUtil.decodeTag(captchaConfig.getSecret(), captchaConfig.getIv(), tag);
            return captcha.equalsIgnoreCase(captchaBO.getCaptcha()) || System.currentTimeMillis() > captchaBO.getExpired();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private void checkGroupsExist(List<Integer> ids) {
        for (Integer id : ids) {
            if (!groupService.checkGroupExistById(id)) {
                throw new NotFoundException(10023);
            }
        }
    }

    private void checkGroupsValid(List<Integer> ids) {
        Integer rootGroupId = groupService.getParticularGroupIdByLevel(GroupLevelEnum.ROOT);
        boolean anyMatch = ids.stream().anyMatch(it -> it.equals(rootGroupId));
        if (anyMatch) {
            throw new ForbiddenException(10073);
        }
    }
}
