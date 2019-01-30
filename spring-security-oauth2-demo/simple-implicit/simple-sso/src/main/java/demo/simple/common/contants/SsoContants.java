package demo.simple.common.contants;

/**
 * @Auther: csp
 * @Description:
 * @Date: Created in 2019/1/18 下午1:56
 * @Modified By:
 */
public class SsoContants {

    // 默认客户端id
    public final static String DEFAULT_CLIENT_ID = "client_id";

    // 默认客户端密码
    public final static String DEFAULT_CLIENT_SECRET = "client_secret";


    public final static String GRANT_TYPE = "grant_type";

    /**
     *
     * 登录token，用来实现自登陆
     *
     */
    public final static String ACCESS_TOKEN = "accessToken";

    /**
     *
     * 刷新token，换取登录token
     *
     */
    public final static String REFRESH_TOKEN = "refreshToken";

}
