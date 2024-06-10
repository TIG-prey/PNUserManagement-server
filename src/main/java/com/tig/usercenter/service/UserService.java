package com.tig.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tig.usercenter.model.domain.User;

/**
* @author jsnlg
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-06-10 14:00:18
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return long
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);
}
