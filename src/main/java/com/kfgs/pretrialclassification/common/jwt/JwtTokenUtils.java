package com.kfgs.pretrialclassification.common.jwt;

import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.ifsaid.com
 * <p>
 * jwt 工具类
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 * @copyright 2019 http://www.ifsaid.com/ Inc. All rights reserved.
 */


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
@Slf4j
public class JwtTokenUtils implements Serializable {

    private static final String CLAIM_KEY_USER_ACCOUNT = "sub";

    private static final String CLAIM_KEY_CREATED = "created";

    /**
     * @description: 秘钥
     * @date: 2019/12/11 21:53
     */
    private String secret;

    /**
     * @description: 过期时间
     * @date: 2019/12/11 21:53
     */
    private Long expiration;

    private String tokenHeader;

    private String tokenHead;

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 生成令牌
     *
     * @param userDetails 用户
     * @return 令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date());
        return generateToken(claims);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put("created", new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 刷新令牌
     *
     * @param completeToken 截取完整的token，根据前缀 "Bearer "开头
     * @return 新令牌
     */
    public String interceptCompleteToken(String completeToken) {
        String authToken = completeToken.substring(this.getTokenHead().length());
        return authToken;
    }


    /**
     * 验证令牌
     *
     * @param token       令牌
     * @param userDetails 用户
     * @return 是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        FenleiBaohuUserinfo user = null;
        String username = null;
        try{
            user = (FenleiBaohuUserinfo) userDetails;
            username = getUsernameFromToken(token);
            if( null == username ){
                return false;
            }else{
                return (username.equals(user.getLoginname()) && !isTokenExpired(token));
            }
        }catch (Exception e){
            log.error("JwtTokenUtils中validateToken发生异常，当前值：token="+token+",userDetails="+userDetails.toString()
                    + ",user="+user+",username = " + username);
            e.printStackTrace();
            return false;
        }

    }

}
