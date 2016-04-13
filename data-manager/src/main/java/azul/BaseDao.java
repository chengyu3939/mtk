package azul;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.SysUser;

import common.ObjectVO;

import db.ConnectionFactory;

public class BaseDao {

    private static final ThreadLocal<HttpServletRequest> context = new ThreadLocal<HttpServletRequest>();

    public static void setContext(HttpServletRequest request) {
        context.set(request);
    }

    public static HttpServletRequest getContext() {
        return context.get();
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public String baseModelName = "";
    public String baseTableName = "";
    private static String PACKEG_NAME = "model.";
    public static boolean AUTO_PRINT = false;
    public static void setAutoPrint(boolean autoPrint){
        AUTO_PRINT=autoPrint;
    }
    @SuppressWarnings("unchecked")
    public void init() {
        // 前2位是Thread和BaseDao,所以从第三位开始
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stack.length; i++) {
            StackTraceElement ste = stack[i];
            baseModelName = ste.getFileName();
            //如果tomcat报tableName=  modelName= 1.没有在XxxDao中调用init();方法 2.数据库结构和表结构不对应
            if (baseModelName.indexOf("Dao.java") > -1) {
                baseModelName = baseModelName.replace("Dao.java", "");
                break;
            }
        }
        if (CacheSystem.tableMap.containsKey(baseModelName)) {
            baseTableName = (String) CacheSystem.tableMap.get(baseModelName);
        }
    }

