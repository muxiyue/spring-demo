package demo.simple.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: csp
 * @Description:
 * @Date: Created in 2019/1/18 下午1:39
 * @Modified By:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private String userId;

    private String userName;


    public static UserInfo createToken(MyUser user) {

        return new UserInfo(user.getUserId(), user.getUsername());
    }


}
