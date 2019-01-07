package demo.config;

import demo.model.UrlGrantedAuthority;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 *
 * @Description: 自定义AccessDecisionManager，通过url和httpmethod拦截权限
 *
 * @auther: csp
 * @date:  2019/1/7 下午9:59
 *
 */
public class UrlMatchAccessDecisionManager implements AccessDecisionManager {


    @Override public boolean supports(ConfigAttribute attribute) {
            return true;
    }

    @Override public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {

        if (authentication == null) {
            throw new AccessDeniedException("无权限！");
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 请求路径
        String url = getUrl(object);
        // http 方法
        String httpMethod = getMethod(object);

        boolean hasPerm = false;

        // request请求路径和httpMethod 和权限列表比对。
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
            throw new AccessDeniedException("无权限！");
        }
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