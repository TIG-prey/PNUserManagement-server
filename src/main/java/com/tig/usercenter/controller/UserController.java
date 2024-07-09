package com.tig.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tig.usercenter.common.BaseResponse;
import com.tig.usercenter.common.ErrorCode;
import com.tig.usercenter.common.ResultUtils;
import com.tig.usercenter.exception.BusinessException;
import com.tig.usercenter.model.domain.User;
import com.tig.usercenter.model.domain.request.UserLoginRequest;
import com.tig.usercenter.model.domain.request.UserRegisterRequest;
import com.tig.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tig.usercenter.contant.UserContant.ADMIN_ROLE;
import static com.tig.usercenter.contant.UserContant.USER_LOGIN_STATE;

/**
 * @author jsnlg
 * @version 1.0.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册请求体
     * @return {@link Long } 用户ID
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        log.info("注册的用户信息：{}", userRegisterRequest);
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户信息为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 登录请求体
     * @param request
     * @return {@link User } 返回用户信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        log.info("登录信息：{}", userLoginRequest);
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"登录信息为空");
        }
        User user = userService.doLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * @param request 从cookie中获取信息
     * @return {@link User }
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object object = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) object;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.safetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 用户搜索
     *
     * @param username 用户名
     * @return {@link List }<{@link User }>
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        // 鉴权
        if (isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        log.info("搜索的用户信息：{}", username);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            userQueryWrapper.like("username", username);
        }
        List<User> userList = userService.list(userQueryWrapper);
        List<User> result = userList.stream().map(user -> userService.safetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    /**
     * 用户删除
     *
     * @param id 用户ID
     * @return boolean
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        log.info("删除用户的ID:{}", id);
        // 鉴权
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 判断id长度
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 用户退出登录
     *
     * @param httpServletRequest
     * @return {@link Integer }
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest httpServletRequest) {
        if (httpServletRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(httpServletRequest);
        return ResultUtils.success(result);
    }

    /**
     * 判断用户角色
     *
     * @param request
     * @return boolean
     */
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return !(user != null && user.getUserRole() == ADMIN_ROLE);
    }
}
