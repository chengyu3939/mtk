package azul;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.ObjectVO;

import db.ConnectionFactory;

public class CacheSystem extends HttpServlet{
	private static final long serialVersionUID = 2938933502236725206L;
	public static int index=0; 
	private static final String PACKEG_NAME = "model.";
	public static String WEB_PATH="";
	public static String FILE_PATH="";
	public static String ip="";
	public static HashMap<String,String> tableMap=new HashMap<String,String>();
	public static HashMap<String,String> indexMap=new HashMap<String,String>();
	@SuppressWarnings("unchecked")
	public static HashMap<String,ArrayList<ObjectVO>> paramMap=new HashMap<String,ArrayList<ObjectVO>>();
	
	public static LinkedList<String> url_list_sms=new LinkedList<String>();
	public static HashMap<String,String> url_map_sms=new HashMap<String,String>();
	public static LinkedList<String> url_list_demand=new LinkedList<String>();
	public static HashMap<String,String> url_map_demand=new HashMap<String,String>();
	
	public static void setWebPath(String webPath){
		WEB_PATH=webPath;
	}
	public static void setFilePath(String filePath){
		FILE_PATH=filePath;
	}
	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		System.out.println("<-------------初始化系统参数开始------------->");
		super.init(config);
		setWebPath(config.getInitParameter("WEB_INFO"));
		setFilePath(config.getServletContext().getRealPath("/"));
		//预防多个网络连接多个ip
		Enumeration<NetworkInterface> netInterfaces = null;   
		try {   
		    netInterfaces = NetworkInterface.getNetworkInterfaces();   
		    while (netInterfaces.hasMoreElements()) {   
		        NetworkInterface ni = netInterfaces.nextElement();    
		        Enumeration<InetAddress> ips = ni.getInetAddresses();   
		        while (ips.hasMoreElements()) {   
		            ip+=ips.nextElement().getHostAddress()+",";   
		        }   
		    }   
		} catch (Exception e) {   
		    e.printStackTrace();   
		} 
		if(ip.indexOf("192.168.")>-1){
			BaseDao.setAutoPrint(true);
			//初始化表结构缓存
			initTable();
			//初始化sp工具
			CacheSp.initSpParam();
			CacheSp.initCidUrl();
			//初始化厂商缓存信息
			//CacheCompany.initCompany();
			//CacheCompany.initCompanyArea();
			//初始化群发信息

			//初始化测试扣费配置使用的缓存

			//初始化天气信息
			//CacheAd.initWeather();
		}
		//wuzhou init
		else if(ip.indexOf("119.145.9.39")>-1){
			//初始化表结构缓存
			initTable();
			//初始化厂商缓存信息
			//CacheCompany.initCompany();
			//CacheCompany.initCompanyArea();
			//初始化群发信息

			//初始化黑名单
			//CacheBlack.initBlackList();
			//初始化短信中心号码匹配
			//初始化广告厂商控制

		}
		//test init
		else if(ip.indexOf("121.14.118.209")>-1){
			//初始化表结构缓存
			initTable();
			//初始化厂商缓存信息
			//CacheCompany.initCompany();
			//CacheCompany.initCompanyArea();
			//初始化黑名单
			//CacheBlack.initBlackList();
			//初始化短信中心号码匹配
			//CacheFee.initSmsCenter();
			//初始化扣费配置使用的缓存
			//CacheFee.initFeeDemand();
			//初始化包月扣费配置使用的缓存
			//CacheFee.initFeeAnchor();
			//初始化提示时间使用的缓存
			//CacheFee.initFeeTime();
			//初始化测试扣费配置使用的缓存
			//CacheFee.initFeeTest();
			//初始化广告厂商控制
			//CacheAd.initAdSet();
			//初始化天气信息
			//CacheAd.initWeather();
		}
		else if(ip.indexOf("119.147.23.190")>-1){
			//初始化表结构缓存
			initTable();
			//初始化厂商缓存信息
			//CacheCompany.initCompany();
			//初始化黑名单
			//CacheBlack.initBlackList();
			//初始化短信中心号码匹配

			//初始化包月扣费配置使用的缓存
			//CacheFee.initFeeAnchor();
		}
		//sp init
		else if(ip.indexOf("119.147.23.178")>-1 || ip.indexOf("211.154.134.148")>-1){
			//初始化表结构缓存
			initTable();
			//初始化sp工具
			CacheSp.initSpParam();
			CacheSp.initCidUrl();
			//初始化厂商缓存信息
			//CacheCompany.initCompany();
			//初始化黑名单
			//CacheBlack.initBlackList();
		}
		else{
			System.out.println("ip检测失败:---------------------------"+ip);
			return;
		}
		System.out.println("<=============初始化系统参数成功==============>");
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	public void destroy() {

	}
	
	@SuppressWarnings("unchecked")
	public void initTable(){
		Connection connA = null;
		Statement stmtA = null;
		ResultSet rsA = null;
		String table_name="";
		ArrayList<String> tableNameList=new ArrayList<String>();
		try {
			//初始化系统数据库表名称和字段
			connA = ConnectionFactory.getInstance().getConnection();
			stmtA = connA.createStatement();
			rsA = stmtA.executeQuery("show tables");
			while (rsA.next()) {
				table_name = rsA.getString(1).toLowerCase();
				tableNameList.add(table_name);
			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			ConnectionFactory.close(rsA,stmtA,connA);
		}
		Connection connB = null;
		Statement stmtB = null;
		ResultSet rsB = null;
		try{
			connB = ConnectionFactory.getInstance().getConnection();
			stmtB = connB.createStatement();
			for (int i = 0; i < tableNameList.size(); i++) {
				table_name=tableNameList.get(i);
				String model_name = JspUtil.upCaseFirst(JspUtil.chargeStr(table_name));
				Class clz =null;
				try {
					clz = Class.forName(PACKEG_NAME + model_name);
				} catch (Exception e) {
					continue;
				}
				tableMap.put(model_name, table_name);
				ArrayList<ObjectVO> voList=new ArrayList<ObjectVO>();
				rsB = stmtB.executeQuery("select * from "+table_name+" where 1=-1");
				ResultSetMetaData meta = rsB.getMetaData();
				//表主键缓存
				indexMap.put(table_name, meta.getColumnName(1));
				for (int j = 1; j < meta.getColumnCount() + 1; j++) {
					String name = meta.getColumnName(j);
					String type = meta.getColumnTypeName(j);
					String paramName=JspUtil.upCaseFirst(JspUtil.chargeStr(name));
					Class paramClz = getColType(type);
					Method setMethod = clz.getMethod("set" + paramName,new Class[] { paramClz });
					Method getMethod = clz.getMethod("get" + paramName, null);
					ObjectVO vo=new ObjectVO();
					vo.setObj1(name);
					vo.setObj2(paramClz);
					vo.setObj3(setMethod);
					vo.setObj4(getMethod);
					voList.add(vo);
				}
				paramMap.put(table_name, voList);
			}
			System.out.println("系统参数缓存成功");
		} catch (SQLException ex){
			System.err.println("<===================================>");
			System.err.println("CacheSystem.initTable:"+table_name);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionFactory.close(rsB,stmtB,connB);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Class getColType(String fileType) {
		if (fileType.indexOf("BIGINT") > -1) {
			return Long.class;
		} else if (fileType.indexOf("INT") > -1) {
			return Integer.class;
		} else if (fileType.indexOf("DOUBLE") > -1) {
			return Double.class;
		/*
		} else if (fileType.indexOf("DATE") > -1) {
			return java.util.Date.class;	
		*/
		} else {
			return String.class;
		}
	}
}