package com.tig.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tig.usercenter.model.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author jsnlg
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-06-10 14:00:18
*/
public interface UserService extends IService<User> {

    int userLogout(HttpServletRequest httpServletRequest);

    /**
     * 用户注册
     *
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return long
     */
    long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    /**
     * 用户脱敏
     *
     * @param user
     * @return {@link User }
     */
    User safetyUser(User user);


}
