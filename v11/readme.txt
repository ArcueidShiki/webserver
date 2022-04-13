导入学子商城webapp资源后访问其首页,发现页面无法正常显示.
浏览器F12跟踪请求和响应的交互发现两个问题:
1:我们仅发送了两个响应头(Content-Length和Content-Type).
  虽然目前仅需要这两个头,但是服务端实际可以根据处理情况设置需要发送其他响应头
2:Content-Type的值是固定的"text/html",这导致浏览器请求到该资源后无法正确
  理解该资源因此没有发挥出实际作用.

分两个版本解决这两个问题
此版本解决响应头可根据设置进行发送.

实现:
1:在HttpServletResponse中添加一个Map类型的属性用于保存所有要发送的响应头
  Map<String,String> headers

2:修改发送响应头的方法sendHeaders中的逻辑,将固定发送两个头的操作改为遍历
  headers这个Map,将所有要发送的响应头逐个发送

3:只需要在发送前根据处理情况向headers中put要发送的响应头即可.这个工作需要
  3.1:在响应对象中添加一个方法:addHeader,将要发送的响应头存入headers中
  3.2:在DispatcherServlet处理请求环节调用addHeader存放要发送的响应头即可
