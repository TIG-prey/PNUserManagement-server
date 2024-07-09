package com.tig.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tig.usercenter.common.ErrorCode;
import com.tig.usercenter.exception.BusinessException;
import com.tig.usercenter.mapper.UserMapper;
import com.tig.usercenter.model.domain.User;
import com.tig.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tig.usercenter.contant.UserContant.USER_LOGIN_STATE;

/**
 * @author jsnlg
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-06-10 14:00:18
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    /**
     * 盐值,混淆密码
     */
    private static final String SALT = "JianShang";

    @Resource
    private UserMapper userMapper;

    @Override
    public int userLogout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * @param userAccount 用户账号
     * @param userPassword  用户密码
     * @param checkPassword  校验密码
     * @return long
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword ,String planetCode) {
        // 检查账号及密码长度是否符合
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "^[^_`~!@#$%^&*()+=|{}':;\\[\\].<>/?\\s]+$";
        Matcher matcher = Pattern
                .compile(validPattern)
                .matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户不符合规范");
        }
        // 检验密码和二次密码是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不同");
        }
        // 账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户已存在");
        }
        // 星球编号不能重复
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号已存在");
        }
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        return user.getId();
    }

    /**
     *
     * @param userAccount 用户账号
     * @param userPassword  用户密码
     * @return {@link User }
     */
    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        // 检查账号及密码长度是否符合
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        // 判断账号长度
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度不符合");
        }
        // 判断密码长度
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不符合");
        }
        // 账户不能包含特殊字符
        String validPattern = "^[^_`~!@#$%^&*()+=|{}':;\\[\\].<>/?\\s]+$";
        Matcher matcher = Pattern
                .compile(validPattern)
                .matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户不符合规范");
        }
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        // 比对账号和密码
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        // LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
        //         .eq(User::getUserAccount, userAccount)
        //         .eq(User::getUserPassword, encryptPassword);
        // User users = userMapper.selectOne(userLambdaQueryWrapper);
        // 判断用户是否为空
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"登录的用户为空");
        }
        // 获取用户脱敏后的数据
        User safetyUser = this.safetyUser(user);
        // 记录用户的登录态
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param user
     * @return {@link User }
     */
    @Override
    public User safetyUser(User user) {
        if (user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"该用户数据为空");
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setPlanetCode(user.getPlanetCode());
        return safetyUser;
    }
}

