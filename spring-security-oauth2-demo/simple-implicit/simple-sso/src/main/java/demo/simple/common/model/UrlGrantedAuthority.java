package demo.simple.common.model;

import org.springframework.security.core.GrantedAuthority;

public class UrlGrantedAuthority implements GrantedAuthority {

    private final String httpMethod;

    private final String url;

    public UrlGrantedAuthority(String httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }

    @Override
    public String getAuthority() {
        return url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlGrantedAuthority target = (UrlGrantedAuthority) o;
        if (httpMethod.equals(target.getHttpMethod()) && url.equals(target.getUrl())) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int result = httpMethod != null ? httpMethod.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}