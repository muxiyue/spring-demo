package demo.provider;

import demo.model.LoginToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 *
 * @Description: token验证逻辑
 *
 * @auther: csp
 * @date:  2019/1/7 下午9:05
 *
 */
public class MyTokenProvider implements AuthenticationProvider {

    UserDetailsService userDetailsService;

    public MyTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (authentication.getCredentials() == null) ? "NONE_PROVIDED"
            : (String) authentication.getCredentials();

        // loginToken_user
        // 这个地方简化处理，实际需要校验token，如jwt token 需要解密 验证信息
        if (token.startsWith("loginToken_")) {

            // 验证下token对不对，然后加载下信息。
            String userName = token.split("_")[1];
            UserDetails user = userDetailsService.loadUserByUsername(userName);

            LoginToken result = new LoginToken(token, user.getAuthorities());
            result.setDetails(authentication.getDetails());

            return result;
        }

        throw new BadCredentialsException("token无效");
    }

    /**
     *
     * @Description:  只处理特定类型的登录
     *
     * @auther: csp
     * @date:  2019/1/7 下午9:03
     * @param authenticationClass
     * @return: boolean
     *
     */
    @Override
    public boolean supports(Class<?> authenticationClass) {
        return (LoginToken.class
                .isAssignableFrom(authenticationClass));
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}