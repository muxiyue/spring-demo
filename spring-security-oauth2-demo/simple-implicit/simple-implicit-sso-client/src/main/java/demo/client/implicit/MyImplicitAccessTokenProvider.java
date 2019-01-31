package demo.client.implicit;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Auther: csp
 * @Description: 自定义implicit处理
 * @Date: Created in 2019/1/15 下午3:14
 * @Modified By:
 */
public class MyImplicitAccessTokenProvider extends ImplicitAccessTokenProvider {

    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest request)
        throws UserRedirectRequiredException, AccessDeniedException, OAuth2AccessDeniedException {

        ImplicitResourceDetails resource = (ImplicitResourceDetails) details;
            // 直接生成到客户端 302 url

            // 追加跳转参数。
            String redirectUri = resource.getRedirectUri(request);
            if (redirectUri == null) {
                throw new IllegalStateException("No redirect URI available in request");
            }

            Map paramMap = request.toSingleValueMap();
            // 交给这个页面设置下 cookie。
            paramMap.put("redirect_uri", redirectUri.replace("/dashboard/login", "/setCookie.html"));
            paramMap.put("response_type", "token");
            paramMap.put("client_id", resource.getClientId());

            if (resource.isScoped()) {

                StringBuilder builder = new StringBuilder();
                List<String> scope = resource.getScope();

                if (scope != null) {
                    Iterator<String> scopeIt = scope.iterator();
                    while (scopeIt.hasNext()) {
                        builder.append(scopeIt.next());
                        if (scopeIt.hasNext()) {
                            builder.append(' ');
                        }
                    }
                }

                paramMap.put("scope", builder.toString());
            }

            // ... but if it doesn't then capture the request parameters for the redirect
            // 最终在 OAuth2ClientContextFilter 中处理跳转。
            throw new UserRedirectRequiredException(resource.getUserAuthorizationUri(), paramMap);

    }
}
