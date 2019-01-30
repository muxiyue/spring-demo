package demo.common.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 *
 * @Description:  声明自定义token，是为后面的AuthenticationProvider提供支撑，区分不同类型的处理。
 *
 * @auther: csp
 * @date:  2019/1/7 下午6:25
 *
 */
public class LoginToken extends AbstractAuthenticationToken {

    private final String token;

    public LoginToken(String token) {
        super(null);
        this.token = token;
    }

    public LoginToken(String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        setAuthenticated(true);
    }


    // 这个地方传递下token，逻辑是简化的逻辑，具体可以根据实际场景处理。
    // 如jwt token，解析出来username等信息，放到该token中。
    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}