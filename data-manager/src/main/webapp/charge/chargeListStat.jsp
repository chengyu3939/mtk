<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="dao.*" %>
<%@ page import="model.*" %>
<%@ page import="azul.*" %>
<%@ page import="java.util.*" %>
<jsp:include page="../check.jsp?check_role=admin" flush="true" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>后台管理系统</title>
<link rel="stylesheet" href="../_css/back.css" type="text/css"/>
<link href="../_js/jquery.alerts.css" rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript" src="../_js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="../_js/jquery.alerts.js"></script>
</head>
<%
String num=JspUtil.getStr(request.getParameter("num"),"100");
String msg=JspUtil.getStr(request.getParameter("msg"),"");
String sid=JspUtil.getStr(request.getParameter("sid"),"");
String cid=JspUtil.getStr(request.getParameter("cid"),"");
String operator=JspUtil.getStr(request.getParameter("operator"),"");
String fee_type=JspUtil.getStr(request.getParameter("fee_type"),"");
String mobile=JspUtil.getStr(request.getParameter("mobile"),"");
String sort_by= JspUtil.getStr(request.getParameter("sort_by"),"");
String sort_order= JspUtil.getStr(request.getParameter("sort_order"),"");
String startDate=JspUtil.getStr(request.getParameter("startDate"),"");
String endDate= JspUtil.getStr(request.getParameter("endDate"),"");
String[] arr=azul.JspUtil.getDateDay(startDate,endDate);
startDate=arr[0];
endDate=arr[1];
String area=JspUtil.getStr(request.getParameter("area"),"");
String tableName=azul.JspUtil.getTableName("charge",startDate);
StringBuffer paramSB=new StringBuffer();
StringBuffer conditionStrSB=new StringBuffer("select * from "+tableName+" where 1=1");
azul.JspUtil.appendWhere(paramSB,conditionStrSB,"msg",msg,"like");
azul.JspUtil.appendWhere(paramSB,conditionStrSB,"mobile",mobile,"=");
azul.JspUtil.appendWhere(paramSB,conditionStrSB,"cid",cid,"=");
azul.JspUtil.appendWhere(paramSB,conditionStrSB,"sid",sid,"=");
azul.JspUtil.appendWhere(paramSB,conditionStrSB,"operator",operator,"=");
azul.JspUtil.appendWhere(paramSB,conditionStrSB,"fee_type",fee_type,"=");
azul.JspUtil.appendWhere(paramSB,conditionStrSB,"province",area,"=");
azul.JspUtil.appendWhere(paramSB,conditionStrSB,"date_time",startDate,">=");
azul.JspUtil.appendWhere(paramSB,conditionStrSB,"date_time",endDate,"<=");
azul.JspUtil.appendOrder(paramSB,conditionStrSB,sort_by,sort_order,"date_time","desc");
String linkParam=paramSB.toString();
String pageSql=conditionStrSB.toString()+" limit "+num;
//System.out.println("----------------");
//System.out.println(pageSql);
CfgCompanyDao cfgCompanyDao=new CfgCompanyDao();
java.util.Map cidMap=cfgCompanyDao.getSelectMap("select cid,concat(cid,'(',name,')') from cfg_company order by name");
CfgSpDao cfgSpDao=new CfgSpDao();
Map sidMap=cfgSpDao.getSelectMap("select sp_code,concat(sp_code,'(',sp_name,')') from cfg_sp");
ChargeDao chargeDao=new ChargeDao();
ArrayList list=chargeDao.getList(pageSql);
%>
<body>
<form name="mainForm" method="post" style="margin:0;padding:0">
  <table width="100%" class="table_noborder">
<tr>
<td>起始日期&nbsp;&nbsp;
  <input name="startDate" type="text" id="startDate" value="<%=startDate%>" onclick="calendar(this)" style="width:70px" /></td>
<td>截止日期&nbsp;
  <input name="endDate" type="text" id="endDate" value="<%=endDate%>"  onclick="calendar(this)" style="width:70px"/></td>
