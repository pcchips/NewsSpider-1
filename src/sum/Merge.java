package sum;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import us.codecraft.webmagic.processor.example.MysqlConnect;

public class Merge {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		test();
	}
	
	// 导入mysqloperation类
	private static MysqlConnect mysqlCon = new MysqlConnect();

	// 准备sql语句
	private static String sql;
	private static String querySql;

	// 影响行数（数据变更后，影响行数都是大于0，等于0时没变更，所以说如果变更失败，那么影响行数必定为负）
	private static int i = -1;

	// 结果集
	private static ResultSet rs;
	
	/**
	 * 读取数据库数据
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public static ArrayList<String> getSqlData() throws SQLException {
		ArrayList<String> page = new ArrayList<String>();
		// 执行查询语句
		querySql = "select * from xinlang";
		mysqlCon.doSql(querySql, null);
		rs = mysqlCon.getRS();
		while(rs.next()){
			page.add(rs.getString("id"));// 1
			page.add(rs.getString("origin_link"));// 2
			page.add(rs.getString("current_link"));// 3
			page.add(rs.getString("type"));// 4
			page.add(rs.getString("title"));// 5
			page.add(rs.getString("content"));// 6
			page.add(rs.getString("deal_content"));// 7
			page.add(rs.getString("scratch_time").substring(0, rs.getString("scratch_time").length()-2));// 8
			page.add(rs.getString("post_time").substring(0, rs.getString("post_time").length()-2));// 9
			page.add(rs.getString("html_path"));// 10
		}
		return page;
	}
	
	/**
	 * 补充数据库
	 * 
	 * @throws Exception
	 */
	public static void insertSql(int id, int type, String[] singlePage) throws Exception {
//		int id = Integer.parseInt(singlePage[0]);
//		int type = Integer.parseInt(singlePage[3]);
		// 创建sql语句
		sql = "replace into xinlang_" + type + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] answerobject = new Object[] { id, singlePage[1], singlePage[2],
				type, singlePage[4], singlePage[5], singlePage[6], singlePage[7],
				singlePage[8], singlePage[9] };

		mysqlCon.doSql(sql, answerobject);
		i = mysqlCon.getUpdateCount();
		if (i != -1) {
//			System.out.println("数据插入成功！");
		} else {
			System.out.println("数据插入失败！");
		}
		// 关闭链接
		mysqlCon.getClose();

	}
	
	/**
	 * 插入数据库
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("null")
	public static void insertAll(){
		try {
			ArrayList<String> page = getSqlData();
			String[] singlePage = new String[10];
			int size = page.size() / 10;
			int id = 0,id1 = 0,id2 = 0,id3 = 0,id4 = 0,id5 = 0;
			for(int i = 0; i < size; i++){
				singlePage[0] = page.get(0+10*i);
				singlePage[1] = page.get(1+10*i);
				singlePage[2] = page.get(2+10*i);
				singlePage[3] = page.get(3+10*i);
				singlePage[4] = page.get(4+10*i);
				singlePage[5] = page.get(5+10*i);
				singlePage[6] = page.get(6+10*i);
				singlePage[7] = page.get(7+10*i);
				singlePage[8] = page.get(8+10*i);
				singlePage[9] = page.get(9+10*i);
				
				int type = Integer.parseInt(singlePage[3]);
				if(type == 1){
					id1++;
					insertSql(id1 , type, singlePage);
				} else if(type == 2){
					id2++;
					insertSql(id2 , type, singlePage);
				} else if(type == 3){
					id3++;
					insertSql(id3 , type, singlePage);
				} else if(type == 4){
					id4++;
					insertSql(id4 , type, singlePage);
				} else if(type == 5){
					id5++;
					insertSql(id5 , type, singlePage);
				} else {
					System.out.println("type is not right....");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 补充数据库
	 * 
	 * @throws Exception
	 */
	public static void updateSql() throws Exception {
		// 创建sql语句
//		sql = "update a2 set type=type+1 where id<596";
//		sql = "update a2 set type=1 where id=1";
		
		// 类别1，体育
//		sql = "update tengxun_1_copy set id=id+576";
//		sql = "update wangyi_1_copy set id=id+1143";
//		sql = "update xinlang_1_copy set id=id+1538";
//		sql = "update zhongxin_1_copy set id=id+2538";
	
		// 类别2，娱乐
//		sql = "update tengxun_2_copy set id=id+595";
//		sql = "update wangyi_2_copy set id=id+915";
//		sql = "update xinlang_2_copy set id=id+1133";
//		sql = "update zhongxin_2_copy set id=id+2133";
		
		// 类别3，财经
//		sql = "update tengxun_3_copy set id=id+598";
//		sql = "update wangyi_3_copy set id=id+1582";
//		sql = "update xinlang_3_copy set id=id+2261";
		sql = "update zhongxin_3_copy set id=id+3261";
		
		// 类别4，科技
//		sql = "update tengxun_4_copy set id=id+600";
//		sql = "update wangyi_4_copy set id=id+1074";
//		sql = "update xinlang_4_copy set id=id+1454";
//		sql = "update zhongxin_4_copy set id=id+2275";
		
		// 类别5，军事
//		sql = "update tengxun_5_copy set id=id+600";
//		sql = "update wangyi_5_copy set id=id+1290";
//		sql = "update xinlang_5_copy set id=id+1633";
//		sql = "update zhongxin_5_copy set id=id+2228";
		
		mysqlCon.doSql(sql, null);
		i = mysqlCon.getUpdateCount();
		if (i != -1) {
//			System.out.println("数据插入成功！");
		} else {
			System.out.println("数据插入失败！");
		}
		// 关闭链接
		mysqlCon.getClose();

	}
	
	/**
	 * 读取数据库数据
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public static ArrayList<String> getSqlData2(String sourceTable) throws SQLException {
		ArrayList<String> page = new ArrayList<String>();
		// 执行查询语句
		querySql = "select * from " + sourceTable;
		mysqlCon.doSql(querySql, null);
		rs = mysqlCon.getRS();
		while(rs.next()){
			page.add(rs.getString("id"));// 1
			page.add(rs.getString("origin_link"));// 2
			page.add(rs.getString("current_link"));// 3
			page.add(rs.getString("type"));// 4
			page.add(rs.getString("title"));// 5
			page.add(rs.getString("content"));// 6
			page.add(rs.getString("deal_content"));// 7
			page.add(rs.getString("scratch_time").substring(0, rs.getString("scratch_time").length()-2));// 8
			page.add(rs.getString("post_time").substring(0, rs.getString("post_time").length()-2));// 9
			page.add(rs.getString("html_path"));// 10
		}
		return page;
	}
	
	/**
	 * 补充数据库
	 * 
	 * @throws Exception
	 */
	public static void insertSql2(String targetTable, String[] singlePage) throws Exception {
//		int id = Integer.parseInt(singlePage[0]);
//		int type = Integer.parseInt(singlePage[3]);
		// 创建sql语句
		sql = "replace into "+targetTable+" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] answerobject = new Object[] { singlePage[0], singlePage[1], singlePage[2],
				singlePage[3], singlePage[4], singlePage[5], singlePage[6], singlePage[7],
				singlePage[8], singlePage[9] };

		mysqlCon.doSql(sql, answerobject);
		i = mysqlCon.getUpdateCount();
		if (i != -1) {
//			System.out.println("数据插入成功！");
		} else {
			System.out.println("数据插入失败！");
		}
		// 关闭链接
		mysqlCon.getClose();

	}
	
	/**
	 * 插入数据库
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("null")
	public static void insertAll2(){
//		String souceTable1 = "tengxun_1_copy";
//		String souceTable1 = "wangyi_1_copy";
//		String souceTable1 = "xinlang_3_copy";
		String souceTable1 = "zhongxin_3_copy";
		String targetTable = "a3";
		try {
			ArrayList<String> page = getSqlData2(souceTable1);
			String[] singlePage = new String[10];
			int size = page.size() / 10;
			for(int i = 0; i < size; i++){
				singlePage[0] = page.get(0+10*i);
				singlePage[1] = page.get(1+10*i);
				singlePage[2] = page.get(2+10*i);
				singlePage[3] = page.get(3+10*i);
				singlePage[4] = page.get(4+10*i);
				singlePage[5] = page.get(5+10*i);
				singlePage[6] = page.get(6+10*i);
				singlePage[7] = page.get(7+10*i);
				singlePage[8] = page.get(8+10*i);
				singlePage[9] = page.get(9+10*i);
				
				insertSql2(targetTable, singlePage);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public static void test() throws Exception{
//		insertAll();
//		updateSql();
		insertAll2();
	}

}
