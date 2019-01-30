package demo.filter;

import demo.common.model.LoginToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @Description: 自定义filter，用来筛选出来想要的登录方式。
 *
 * @auther: csp
 * @date:  2019/1/7 下午6:27
 *
 */
public class MyTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String SPRING_SECURITY_RESTFUL_TOKEN = "token";

    public static final String SPRING_SECURITY_RESTFUL_LOGIN_URL = "/tokenLogin";
    private boolean postOnly = true;

    // 请求路径声明，url不能被权限拦截。
    // 会根据AntPathRequestMatcher 筛选请求，符合条件的才会认为有效
    public MyTokenAuthenticationFilter() {
        super(new AntPathRequestMatcher(SPRING_SECURITY_RESTFUL_LOGIN_URL, null));
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        if (postOnly && !request.getMethod().equals("POST")) {
//            throw new AuthenticationServiceException(
//                    "Authentication method not supported: " + request.getMethod());
//        }

        AbstractAuthenticationToken authRequest;

        String token = obtainParameter(request, SPRING_SECURITY_RESTFUL_TOKEN);

        authRequest = new LoginToken(token);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        // 根据AuthenticationManager校验具体的请求，实际的登录验证触发。
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private void setDetails(HttpServletRequest request,
                            AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    private String obtainParameter(HttpServletRequest request, String parameter) {
        String result =  request.getParameter(parameter);
        return result == null ? "" : result;
    }
}