<td>Msg&nbsp;
  <input name="msg" type="text" id="msg" value="<%=msg%>" style="width:100px"/></td>
<td>手机号码&nbsp;
  <input name="mobile" type="text" id="mobile" value="<%=mobile%>" style="width:100px"/></td>
<td>&nbsp;运营商
  <select name="operator" id="operator">
    <option value="">选择</option>
    <option value="1">移动</option>
    <option value="2">联通</option>
    <option value="3">电信</option>
  </select></td>
</tr>
<tr>
  <td>SP合作商
    <select name="sid" id="sid" style="width:125px">
      <option value="">请选择</option>
    </select></td>
  <td>合作厂商&nbsp;
    <select name="cid" id="cid">
	<option value="">请选择</option>
    </select></td>
  <td>地区信息
  <select name="area" id="area">
    <option value="">请选择</option>
	<%
	for(int i=0;i<common.Constant.AREA.length;i++){
	%>
    <option value="<%=common.Constant.AREA[i]%>"><%=common.Constant.AREA[i]%></option>
    <%
	}
    %>
  </select></td>
  <td>显示条数&nbsp;&nbsp;
    <input name="num" type="text" id="num" value="<%=num%>" style="width:100px"/></td>
  <td><a href="#" onclick="_jsSearch('chargeListStat.jsp')"><img src="../_js/ico/btn_search.gif" border="0" alt="搜索" align="absmiddle"/></a></td>
  </tr>
</table>
<table id="TableColor" width="100%" border="0">
<tr>
<td>LinkId</td>
<td id="js_sort_sid">SID</td>
<td id="js_sort_cid">厂商ID</td>
<td>金额(元)</td>
<td>费用类型</td>
<td>所属运营商</td>
<td id="js_sort_mobile">手机号码</td>
<td id="js_sort_province">省份</td>
<td>市区</td>
<td>命令</td>
<td>端口</td>
<td id="js_sort_date_time">时间</td>
</tr>
<%
int allMoney=0;
for(int i=0;i<list.size();i++){
    Charge charge=(Charge)list.get(i);
    allMoney+=charge.getFee();
	int tempOperator=charge.getOperator();
	String operatorStr="未知";
	if(tempOperator==1){
	     operatorStr="移动";
	}
	else if(tempOperator==2){
		operatorStr="联通";
	}
	String myClass="";
	if(charge.getOk()==0){
		myClass="tdRed";
	}
	else{
		myClass="tdGreen";
	}
%>
<tr>
<td><%=charge.getLinkid()%></td>
<td><%=charge.getSid()%></td>
<td><%=charge.getCid()%></td>
<td><%=charge.getFee()%></td>
<td><%=charge.getFeeType()%></td>
<td><%=operatorStr%></td>
<td><%=charge.getMobile()%></td>
<td><%=charge.getProvince()%></td>
<td><%=charge.getCity()%></td>
<td><%=charge.getMsg()%></td>
<td><%=charge.getSpnum()%></td>
<td class="<%=myClass%>"><%=charge.getDateTime()%></td>
</tr>
<%
}
%>
</table>
</form>
<script type="text/javascript" src="../_js/elemUtil.js"></script>
<script type="text/javascript" src="../_js/meizzDate.js"></script>
<script type="text/javascript" src="../_js/TableSort.js"></script>
<script type="text/javascript" src="../_js/TableColor.js"></script>
<script>
function saveAsExcel(excelTable,fileName){
	var divText=document.getElementById(excelTable).outerHTML;
	var oWin=window.open("","","top=1000,left=2000");
	with(oWin) {
		document.write(divText);
		document.execCommand('Saveas',true,fileName);
	}
}
function mark(){
    location="../wavecom/wavecomAction.jsp?op=areaStat&tableName=charge&srcPage=../charge/chargeListStat.jsp";
}
<%
out.println(JspUtil.initSelect("sid",sidMap,sid));
out.println(JspUtil.initSelect("cid",cidMap,cid));
%>
TableSort.mySort("<%=sort_by%>","<%=sort_order%>","<%=linkParam%>");
initElem("area","<%=area%>");
</script>
</body>
</html>