package azul;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import db.ConnectionFactory;

public class CacheSp {
	@SuppressWarnings("unchecked")
	public static HashMap<String,String> urlMap = null;
	public static HashMap<String,String> cidMap = null;
	public static HashMap<String,Double> rateMap = null;
	@SuppressWarnings("unchecked")
	public static HashMap<String,ArrayList> paramMap = null;
	@SuppressWarnings("unchecked")
	public static void initSpParam() {
		System.out.println("初始化sp工具和对应缓存");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cidMap=new HashMap<String,String>();
			rateMap=new HashMap<String,Double>();
			paramMap = new HashMap<String,ArrayList>();
			conn = ConnectionFactory.getInstance().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from cfg_sp_code order by LENGTH(sp_code) desc");
			while (rs.next()) {
				String sid=rs.getString("sid");
				String sp_code=rs.getString("sp_code").trim().toLowerCase();
				String cid=rs.getString("cid");
				double rate=rs.getDouble("rate");
				common.ObjectVO vo=new common.ObjectVO();
				vo.setObj1(sp_code);
				vo.setObj2(cid);
				vo.setObj3(rate);
				String tempKey=sid+"_"+sp_code;
				cidMap.put(tempKey,cid);
				rateMap.put(tempKey, rate);
				if (paramMap.containsKey(sid)) {
					ArrayList tempList=(ArrayList)paramMap.get(sid);
					tempList.add(vo);
					paramMap.put(sid, tempList);
				}
				else{
					ArrayList tempList=new ArrayList();
					tempList.add(vo);
					paramMap.put(sid, tempList);
				}
			}
			//azul.JspUtil.p(paramMap);
			//azul.JspUtil.p(cidMap);
			//azul.JspUtil.p(rateMap);
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			ConnectionFactory.close(rs, stmt, conn);
		}
	}
	
	//初始化cid同步url
	public static void initCidUrl() {
		System.out.println("初始化cid同步url");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			urlMap=new HashMap<String,String>();
			conn = ConnectionFactory.getInstance().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from cfg_cid_url");
			while (rs.next()) {
				urlMap.put(rs.getString("cid"), rs.getString("url"));
			}
			JspUtil.p(urlMap);
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			ConnectionFactory.close(rs, stmt, conn);
		}
	}
	
	//根据sid，msg直接找到所属厂商
	public static String getCid(String sid,String msg){
		String tempKey=sid+"_"+msg;
		if(cidMap.containsKey(tempKey)){
			return cidMap.get(tempKey);
		}
		else{
			//如果缓存里面没有这个命令就加入到缓存中
			String temp="";
			common.ObjectVO vo=getVo(sid,msg);
			if(vo!=null){
				temp=(String)vo.getObj2();
			}
			//确保没找到也加到缓存中,第二次可以不用再去查找
			cidMap.put(tempKey,temp);
			return temp;
		}
	}
	
	//根据sid和msg直接找到扣量设置
	public static double getRate(String sid,String msg){
		String tempKey=sid+"_"+msg;
		if(rateMap.containsKey(tempKey)){
			return rateMap.get(tempKey);
		}
		else{
			//如果缓存里面没有这个命令就加入到缓存中
			double temp=0;
			common.ObjectVO vo=getVo(sid,msg);
			if(vo!=null){
				temp=(Double)vo.getObj3();
			}
			//确保没找到也加到缓存中,第二次可以不用再去查找
			rateMap.put(tempKey,temp);
			return temp;
		}
	}
	
	//根据cid直接找到同步url
	@SuppressWarnings("unchecked")
	public static String getUrl(String cid){
		if(urlMap.containsKey(cid)){
			return urlMap.get(cid);
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	private static common.ObjectVO getVo(String sid,String msg){
		if(paramMap.containsKey(sid)){
			ArrayList tempList=(ArrayList)paramMap.get(sid);
			for (int i = 0; i < tempList.size(); i++) {
				common.ObjectVO vo=(common.ObjectVO)tempList.get(i);
				String sp_code=(String)vo.getObj1();
				//简单判断会使AD11246归属到AD1命令中，所以需要在list中找到最长的命令来匹配
				//momsg.toLowerCase()兼容大小写问题
				if(msg.trim().toLowerCase().indexOf(sp_code)>-1){
					return vo;
				}
			}
		}
		return null;
	}
}
