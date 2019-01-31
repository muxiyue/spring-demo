package demo.resource;

import demo.entrypoint.MyOAuth2AccessDeniedHandler;
import demo.entrypoint.MyOAuth2AuthenticationEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * @Auther: csp
 * @Description:
 * @Date: Created in 2019/1/10 下午9:21
 * @Modified By:
 */
@RestController
@Configuration
// 资源api相关
@RequestMapping("/api")
@EnableResourceServer // 默认 order 为3
public class ResourceController {

    private final static Logger log = LoggerFactory.getLogger(ResourceController.class);

    @RequestMapping("/message")
    public Map<String, Object> dashboard() {
        return Collections.<String, Object> singletonMap("message....", "Yay!");
    }

    @Autowired
    private ResourceServerProperties resource;

    @Autowired
    private MyOAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint;


    @Bean
    public ResourceServerConfigurer resourceServer() {
        return new ResourceSecurityConfigurer(this.resource, oAuth2AuthenticationEntryPoint);
    }

    // 重写spring boot 自带的，
    // 实现一些资源自定义处理。
    protected static class ResourceSecurityConfigurer
        extends ResourceServerConfigurerAdapter implements ApplicationContextAware {

        private ResourceServerProperties resource;

        private ConfigurableApplicationContext applicationContext;
        MyOAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint;

        public ResourceSecurityConfigurer(ResourceServerProperties resource, MyOAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint) {
            this.resource = resource;
            this.oAuth2AuthenticationEntryPoint = oAuth2AuthenticationEntryPoint;
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources)
            throws Exception {
            resources.resourceId(this.resource.getResourceId());
            // 使用自带的，支持从cookie header parameter attribute 4中方式获取token。
            resources.tokenExtractor(new MyBearerTokenExtractor());
            // 未登录，直接跳转到 登录页面，走 token 申请逻辑。
            resources.authenticationEntryPoint(oAuth2AuthenticationEntryPoint);
            // 资源403 定制
            resources.accessDeniedHandler(new MyOAuth2AccessDeniedHandler());
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated();
        }

        @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            //将applicationContext转换为ConfigurableApplicationContext
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        }
    }




}
