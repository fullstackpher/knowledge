package io.github.talelin.knowledge.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.talelin.knowledge.common.mybatis.LinPage;
import io.github.talelin.knowledge.model.GroupDO;
import io.github.talelin.knowledge.model.UserDO;
import io.github.talelin.knowledge.model.UserGroupDO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("test")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private UserGroupMapper userGroupMapper;


    @Test
    void selectCountByUsername() {
        String email = "13129982604@qq.com";
        String username = "pedro-test";
        UserDO userDO = new UserDO();
        userDO.setEmail(email);
        userDO.setUsername(username);
        userMapper.insert(userDO);
        int count = userMapper.selectCountByUsername(username);
        assertTrue(count > 0);
    }

    @Test
    void selectCountById() {
        String email = "13129982604@qq.com";
        String username = "pedro-test";
        UserDO userDO = new UserDO();
        userDO.setEmail(email);
        userDO.setUsername(username);
        userMapper.insert(userDO);
        int count = userMapper.selectCountById(userDO.getId());
        assertTrue(count > 0);
    }

    @Test
    void selectPageByGroupId() {
        String email = "13129982604@qq.com";
        String username = "pedro-test";
        UserDO userDO = new UserDO();
        userDO.setEmail(email);
        userDO.setUsername(username);
        userMapper.insert(userDO);

        GroupDO group = GroupDO.builder().name("group").info("零零落落").build();
        groupMapper.insert(group);

        userGroupMapper.insert(new UserGroupDO(userDO.getId(), group.getId()));

        LinPage page = new LinPage(0, 10);
        IPage<UserDO> iPage = userMapper.selectPageByGroupId(page, group.getId(), 99999);
        assertTrue(iPage.getTotal() > 0);
        boolean anyMatch = iPage.getRecords().stream().anyMatch(it -> it.getUsername().equals(username));
        assertTrue(anyMatch);
    }
}