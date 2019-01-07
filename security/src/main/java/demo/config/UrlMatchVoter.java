package demo.config;

import demo.model.UrlGrantedAuthority;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.util.Collection;

public class UrlMatchVoter implements AccessDecisionVoter<Object> {


    @Override public boolean supports(ConfigAttribute attribute) {
            return true;
    }

    @Override public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override public int vote(Authentication authentication, Object object,
        Collection<ConfigAttribute> attributes) {

        if (authentication == null) {
            return ACCESS_DENIED;
        }


        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 请求路径
        String url = getUrl(object);
        // http 方法
        String httpMethod = getMethod(object);

        boolean hasPerm = false;

        for (GrantedAuthority authority : authorities) {
            if (!(authority instanceof UrlGrantedAuthority))
                continue;
            UrlGrantedAuthority urlGrantedAuthority = (UrlGrantedAuthority) authority;
            if (StringUtils.isEmpty(urlGrantedAuthority.getAuthority()))
                continue;
            //如果method为null，则默认为所有类型都支持
            String httpMethod2 = (!StringUtils.isEmpty(urlGrantedAuthority.getHttpMethod())) ?
                urlGrantedAuthority.getHttpMethod() :
                httpMethod;
            //AntPathRequestMatcher进行匹配，url支持ant风格（如：/user/**）
            AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(urlGrantedAuthority.getAuthority(),
                httpMethod2);
            if (antPathRequestMatcher.matches(((FilterInvocation) object).getRequest())) {
                hasPerm = true;
                break;
            }
        }

        if (!hasPerm) {
            return ACCESS_DENIED;
        }

        return ACCESS_GRANTED;
    }

    /**
     * 获取请求中的url
     */
    private String getUrl(Object o) {
        //获取当前访问url
        String url = ((FilterInvocation) o).getRequestUrl();
        int firstQuestionMarkIndex = url.indexOf("?");
        if (firstQuestionMarkIndex != -1) {
            return url.substring(0, firstQuestionMarkIndex);
        }
        return url;
    }

    private String getMethod(Object o) {
        return ((FilterInvocation) o).getRequest().getMethod();
    }
}