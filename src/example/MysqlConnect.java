package example;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
 
import org.junit.Test;
 
public class MysqlConnect {
     
    //创建数据库驱动名称
    private static String Driver_class = "com.mysql.jdbc.Driver";
     
    //数据库链接地址
//    private String url = "jdbc:mysql://localhost:3306/database";
    private String url = "jdbc:mysql://localhost:3306/database?useUnicode=true&characterEncoding=GB2312";
     
    //数据库用户名
    private String user = "root";
     
    //数据库密码
    private String password = "199306";
     
    //数据库链接
    private Connection con = null;
     
    //准备声明sql语句
    private PreparedStatement pstmt = null;
     
    //结果集
    private ResultSet rs;
     
    //影响行数
    private int i;
     
    /*
     * 创建驱动
     * */
    static{
        try {
            Class.forName(Driver_class);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
     
    /*
     * 加载驱动
     * */
    @Test
    public void getConnect() {
        // TODO Auto-generated method stub
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
        //判断数据库是否加载成功
        if(con != null){
//            System.out.println("数据库加载成功！");
        }else{
            System.out.println("数据库加载失败！");
        }
    }
     
    /*
     * 执行sql语句
     * */
    public void doSql(String sql,Object[] object) {
        // TODO Auto-generated method stub
         
        //判断sql语句是否存在
        if(sql != null){
            //加载驱动
            getConnect();
            //判断object数组是否存在
            if(object == null){
                //如果不存在，创建一个，防止出现空指针异常
                object = new Object[0];
            }
 
            try {
                //声明一条准备的sql语句
                pstmt = con.prepareStatement(sql);
                //为Object对象一一赋值
                for(int i = 0; i < object.length; i++){
                    pstmt.setObject(i + 1, object[i]);
                }
                //执行声明的sql语句
                pstmt.execute();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            System.out.println("sql语句并不存在！");
             
        }
    }
     
    /*
     * 获取结果集
     * */
    public ResultSet getRS(){
        try {
            //获取结果集方法
            rs = pstmt.getResultSet();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //返回结果集
        return rs;
    }
     
    /*
     * 获取影响行数
     * */
    public int getUpdateCount() {
        // TODO Auto-generated method stub
        try {
            //获取影响行数方法
            i = pstmt.getUpdateCount();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //返回影响行数
        return i;
    }
     
    /*
     * 关闭方法
     * */
    public void getClose() {
        // TODO Auto-generated method stub
        //关闭结果集
        try {
            //结果集关闭方法
//            rs.close();
        	pstmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //关闭声明的sql语句
            try {
                //关闭声明的sql语句方法
                pstmt.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                //卸载驱动
                try {
                    //驱动卸载方法
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                 
            }
        }
     
    }
     
}