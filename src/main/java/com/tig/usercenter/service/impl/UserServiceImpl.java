package com.tig.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tig.usercenter.model.domain.User;
import com.tig.usercenter.service.UserService;
import com.tig.usercenter.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jsnlg
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-06-10 14:00:18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    /**
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return long
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 检查账号及密码长度是否符合
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }
        // 账户不能包含特殊字符
        String validPattern = ".*[[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\\\n|\\\\r|\\\\t].*";
        Matcher matcher = Pattern
                .compile(validPattern)
                .matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 检验密码和二次密码是否相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        int count = this.count(userQueryWrapper);
        if (count > 0) {
            return -1;
        }
        // 密码加密
        final String SALT = "JianShang";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }
}




