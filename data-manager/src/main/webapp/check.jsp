<%@ page contentType="text/html;charset=utf-8" %>
<%
String roleMsg="";
if(session.getAttribute("sysUser")==null){
	 roleMsg="登陆超时请重新登陆";
}
else{
	boolean can=false;
	String check_role=azul.JspUtil.getStr(request.getParameter("check_role"),"");
	if(!"".equals(check_role)){
		model.SysUser user=(model.SysUser)session.getAttribute("sysUser");
		String userRole=user.getRole();
		String[] arr=check_role.split(",");
		for(int i=0;i<arr.length;i++){
			if(userRole.equals(arr[i])){
				can=true;
				break;
			}
		}
		if(!can){
			roleMsg="您没有该菜单权限";
		}
	}
}
if(!"".equals(roleMsg)){
	%>
	<script>
	alert("<%=roleMsg%>");
	parent.location="../index.jsp";
	</script>
	<%
	return;
}
%>