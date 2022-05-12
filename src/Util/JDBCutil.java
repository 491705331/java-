package Util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JDBCutil {
    private static DataSource ds; // 数据源
    static { //加载数据库配置文件
        try {
            Properties ps = new Properties();
            ps.load(new FileInputStream("src/source/druid.properties"));
            ds = DruidDataSourceFactory.createDataSource(ps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection connection = getConnection();
        System.out.println(connection);

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static  <T> List<T> queryForList(Class<T> tClass, String sql, Object...obj){ //执行sql语句，将结果返回为tClass对象的列表
        ArrayList<T> arrayList=null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i=0; i<obj.length; ++i){
                preparedStatement.setObject(i+1,obj[i]);
            }

            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData(); //获得元数据

            arrayList = new ArrayList<>();
            while (resultSet.next()){ //有下一行?
                T t = tClass.getDeclaredConstructor().newInstance();
                for(int i=0; i<metaData.getColumnCount(); ++i){
                    Object columnValue = resultSet.getObject(i + 1); //也可以根据名字好像
                    Field field = tClass.getDeclaredField( metaData.getColumnName(i+1) );
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
                arrayList.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            close(resultSet,preparedStatement,connection);
        }
        return arrayList;
    }

    public static  <T> Object queryForObject(Class<T> tClass, String sql, Object...obj){ //执行sql语句，将结果返回为tClass对象
        T t = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i=0; i<obj.length; ++i){
                preparedStatement.setObject(i+1,obj[i]);
            }

            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData(); //获得元数据

            while (resultSet.next()){ //有下一行?
                t = tClass.getDeclaredConstructor().newInstance();
                for(int i=0; i<metaData.getColumnCount(); ++i){
                    Object columnValue = resultSet.getObject(i + 1);
                    Field field = tClass.getDeclaredField( metaData.getColumnName(i+1) );
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            close(resultSet,preparedStatement,connection);
        }
        return t;
    }

    public static int queryForInt(String sql, Object... obj){ // //执行sql语句，将结果返回为int数据
        Object object = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i=0; i<obj.length; ++i){
                preparedStatement.setObject(i+1,obj[i]);
            }
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){ //有下一行?
                object = resultSet.getObject(1);//也可以根据名字好像
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            close(resultSet,preparedStatement,connection);
        }
        if ( object==null ) {
            return 0;
        }
        return Integer.parseInt( String.valueOf( object) );
    }

    public static boolean update(String sql, Object...obj) { //执行sql语句进行更新数据，返回是否更新成功
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i=0; i<obj.length; ++i){
                preparedStatement.setObject(i+1,obj[i]);
            }

            return preparedStatement.executeUpdate()>=1;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            close(preparedStatement,connection);
        }
        return false;
    }

    public static void close(Statement statement, Connection con){
        close(null,statement,con);
    }

    public static void close(ResultSet rs,Statement statement, Connection con){
        if( rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if( statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if( con!=null){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection getConnection(){ //返回一个数据库连接对象
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
