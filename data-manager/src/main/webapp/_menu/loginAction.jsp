<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="dao.*"%>
<%@ page import="common.*"%>
<%
String username=azul.JspUtil.getStr(request.getParameter("username"),"");
String password=azul.JspUtil.getStr(request.getParameter("password"),"");
String rember=azul.JspUtil.getStr(request.getParameter("rember"),"");
SysUserDao sysUserDao=new SysUserDao();
String msg=sysUserDao.login(username,password,request);
	System.out.println(msg+">>>>>>>>>>>"+rember);
if("".equals(msg) && "1".equals(rember)){
	//设置cookie
	System.out.println("set cookie");
	MyCookie myCookie=new MyCookie();
	myCookie.setCookie(request,response,username,password,"user");
}
out.print(msg);
%>