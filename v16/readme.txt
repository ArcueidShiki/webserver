独立完成用户登录模块
登录流程:
1:用户在首页上点击超链接来到登录页面:login.html
2:在登录页面上输入用户名和密码并点击登录按钮提交
3:服务端处理登录逻辑，并响应登录结果页面(登录成功或失败)

实现:
1:在static/myweb目录下定义登录业务所需要的页面
  1.1:login.html登录页面，form中action指定值"/myweb/login"
  1.2:login_info_error.html输入信息有误的登录失败提示页面
  1.3:login_fail.html登录失败页面，居中一行字:登录失败，用户名或密码错误
  1.4:login_success.html登录成功提示页面

2:在UserController中添加一个处理登录业务的方法:login
  登录要求:当用户名和密码都正确时响应登录成功。若用户不存在或密码输入错误都响
  应登录失败页面

3:在DispatcherServlet中添加一个else if分支，判断如果请求路径是请求登录的，
  则执行登录业务