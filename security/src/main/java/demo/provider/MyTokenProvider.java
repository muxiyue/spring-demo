package demo.provider;

import demo.model.LoginToken;
import demo.model.UrlGrantedAuthority;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

//@Service
public class MyTokenProvider implements AuthenticationProvider {



    @Override public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (authentication.getCredentials() == null) ? "NONE_PROVIDED"
            : authentication.getName();

        if ("loginToken".equals(token)) {
            List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
            UrlGrantedAuthority authority = new UrlGrantedAuthority(null, "/**");
            list.add(authority);

            User user = new User("admin", "123456", list);

            LoginToken result = new LoginToken(token, user.getAuthorities());
            result.setDetails(authentication.getDetails());

            return result;
        }

        throw new BadCredentialsException("token无效");
    }

    @Override
    public boolean supports(Class<?> authenticationClass) {
        return (LoginToken.class
                .isAssignableFrom(authenticationClass));
    }
}