package demo.simple.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: csp
 * @Description:
 * @Date: Created in 2019/1/17 下午5:03
 * @Modified By:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    private String accessToken;
    private String refreshToken;
    private UserInfo userInfo;

}