    @SuppressWarnings("unchecked")
    public int executeUpdate(String sql) {
        int result = 0;
        Connection conn = null;
        Statement stmt = null;
        try {
            printSql(sql);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);
            if (result > 0) {
                //如果不是页面操作,此时得不到操作session,不记录操作日志
                HttpServletRequest request = getContext();
                if(request==null){
                    return result;
                }
                HttpSession session=request.getSession();
                if (session != null && session.getAttribute("sysUser") != null && sql.indexOf("sys_logs") == -1) {
                    SysUser sysUser = (SysUser) session.getAttribute("sysUser");
                    int sys_user_id = sysUser.getSysUserId();
                    String act = "";
                    sql = sql.toLowerCase();
                    if (sql.indexOf("insert") > -1) {
                        act = "ADD";
                    } else if ((sql.indexOf("update") > -1)) {
                        act = "EDIT";
                    } else if ((sql.indexOf("delete") > -1)) {
                        act = "DELETE";
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append("insert into sys_logs (sys_user_id,ip,act_type,act) values (");
                    sb.append(sys_user_id);
                    sb.append(",'");
                    sb.append(request.getRemoteAddr());
                    sb.append("','");
                    sb.append(act);
                    sb.append("','");
                    sb.append(sql.replace("'", "\\'"));
                    sb.append("')");
                    stmt.executeUpdate(sb.toString());
                    clearCache(baseTableName);
                }
            }
        } catch (Exception e) {
            System.err.println("--------------error sql--------------");
            System.err.println(sql);
        } finally {
            ConnectionFactory.close(stmt,conn);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Object> getList() {
        ArrayList<Object> list = new ArrayList<Object>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String pageSql = "select * from " + baseTableName;
        try {
            printSql(pageSql);
            Class cls = Class.forName(PACKEG_NAME + baseModelName);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            while (rs.next()) {
                Object obj = cls.newInstance();
                setMethodValue(obj, rs);
                list.add(obj);
            }
        } catch (SQLException e) {
            System.err.println("------------BaseDao.getList() exception:"
                    + sdf.format(new Date()));
            System.err.println("tableName=" + baseTableName + "  modelName="
                    + baseModelName);
            System.err.println(pageSql);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Object> getList(String pageSql) {
        ArrayList<Object> list = new ArrayList<Object>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            printSql(pageSql);
            Class cls = Class.forName(PACKEG_NAME + baseModelName);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            while (rs.next()) {
                Object obj = cls.newInstance();
                setMethodValue(obj, rs);
                list.add(obj);
            }
        } catch (SQLException ex) {
            System.err
                    .println("-------------BaseDao.getList(String) exception:"
                            + sdf.format(new Date()));
            System.err.println("tableName=" + baseTableName + "  modelName="
                    + baseModelName);
            System.err.println(pageSql);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Object> getList(int pageIndex, int size, String pageSql) {
        ArrayList<Object> list = new ArrayList<Object>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer("");
        try {
            sb.append(pageSql).append(" limit ").append((pageIndex - 1) * size)
                    .append(",").append(size);
            printSql(sb.toString());
            Class cls = Class.forName(PACKEG_NAME + baseModelName);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                Object obj = cls.newInstance();
                setMethodValue(obj, rs);
                list.add(obj);
            }
        } catch (Exception e) {
            System.err
                    .println("-------------BaseDao.getList(int,int,String)  exception:"
                            + sdf.format(new Date()));
            System.err.println("tableName=" + baseTableName + "  modelName="
                    + baseModelName);
            System.err.println(sb.toString());
            try {
                throw new Exception(e);
            } catch (Exception e1) {
            }
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return list;
    }

    /*
     * sql="select teacher_id,nickname,count(teacher_id) as num from vote";
     * List tempList=voteDao.getObject(sql);
     * int tempTeacherId=(Integer)tempList.get(0);
     * String nickName=""+tempList.get(1);
     * long num=(Long)tempList.get(2); }
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Object> getObject(String pageSql) {
        ArrayList<Object> result = new ArrayList<Object>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            printSql(pageSql);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            ResultSetMetaData meta = rs.getMetaData();
            if (rs.next()) {

                for (int i = 0; i < meta.getColumnCount(); i++) {
                    Object obj = rs.getObject(i + 1);
                    if (obj != null) {
                        if (obj.toString().equals("true")) {
                            result.add(1);
                        } else if (obj.toString().equals("false")) {
                            result.add(0);
                        } else {
                            result.add(obj);
                        }
                    } else {
                        result.add(null);
                    }
                }

            }

        } catch (SQLException e) {
            System.err.println("-------------BaseDao.getObject(String) exception:"+ sdf.format(new Date()));
            System.err.println(pageSql);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return result;
    }

    /*
     * sql="select teacher_id,nickname,count(teacher_id) as num from vote"; List
     * voteList=(ArrayList)voteDao.getObjectList(sql);
     * for(int i=0;i<voteList.size();i++){
     * List tempList=(List)voteList.get(i);
     * int tempTeacherId=(Integer)tempList.get(0); String
     * nickName=""+tempList.get(1); long num=(Long)tempList.get(2); }
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ArrayList> getObjectList(String pageSql) {
        ArrayList<ArrayList> list = new ArrayList<ArrayList>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            printSql(pageSql);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                ArrayList<Object> tempList = new ArrayList<Object>();
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    Object obj = rs.getObject(i + 1);
                    if (obj != null) {
                        if (obj.toString().equals("true")) {
                            tempList.add(1);
                        } else if (obj.toString().equals("false")) {
                            tempList.add(0);
                        } else {
                            tempList.add(obj);
                        }
                    } else {
                        tempList.add(null);
                    }
                }
                list.add(tempList);
            }
        } catch (SQLException e) {
            System.err
                    .println("-------------BaseDao.getObjectList(String) exception:"
                            + sdf.format(new Date()));
            System.err.println(pageSql);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ArrayList> getObjectList(int pageIndex, int size, String pageSql) {
        pageSql = pageSql + " limit " + (pageIndex - 1) * size + "," + size;
        ArrayList<ArrayList> list = new ArrayList<ArrayList>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            printSql(pageSql);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                ArrayList<Object> tempList = new ArrayList<Object>();
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    Object obj = rs.getObject(i + 1);
                    if (obj != null) {
                        if (obj.toString().equals("true")) {
                            tempList.add(1);
                        } else if (obj.toString().equals("false")) {
                            tempList.add(0);
                        } else {
                            tempList.add(obj);
                        }
                    } else {
                        tempList.add(null);
                    }
                }
                list.add(tempList);
            }
        } catch (SQLException e) {
            System.err.println("-------------BaseDao.getObjectList(int,int,String) exception:"+ sdf.format(new Date()));
            System.err.println("pageIndex:" + pageIndex + "  size:" + size);
            System.err.println(pageSql);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public Object loadBySql(String pageSql) {
        Object obj = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            printSql(pageSql);
            Class cls = Class.forName(PACKEG_NAME + baseModelName);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            if (rs.next()) {
                obj = cls.newInstance();
                setMethodValue(obj, rs);
            }
        } catch (Exception e) {
            System.err.println("-------------BaseDao.loadBySql(String) exception:"+ sdf.format(new Date()));
            System.err.println("tableName=" + baseTableName
                    + "   baseModelName=" + baseModelName);
            System.err.println(pageSql);
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    public Object getById(int id) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Object obj = null;
        StringBuffer pageSql = new StringBuffer("");
        try {
            String indexName = CacheSystem.indexMap.get(baseTableName);
            pageSql.append("select * from ").append(baseTableName).append(
                    " where ").append(indexName).append("=").append(id);
            printSql(pageSql.toString());
            Class cls = Class.forName(PACKEG_NAME + baseModelName);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql.toString());
            if (rs.next()) {
                obj = cls.newInstance();
                setMethodValue(obj, rs);
            }
        } catch (SQLException e) {
            System.err.println("-------------BaseDao.getById(int) exception:"
                    + sdf.format(new Date()));
            System.err.println("tableName=" + baseTableName
                    + "   baseModelName=" + baseModelName + "   id=" + id);
            System.err.println(pageSql);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return obj;
    }

    public Object getValue(String pageSql) {
        Object obj = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            printSql(pageSql);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            while (rs.next()) {
                obj = rs.getObject(1);
            }
        } catch (SQLException e) {
            System.err
                    .println("-------------BaseDao.getValue(String) exception:"
                            + sdf.format(new Date()));
            System.err.println(pageSql);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getValueList(String pageSql) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            printSql(pageSql);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.err
                    .println("-------------BaseDao.getValueList(String) exception:"
                            + sdf.format(new Date()));
            System.err.println(pageSql);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return list;
    }

    //得到表中所有记录的具体值,并以特定符号相连
    @SuppressWarnings("unchecked")
    public String getValueStr(String pageSql, String key) {
        String result = "";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            printSql(pageSql);
            StringBuffer sb = new StringBuffer();
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            while (rs.next()) {
                sb.append(rs.getString(1));
                sb.append(key);
            }
            result = sb.toString();
            if (result.length() > 0) {
                result = result.substring(0, result.length() - key.length());
            }
        } catch (SQLException e) {
            System.err
                    .println("-------------BaseDao.getValueList(String) exception:"
                            + sdf.format(new Date()));
            System.err.println(pageSql);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public String add(Object obj) {
        String result = "-1";
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("insert into ");
            sb.append(baseTableName);
            sb.append(" (");
            StringBuffer sbName = new StringBuffer();
            StringBuffer sbValue = new StringBuffer();
            ArrayList<ObjectVO> list =  CacheSystem.paramMap.get(baseTableName);
            for (int i = 1; i < list.size(); i++) {
                ObjectVO vo = list.get(i);
                String name = (String) vo.getObj1();
                Class type = (Class) vo.getObj2();
                Method getMethod = (Method) vo.getObj4();
                Object temp = getMethod.invoke(obj, new Object[]{});
                if (temp == null && !"date_time".equals(name)) {
                    continue;
                }
                sbName.append(name);
                sbName.append(",");
                if (type == Integer.class || type == Double.class) {
                    sbValue.append(temp);
                } else {
                    if ("date_time".equals(name)) {
                        sbValue.append("now()");
                        ;
                    } else {
                        sbValue.append("'");
                        sbValue.append(temp);
                        sbValue.append("'");
                    }
                }
                sbValue.append(",");
            }
            // 由于最后一个属性有可能为空所以不能以是否是最后属性来截取","号
            sb.append(sbName.substring(0, sbName.length() - 1));
            sb.append(") values (");
            sb.append(sbValue.substring(0, sbValue.length() - 1));
            sb.append(")");
            int k = executeUpdate(sb.toString());
            if (k == 1) {
                result = "1";
            } else {
                System.err.println("BaseDao.add(Class) error");
                System.err.println("tableName=" + baseTableName);
            }
        } catch (NullPointerException ex) {
            System.err.println("-------------BaseDao.add(Class) exception:"
                    + sdf.format(new Date()));
            System.err.println("tableName=" + baseTableName);
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public String edit(Object obj) {
        String result = "-1";
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("update ");
            sb.append(baseTableName);
            sb.append(" set ");
            StringBuffer setValue = new StringBuffer();
            StringBuffer whereValue = new StringBuffer();
            ArrayList<ObjectVO> list = (ArrayList<ObjectVO>) CacheSystem.paramMap.get(baseTableName);
            for (int i = 0; i < list.size(); i++) {
                ObjectVO vo = list.get(i);
                String name = (String) vo.getObj1();
                Class type = (Class) vo.getObj2();
                Method getMethod = (Method) vo.getObj4();
                Object temp = getMethod.invoke(obj, new Object[]{});
                if (temp == null) {
                    continue;
                }
                if (i == 0) {
                    whereValue.append(" where ");
                    whereValue.append(name);
                    whereValue.append("=");
                    whereValue.append(temp);
                } else {
                    setValue.append(name);
                    setValue.append("=");
                    if (type == Integer.class) {
                        setValue.append(temp);
                    } else if (type == Double.class) {
                        setValue.append(temp);
                    } else {
                        setValue.append("'");
                        setValue.append(temp);
                        setValue.append("'");
                    }
                    setValue.append(",");
                }
            }
            sb.append(setValue.substring(0, setValue.length() - 1)).append(
                    whereValue);
            int k = executeUpdate(sb.toString());
            if (k == 1) {
                result = "1";
            }
        } catch (StringIndexOutOfBoundsException ex) {
            System.out
                    .println("BaseDao.edit(Class) StringIndexOutOfBoundsException");
        } catch (ClassCastException ex) {
            System.err
                    .println("-------------BaseDao.edit(Class) null point exception error");
            System.err.println("tableName=" + baseTableName);
        } catch (Exception e) {
            System.err.println("-------------BaseDao.edit(Class) error");
            System.err.println("tableName=" + baseTableName);
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public String delete(int id) {
        String result = "-1";
        StringBuffer pageSql = new StringBuffer("");
        try {
            String tableKey = CacheSystem.indexMap.get(baseTableName);
            pageSql.append("delete from ").append(baseTableName).append(
                    " where ").append(tableKey).append("=").append(id);
            int k = executeUpdate(pageSql.toString());
            if (k == 1) {
                result = "1";
            }
        } catch (Exception e) {
            System.err.println("-------------BaseDao.delete(int) exception:"
                    + sdf.format(new Date()));
            System.err.println("tableName=" + baseTableName + " id=" + id);
            System.err.println(pageSql);
        }
        return result;
    }

    @SuppressWarnings( { "unused", "unchecked" })
    public String delete(String idStr) {
        if ("".equals(idStr)) {
            return "-1";
        }
        String result = "-1";
        StringBuffer pageSql = new StringBuffer("delete from ");
        pageSql.append(baseTableName);
        try {
            String tableKey = CacheSystem.indexMap.get(baseTableName);
            if (idStr.indexOf(",") == -1) {
                if (idStr.indexOf("and") > 3) {
                    pageSql.append(" where 1=1 ").append(idStr);
                } else {
                    pageSql.append(" where 1=1 and ").append(idStr);
                }
            } else {
                pageSql.append(" where ").append(tableKey).append("in (")
                        .append(idStr).append(")");
            }
            int k = executeUpdate(pageSql.toString());
            if (k == 1) {
                result = "1";
            }
        } catch (Exception e) {
            System.err.println("-------------BaseDao.delete(String) exception:"
                    + sdf.format(new Date()));
            System.err.println("tableName=" + baseTableName + "   idStr="
                    + idStr);
            System.err.println(pageSql);
        }
        return result;
    }

    // String sql="select max(sort) from product where classid=2"
    public int getNextSort(String pageSql, int size) {
        int result = 1;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            printSql(pageSql);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err
                    .println("-------------BaseDao.getNextSort(Strin,int) exception:"
                            + sdf.format(new Date()));
            System.err.println("pageSql=" + pageSql + "   size=" + size);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return result + size;
    }

    public int getRecordCount(String pageSql) {
        int result = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer("");
        try {
            String tmpSQL = pageSql.toLowerCase();
            int i = tmpSQL.indexOf("group by");
            if (i == -1) {
                i = tmpSQL.indexOf(" order by ");
                if (i > 0) {
                    pageSql = pageSql.substring(0, i);
                }
                i = tmpSQL.indexOf(" from ");
                sb.append("select count(*) ").append(pageSql.substring(i));
            } else {
                sb.append("select count(*) from (").append(tmpSQL).append(
                        ") tab");
            }
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            printSql(sb.toString());
            rs = stmt.executeQuery(sb.toString());
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err
                    .println("-------------BaseDao.getRecordCount(String) exception:"
                            + sdf.format(new Date()));
            System.err.println(pageSql);
            System.err.println(sb.toString());
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return result;
    }

    // 取得对象的普通map key是id value是对象
    @SuppressWarnings("unchecked")
    public HashMap<Object,Object> getMap() throws ClassNotFoundException {
        HashMap<Object,Object> result = new HashMap<Object,Object>();
        try {
            ArrayList list = getList();
            ArrayList volist = CacheSystem.paramMap.get(baseTableName);
            ObjectVO vo = (ObjectVO) volist.get(0);
            Method getMethod = (Method) vo.getObj4();
            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);
                Object idValue = getMethod.invoke(obj, new Object[]{});
                result.put(idValue, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 取得只有2个属性的对象map key是第一属性 value是第二属性
    @SuppressWarnings("unchecked")
    public HashMap<Object,Object> getSelectMap() {
        HashMap<Object,Object> result = new HashMap<Object,Object>();
        try {
            ArrayList list = getList();
            ArrayList volist = CacheSystem.paramMap.get(baseTableName);
            ObjectVO voA = (ObjectVO) volist.get(0);
            Method getMethodA = (Method) voA.getObj4();
            ObjectVO voB = (ObjectVO) volist.get(1);
            Method getMethodB = (Method) voB.getObj4();
            // System.out.println(getMethodA+" "+getMethodB);
            for (int i = 0; i < list.size(); i++) {
                Object obj = (Object) list.get(i);
                Object idValueA = getMethodA.invoke(obj, new Object[]{});
                Object idValueB = getMethodB.invoke(obj, new Object[]{});
                result.put(idValueA, idValueB);
                // System.out.println(idValueA+" "+idValueB);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 取得sql语句只有2个属性的对象map key是第一属性 value是第二属性
    @SuppressWarnings("unchecked")
    public HashMap<String,String> getSelectMap(String pageSql) {
        HashMap<String,String> map = new HashMap<String,String>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            printSql(pageSql);
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(pageSql);
            while (rs.next()) {
                map.put(rs.getString(1), rs.getString(2));
            }
        } catch (Exception e) {
            System.err
                    .println("-------------BaseDao.getSelectMap(String) exception:"
                            + sdf.format(new Date()));
            System.err.println("wrong sql:" + pageSql);
        } finally {
            ConnectionFactory.close(rs,stmt,conn);
        }
        return map;
    }

    public String execute(ArrayList<String> list) {
        String msg = "";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            for (int j = 0; j < list.size(); j++) {
                msg = list.get(j);
                stmt.addBatch(msg);
            }
            int[] resultNum = stmt.executeBatch();
            if (resultNum != null && resultNum.length > 0) {
                for (int j = 0; j < list.size(); j++) {
                    clearCacheBySql(list.get(j));
                }
                msg = "1";
            }
        } catch (Exception e) {
            System.err.println("-------------BaseDao.execute(List) exception:"
                    + sdf.format(new Date()));
            System.out.println(msg);
        } finally {
            ConnectionFactory.close(stmt,conn);
        }
        return msg;
    }

    //baseTableName=null 和baseModelName=null,注意看tomcat启动时候报错信息
    // 如果代码报错java.lang.IllegalArgumentException: argument type mismatch
    // 一般情况下是数据库字段设置类型为datetime，此处为了处理方便datetime对应类型为string
    // 当调用setDateTime(String)时,rs.getObject()得到的对象传入invoke会报这个错，解决方法是数据库表改成varchar
    // 报Column 'fee_stat_null_id' not found,检查sql语句是否没有选择这个字段
    @SuppressWarnings("unchecked")
    private void setMethodValue(Object obj, ResultSet rs) {
        Object value = null;
        Method setMethod = null;
        try {
            ArrayList<ObjectVO> list = CacheSystem.paramMap.get(baseTableName);
            for (int i = 0; i < list.size(); i++) {
                ObjectVO vo = list.get(i);
                Class fieldTypeClass = (Class) vo.getObj2();
                if (fieldTypeClass == String.class) {
                    String temp = rs.getString((String) vo.getObj1());
                    value = temp == null ? "" : temp;
                } else if (fieldTypeClass == Integer.class) {
                    value = rs.getInt((String) vo.getObj1());
                } else if (fieldTypeClass == Long.class) {
                    value = rs.getLong((String) vo.getObj1());
                } else if (fieldTypeClass == Double.class) {
                    value = rs.getDouble((String) vo.getObj1());
                }
                // System.out.println((String)vo.getObj1()+" "+value);
                setMethod = (Method) vo.getObj3();
                setMethod.invoke(obj, new Object[] { value });
            }
        } catch (Exception e) {
            System.err.println("-------------BaseDao.setMethodValue exception");
            System.err.println("table:" + baseTableName + " method:"
                    + setMethod + " value:" + value);
            System.out.println(e);
        }
    }

    private void printSql(String sql) {
        if (AUTO_PRINT) {
            System.out.println(sql);
        }
    }

    @SuppressWarnings("unchecked")
    public static void clearCache(String key) {
        HashMap map=CacheData.getAllCache();
        Iterator<Entry> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry entry = it.next();
            String name = (String)entry.getKey();
            if (name.indexOf(key) > -1) {
                // System.out.println("clear name:" + name);
                CacheData.setData(name, null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void clearCacheBySql(String sql) {
        sql = sql.toLowerCase();
        String key = "";
        if (sql.indexOf("update") > -1) {
            String[] arr = sql.split(" ");
            key = arr[1];
        } else if (sql.indexOf("delete from") > -1) {
            String[] arr = sql.split(" ");
            key = arr[2];
        }
        HashMap map=CacheData.getAllCache();
        Iterator<Entry> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry entry = it.next();
            String name = (String)entry.getKey();
            if (name.indexOf(key) > -1) {
                // System.out.println("clear name:" + name);
                CacheData.setData(name, null);
            }
        }
    }

    public static void main(String[] args) {
        BaseDao baseDao = new BaseDao();
        baseDao.baseTableName = "cfg_company";
        baseDao.baseModelName = "CfgCompany";
        String sql = "insert into sys_logs (sys_user_id,ip,act_type,act) values ('aa','12','add','sdfsdfsdf')";
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = ConnectionFactory.getInstance().getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println(sql);
        }
    }
}