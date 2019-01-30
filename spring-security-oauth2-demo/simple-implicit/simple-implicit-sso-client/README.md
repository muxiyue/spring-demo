# 简单的implicit 客户端，实现单点登录功能。
访问页面->未登录（token控制, 修改支撑 token cookie处理）-> 跳转到应用登录页面（替换为security.oauth2.sso.loginPath地址）
-> OAuth2ClientAuthenticationProcessingFilter拦截，触发 请求token逻辑（implicit 重写）-> 跳转到sso 请求 token地址
-> sso 判断未登录（默认 session 控制） -> 跳转到sso登录界面 -> 登录成功，拿到上一次url地址（默认从session中拿），跳转地址 ->
生成token （这里用的是jwt rsa token），跳转到应用url（这个地方，demo中写死为setCookie.html)-> 设置 token cookie
-> 请求其他页面 带 token，自动解析 （这里用的是jwt rsa token）
