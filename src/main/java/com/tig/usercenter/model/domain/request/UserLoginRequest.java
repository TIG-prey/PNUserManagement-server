package com.tig.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author jsnlg
 * {@code @date} 2024/06/21
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -8145335636048644239L;

    private String userAccount;
    private String userPassword;
}
