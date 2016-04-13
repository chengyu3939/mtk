package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class DateUtil {
    public final static String DATE_A="yyyy-MM-dd";
    public final static String DATE_B="yyyy-MM-dd HH:mm:ss";
    public final static String DATE_C="yyyyMMddHHmmss";
    public final static String DATE_D="yyyyMMdd-HHmmss-SS";
    public final static String DATE_E="M月d日";
    public final static String DATE_F="MM-dd";

    //自动检测字符串形式然后转换
    public static Date strToDate(String dateStr) {
        Date date=null;
        SimpleDateFormat sdf=null;
        if (dateStr == null || dateStr.equals("")) {
            throw new RuntimeException("DateUtil.strToDate():" +dateStr);
        }
        else if(dateStr.indexOf(":")>0){
            sdf = new SimpleDateFormat(DATE_B);
        }
        else if(dateStr.indexOf("-")>0){
            sdf = new SimpleDateFormat(DATE_A);
        }
        try {
            if(sdf!=null){
                date=sdf.parse(dateStr);
            }
        } catch (Exception e) {
            throw new RuntimeException("DateUtil.strToDate():" +dateStr);
        }
        return date;
    }

    //特殊的日期格式转换
    public static Date strToDate(String dateStr, String dateFormat) {
        if (dateStr == null || dateStr.equals("")) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("DateUtil.strToDate():" + e.getMessage());
        }
    }

    //普通的当前时间转字符串方法，格式为yyyy-MM-dd
    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_A);
        return sdf.format(new Date());
    }

    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_B);
        return sdf.format(new Date());
    }

    //普通的时间转字符串方法
    public static String getDate(java.util.Date date,String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 时间相加
     */
    public static Date addDate(String datepart, int number, Date date) {
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        if (datepart.equals("yy")) {
            cal.add(Calendar.YEAR, number);
        } else if (datepart.equals("MM")) {
            cal.add(Calendar.MONTH, number);
        } else if (datepart.equals("dd")) {
            cal.add(Calendar.DATE, number);
        } else if (datepart.equals("HH")) {
            cal.add(Calendar.HOUR, number);
        } else if (datepart.equals("mm")) {
            cal.add(Calendar.MINUTE, number);
        } else if (datepart.equals("ss")) {
            cal.add(Calendar.SECOND, number);
        }
        return cal.getTime();
    }

    /**
     * 两个时间相差多少月
     */
    public static int getMonthDiff(Date startdate, Date enddate) {
        int k = 0;
        GregorianCalendar temp = new GregorianCalendar();
        temp.setTime(startdate);
        temp.set(GregorianCalendar.MILLISECOND, 0);
        temp.add(GregorianCalendar.DAY_OF_MONTH, 1);
        int day = temp.getActualMaximum(GregorianCalendar.DATE);
        GregorianCalendar endCal = new GregorianCalendar();
        endCal.setTime(enddate);
        endCal.set(GregorianCalendar.MILLISECOND, 0);
        endCal.add(GregorianCalendar.DAY_OF_MONTH, 1);
        while (temp.getTime().before(endCal.getTime())) {
            k++;
            day = temp.getActualMaximum(GregorianCalendar.DATE);
            temp.add(GregorianCalendar.DAY_OF_MONTH, day);
        }
        return k;
    }

    /**
     * 将时间的时分秒信息清除
     */
    public static Date clearTime(Date date) {
        if (date == null)return date;
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.clear(Calendar.MILLISECOND);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.AM_PM);
        return cal.getTime();
    }

    //需要加上时区的8个小时
    public static String millisToTime(long millisSeconds) {
        String result = "";
        long hours, minutes, seconds;
        hours = millisSeconds / 3600000%24;
        millisSeconds = millisSeconds - (hours * 3600000);
        minutes = millisSeconds / 60000%60;
        millisSeconds = millisSeconds - (minutes * 60000);
        seconds = millisSeconds / 1000%60;
        if (hours != 0) {
            result += hours + "小时";
        }
        result += minutes + "分";
        result += seconds + "秒";
        return result;
    }

    public static long timeDec(Date endDate, Date begDate) {
        return endDate.getTime() - begDate.getTime();
    }

    public static int getAge(java.util.Date date) {
        long temp=timeDec(new java.util.Date(),date);
        return (int) (temp/(365*24*60*60*10000));
    }

    //得到每个周的第一天
    public static Date getFirstDayWeek(Date date){
        Calendar cal = new GregorianCalendar();
        int curDay = cal.get(Calendar.DAY_OF_WEEK);
        cal.setTime(date);
        if (curDay == 1) {
            cal.add(GregorianCalendar.DATE, -6); // 每周第一天
        } else {
            cal.add(GregorianCalendar.DATE, 2 - curDay); // 每周第一天
        }
        return cal.getTime();
    }

    // 得到每个周的最后一天
    public static Date getLastDayWeek(Date date){
        return addDate("dd",6,getFirstDayWeek(date));
    }

    //得到每个月的第一天
    public static Date getFirstDayMonth(Date date){
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(date);
        int tempDate=tempCal.getActualMinimum(Calendar.DAY_OF_MONTH);
        tempCal.set(Calendar.DAY_OF_MONTH,tempDate);
        return tempCal.getTime();
    }

    // 得到每个月的最后一天
    public static Date getLastDayMonth(Date date){
        Calendar cal=Calendar.getInstance();
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(date);
        int tempDate=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        tempCal.set(Calendar.DAY_OF_MONTH,tempDate);
        return tempCal.getTime();
    }

    //距离某个时间的时间间隔,如果起始时间小于结束时间为正值,否则为负值
    public static long getInterval(Date begDate,Date endDate){
        Calendar cal=Calendar.getInstance();
        int hour=cal.get(Calendar.HOUR_OF_DAY);
        //date.setTimeInMillis(date.getTimeInMillis() + 86400000);
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 10);
        cal.set(Calendar.SECOND , 0);
        long interval=0;
        //System.out.println("AAA="+hour);
        if(hour<3){
            interval=cal.getTimeInMillis()-begDate.getTime();
        }
        else{
            Date tempDate=addDate("dd",1,cal.getTime());
            //System.out.println(getDate(tempDate,DATE_B));
            interval=tempDate.getTime()-begDate.getTime();
        }
        return interval;
    }

    public static void main(String args[]) {
        try {
            //			Date toDay = new Date();
            //			Date begDate = addDate("dd", -10, toDay);
            //			List<String> dateList = new ArrayList<String>();
            //			while (compareDate(toDay, begDate) >= 0) {
            //				dateList.add(dateToStr(begDate, "yyyy-MM-dd"));
            //				System.out.println(dateToStr(begDate, "yyyy-MM-dd"));
            //				begDate = addDate("dd", 1, begDate);
            //			}
            //			for (int i = 0; i < 5; i++) {
            //				System.out.println(Aaa.getAllConfigInfo());
            //			}


            //long aaa=1294231194828;
			/*
		    Date dateA=DateUtil.getFirstDayWeek(new java.util.Date());
		    Date dateB=DateUtil.getLastDayWeek(new java.util.Date());
		    Date dateC=DateUtil.getFirstDayMonth(new java.util.Date());
		    Date dateD=DateUtil.getLastDayMonth(new java.util.Date());
			System.out.println(getDate(dateA,DateUtil.DATE_A));
			System.out.println(getDate(dateB,DateUtil.DATE_B));
			System.out.println(getDate(dateC,DateUtil.DATE_A));
			System.out.println(getDate(dateD,DateUtil.DATE_B));
			Calendar tempCal = Calendar.getInstance();
			tempCal.set(Calendar.DAY_OF_WEEK,tempCal.getFirstDayOfWeek());
            */
			/*
			Calendar cal=Calendar.getInstance();
			int hour=cal.get(Calendar.HOUR_OF_DAY);
			//date.setTimeInMillis(date.getTimeInMillis() + 86400000);
			cal.set(Calendar.HOUR_OF_DAY, 3);
			cal.set(Calendar.MINUTE, 10);
			cal.set(Calendar.SECOND , 0);
			long interval=0;
			Date now=new Date();
			System.out.println("AAA="+hour);
			if(hour<3){
				interval=cal.getTimeInMillis()-now.getTime();
			}
			else{
				Date tempDate=addDate("dd",1,cal.getTime());
				//System.out.println(getDate(tempDate,DATE_B));
				interval=tempDate.getTime()-now.getTime();
			}
			*/
            Date now=strToDate("2011-05-12 23:00:00");
            //Date tempDate=addDate("dd",1,now);


            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 3);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND , 0);
            long t=cal.getTimeInMillis()-now.getTime();
            //t=t%86400000;
            System.out.println(t);
            System.out.println(millisToTime(t));
            Random random = new Random();
            double feeDouble=util.NumberUtil.getPosRadom(100,50)+(double)random.nextInt(10)/10;
            System.out.println(feeDouble);
        } catch (Exception e) {
            System.out.println(e);
            e.getStackTrace();
        }
    }
}