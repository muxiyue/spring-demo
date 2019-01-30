package demo.client.implicit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.RequestEnhancer;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Auther: csp
 * @Description:
 * @Date: Created in 2019/1/15 下午9:28
 * @Modified By:
 */
@Component
public class ImplicitUserInfoRestTemplateFactory implements UserInfoRestTemplateFactory {


    @Resource
    @Qualifier("myOAuth2RestTemplate")
    OAuth2RestTemplate oAuth2RestTemplate;


    /**
     * Return the {@link OAuth2RestTemplate} used for extracting user info during
     * authentication if none is available.
     *
     * @return the OAuth2RestTemplate used for authentication
     */
    @Override
    public OAuth2RestTemplate getUserInfoRestTemplate() {
        OAuth2RestTemplate oauth2RestTemplate = oAuth2RestTemplate;
        ImplicitAccessTokenProvider accessTokenProvider = new MyImplicitAccessTokenProvider();
        oauth2RestTemplate.getInterceptors()
            .add(new AcceptJsonRequestInterceptor());
        accessTokenProvider.setTokenRequestEnhancer(new AcceptJsonRequestEnhancer());

        oauth2RestTemplate.setAccessTokenProvider(accessTokenProvider);
        return oauth2RestTemplate;
    }

    static class AcceptJsonRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            return execution.execute(request, body);
        }

    }

    static class AcceptJsonRequestEnhancer implements RequestEnhancer {

        @Override
        public void enhance(AccessTokenRequest request,
            OAuth2ProtectedResourceDetails resource,
            MultiValueMap<String, String> form, HttpHeaders headers) {
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        }

    }



}
