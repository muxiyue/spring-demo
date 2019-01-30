package demo.resource;

import demo.contants.SsoContants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: csp
 * @Description:
 * @Date: Created in 2019/1/15 下午9:13
 * @Modified By:
 */
public class MyBearerTokenExtractor extends BearerTokenExtractor {

    private final static Log logger = LogFactory.getLog(MyBearerTokenExtractor.class);

    // 重写，支持多种方式获取 access_token
    @Override
    protected String extractToken(HttpServletRequest request) {

        // 登录接口，跳过token逻辑。
        if (new AntPathRequestMatcher("/login", "POST").matches(request)) {
            return null;
        }

        // first check the header...
        String token = extractHeaderToken(request);

        // bearer type allows a request parameter as well
        if (token == null) {
            logger.debug("Token not found in headers. Trying request parameters.");
            token = request.getParameter(SsoContants.ACCESS_TOKEN);

            if (token == null) {
                logger.debug("Token not found in cookies. Trying request cookies.");

                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (SsoContants.ACCESS_TOKEN.equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }

                if (token == null) {

                    logger.debug("Token not found in request cookies.  Trying request attributes.");
                    Object tokenO = request.getAttribute(SsoContants.ACCESS_TOKEN);
                    if (tokenO == null) {
                        logger.debug("Token not found in request parameters.  Not an OAuth2 request.");
                    }
                    token = (String) tokenO;
                }
                else {
                    request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, OAuth2AccessToken.BEARER_TYPE);
                }
            }
        }

        return token;
    }
}
