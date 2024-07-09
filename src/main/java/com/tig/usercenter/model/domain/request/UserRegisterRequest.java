package com.tig.usercenter.model.domain.request;

import lombok.Data;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author jsnlg
 * {@code @date} 2024/06/21
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 195790535954813867L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
