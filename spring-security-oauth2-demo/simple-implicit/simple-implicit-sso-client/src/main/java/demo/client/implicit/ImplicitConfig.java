package demo.client.implicit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;

import javax.annotation.Resource;

/**
 * @Auther: csp
 * @Description:
 * @Date: Created in 2019/1/30 上午9:59
 * @Modified By:
 */
@Configuration
public class ImplicitConfig {

    @Resource
    @Qualifier("accessTokenRequest")
    AccessTokenRequest accessTokenRequest;

    // 使用implicit方式
    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ImplicitResourceDetails implicitResourceDetails() {
        return new ImplicitResourceDetails();
    }

    // implicit方式，使用request 作用域的OAuth2ClientContext
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    @Primary
    public OAuth2ClientContext myOAuth2ClientContext() {
        return new DefaultOAuth2ClientContext(accessTokenRequest);
    }

    @Bean
    public OAuth2RestTemplate myOAuth2RestTemplate(@Qualifier("implicitResourceDetails")
        OAuth2ProtectedResourceDetails resource) {
        return new OAuth2RestTemplate(resource, myOAuth2ClientContext());
    }
}
