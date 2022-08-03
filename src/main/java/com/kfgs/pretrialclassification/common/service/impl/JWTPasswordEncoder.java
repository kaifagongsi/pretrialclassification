package com.kfgs.pretrialclassification.common.service.impl;

import com.kfgs.pretrialclassification.common.utils.MD5Util;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Date: 2020-01-22-00-12
 * Module: 自定义密码不加密
 * Description:
 * @author:
 */
public class JWTPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        //不做任何加密处理
//        return charSequence.toString();
        return MD5Util.getMD5(charSequence.toString());
    }

    @Override
    public boolean matches(CharSequence charSequence, String password) {
       /* e10adc3949ba59abbe56e057f20f883e
       //charSequence是前端传过来的密码，s是数据库中查到的密码
        if (charSequence.toString().equals(s)) {
            return true;
        }
        return false;*/

//        return password.contentEquals(encode(charSequence));
//        return charSequence.toString().contentEquals(encode(password));
        return encode(password).toLowerCase().contentEquals(charSequence);
    }
}
