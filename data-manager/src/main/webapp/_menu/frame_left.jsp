<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="dao.*"%>
<%@ page import="model.*"%>
<%@ page import="java.util.*"%>
<jsp:include page="../check.jsp" flush="true" />
<%
SysUser sysUser=(SysUser)session.getAttribute("sysUser");
SysMenuMainDao sysMenuMainDao=new SysMenuMainDao();
ArrayList menuList=sysMenuMainDao.getList("select * from sys_menu_main order by sort");

SysMenuSubDao sysMenuSubDao=new SysMenuSubDao();
ArrayList subList=sysMenuSubDao.getList("select * from sys_menu_sub order by sys_menu_main_id,sort");
String mainRoleStr="";
String subRoleStr="";
if(sysUser.getUsername().indexOf("admin")==-1){
	SysRoleDao sysRoleDao=new SysRoleDao();
	SysRole sysRole=(SysRole)sysRoleDao.loadBySql("select * from sys_role where sys_user_id=" + sysUser.getSysUserId());
	if(sysRole!=null){
		subRoleStr=","+sysRole.getSysMenuSub()+",";
		mainRoleStr=","+sysMenuMainDao.getValueStr("select distinct(sys_menu_main_id) from sys_menu_sub where sys_menu_sub_id in ("+sysRole.getSysMenuSub()+")",",")+",";
	}
}
//System.out.println(mainRoleStr+"    "+subRoleStr);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>后台管理系统</title>
<link rel="stylesheet" href="../_css/back.css" type="text/css"/>
<link href="../_js/dtree/dtree.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="../_js/dtree/dtree.js"></script>
</head>
<body>
<table width="142" border="0" height="100%">
  <tr>
    <td height="20" bgcolor="#CBE0E4"><strong>系统菜单</strong><a href="javascript:showTree()"><img id="treeBar" align="absmiddle" src="../_js/dtree/img/nolines_plus.gif" width="18" height="18" border="0"/></a></td>
  </tr>
  <tr>
    <td height="100%" valign="top">
<script type="text/javascript">
var d = new dTree('d');
d.add(0,-1,'');  //目录
<%
for(int i=0;i<menuList.size();i++){
	SysMenuMain sysMenuMain=(SysMenuMain)menuList.get(i);
	int sys_menu_main_id = sysMenuMain.getSysMenuMainId();
	if(sysUser.getUsername().indexOf("admin")>-1 || mainRoleStr.indexOf(","+sys_menu_main_id+",")>-1){
    %>
	d.add(<%=i+1%>,0,"<strong><%=sysMenuMain.getName()%></strong>");
	<%
	}
	for(int j=0;j<subList.size();j++){
		SysMenuSub sysMenuSub=(SysMenuSub)subList.get(j);
		int sys_menu_sub_id = sysMenuSub.getSysMenuSubId();
		if(sysMenuSub.getSysMenuMainId()!=sys_menu_main_id){
			continue;
		}
		if(sysUser.getUsername().indexOf("admin")>-1 || subRoleStr.indexOf(","+sys_menu_sub_id+",")>-1){
			String title=sysMenuSub.getTitle();
			int color=sysMenuSub.getColor();
			if(1==color){
				title="<span class='tdRed'>"+title+"</span>";
		    }
		    else if(2==color){
		    	title="<span class='tdOrgen'>"+title+"</span>";
		    }
		    else if(3==color){
		    	title="<span class='tdBlue'>"+title+"</span>";
		    }
		    else if(4==color){
		    	title="<span class='tdGreen'>"+title+"</span>";
		    }
		    %>
				d.add(<%=i+100+j%>,<%=i+1%>,"&nbsp;<%=title%>","javascript:linkToPage('../<%=sysMenuSub.getLinks()%>')");
			<%
		}
	}
}
if(sysUser.getUsername()!=null && sysUser.getUsername().indexOf("admin")>-1){
%>
d.add(9001,0,"<strong>系统管理</strong>");
d.add(9002,9001,"&nbsp;&nbsp;系统权限","javascript:linkToPage('../_menu/sysRoleList.jsp')");
d.add(9003,9001,"&nbsp;&nbsp;菜单管理","javascript:linkToPage('../_menu/sysMenuMainList.jsp')");
d.add(9004,9001,"&nbsp;&nbsp;角色菜单","javascript:linkToPage('../_menu/sysRoleMenuList.jsp')");
d.add(9005,9001,"&nbsp;&nbsp;设置密码","javascript:linkToPage('../_menu/sysUserPassList.jsp')");
d.add(9006,9001,"&nbsp;&nbsp;系统用户","javascript:linkToPage('../_menu/sysUserList.jsp')");
d.add(9007,9001,"&nbsp;&nbsp;操作日志","javascript:linkToPage('../_menu/sysLogsList.jsp')");
d.add(9008,9001,"&nbsp;&nbsp;系统提醒","javascript:linkToPage('../_menu/sysHintList.jsp')");
d.add(9009,9001,"&nbsp;&nbsp;系统预警","javascript:linkToPage('../_menu/sysWarnList.jsp')");
d.add(9010,9001,"&nbsp;&nbsp;系统状态","javascript:linkToPage('../SystemStatus.jsp')");
d.add(9011,9001,"&nbsp;&nbsp;系统缓存","javascript:linkToPage('../SystemCache.jsp')");
d.add(9012,9001,"&nbsp;&nbsp;my_test","javascript:linkToPage('../param/myTestList.jsp')");
<%
}
%>
d.add(10000,0,"&nbsp;&nbsp;<strong><a href='quit.jsp'>退出</a></strong>");
document.write(d);
//-->
</script>
    </td>
  </tr>
</table>
<a href="javascript:endIeStatus();" onclick="window.status=''; return true;" id="endIeStatus" style="display: none;"/>
<script>
var treeFlag=1;
function showTree(){
   var elem=document.getElementById("treeBar");
   if(treeFlag==0){
       treeFlag=1;
       elem.src="../_js/dtree/img/nolines_plus.gif";
	   d.closeAll();
   }
   else{
       treeFlag=0;
       elem.src="../_js/dtree/img/nolines_minus.gif";
	   d.openAll();   
   }
}
function endIeStatus(){}
function linkToPage(url){
   top.rightIfame.location=url;
   document.getElementById('endIeStatus').onclick();
}
</script>
</body>
</html>
