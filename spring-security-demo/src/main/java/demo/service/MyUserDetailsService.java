package demo.service;

import demo.model.UrlGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component public class MyUserDetailsService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("用户的用户名: {}", username);
        // TODO 根据用户名，查找到对应的密码，与权限



        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();


        if ("admin".equals(username)) {
            UrlGrantedAuthority authority = new UrlGrantedAuthority(null, "/index");
            list.add(authority);
            // 封装用户信息，并返回。参数分别是：用户名，密码，用户权限
            User user = new User(username, "123456", list);

            return user;
        }
        else if ("user".equals(username)) {
            list.add(new SimpleGrantedAuthority("ROLE_USER"));
            User user = new User(username, "123456", list);

            return user;
        }
        else {
            throw new DisabledException("用户或密码错误");
        }

    }
}