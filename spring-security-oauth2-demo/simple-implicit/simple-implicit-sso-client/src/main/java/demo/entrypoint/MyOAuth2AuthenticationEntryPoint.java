package demo.entrypoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: csp
 * @Description: resource 资源 访问 失败，直接跳转到登录页
 * @Date: Created in 2019/1/21 下午4:09
 * @Modified By:
 */
@Component
public class MyOAuth2AuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {


    @Autowired
    OAuth2SsoProperties oAuth2SsoProperties;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    protected final Log logger = LogFactory.getLog(this.getClass());


    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException {

        if (authException != null) {
            logger.info("", authException);
        }

        // 直接跳转到触发sso的登录页面。
        logger.debug("Redirecting to DefaultSavedRequest Url: " + oAuth2SsoProperties.getLoginPath());
        redirectStrategy.sendRedirect(request, response, oAuth2SsoProperties.getLoginPath());

    }
}
