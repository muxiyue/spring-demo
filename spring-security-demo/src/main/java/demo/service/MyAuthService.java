package demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class MyAuthService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     *
     * @Description: 判断一个请求是否拥有权限。
     *
     * @auther: csp
     * @date:  2019/1/7 下午9:48
     * @param request
     * @param authentication
     * @return: boolean
     *
     */
    public boolean canAccess(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if(principal == null){
            return false;
        }

        if(authentication instanceof AnonymousAuthenticationToken){
            //check if this uri can be access by anonymous
            return false;
        }

        authentication.getAuthorities();
        String uri = request.getRequestURI();
        //check this uri can be access by this role

        // TODO 实际根据权限列表判断。
        log.info("=================== myAuth pass ===================");
        return true;

    }
}