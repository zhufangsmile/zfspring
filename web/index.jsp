<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/6/8
  Time: 10:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=utf-8" %>
<html>
<body>
<h2>Hello World!</h2><hr/>
<a href="dispatcherServlet">Get方式请求</a><br/>
<!-- 会到web.xml中交给url-pattern为/aaa/bbb的Servlet来处理，根据get、post的提交方式来执行doGet()、doPost()方法-->
<form action="dispatcherServlet" method="post">
  <input type="submit" value="Post方式请求"/>
</form>
</body>
</html>