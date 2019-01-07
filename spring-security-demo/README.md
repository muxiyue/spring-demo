spring security demo
# 简介：
> spring security主要分为两部分，认证(authentication)和授权(authority)。

> 认证：

[认证说明链接](https://blog.csdn.net/qq_30062125/article/details/86031593)

1. 用户名密码登录：

http://127.0.0.1:9999/

admin 123456
user 123456

2. token登录：

user登录：
http://127.0.0.1:9999/tokenLogin?token=loginToken_user
admin登录：
http://127.0.0.1:9999/tokenLogin?token=loginToken_admin


> 授权：

[授权说明链接](https://blog.csdn.net/qq_30062125/article/details/86031713)

> 自定义AuthenticationProvider，一个authenticationManager包含多个AuthenticationProvider。通过supports方法控制请求是否走到该逻辑。

user 通过角色授权（ROLE）
admin 通过自定义投票项UrlMatchVoter授权
