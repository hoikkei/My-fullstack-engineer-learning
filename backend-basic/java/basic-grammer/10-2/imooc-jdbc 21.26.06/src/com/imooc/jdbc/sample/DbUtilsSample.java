package com.imooc.jdbc.sample;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.imooc.jdbc.common.DbUtils;
import com.imooc.jdbc.hrapp.entity.Employee;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
/**
 * Apache DBUtils + Druid联合使用演示
 */
public class DbUtilsSample {
    private static void query(){
        Properties properties = new Properties();
        String propertyFile = DbUtilsSample.class.getResource("/druid-config.properties").getPath();
        try {
            propertyFile = URLDecoder.decode(propertyFile, StandardCharsets.UTF_8.name());
            properties.load(new FileInputStream(propertyFile));
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            //利用Apache DbUtils大幅简化了数据的提取过程
            QueryRunner qr = new QueryRunner(dataSource);
            List<Employee> list =  qr.query("select * from employee limit ?,10",
                    new BeanListHandler<>(Employee.class),
                    new Object[]{10});
            for (Employee emp : list) {
                System.out.println(emp.getEname());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(){
        Properties properties = new Properties();
        String propertyFile = DbUtilsSample.class.getResource("/druid-config.properties").getPath();
        Connection conn = null;
        try {
            propertyFile = URLDecoder.decode(propertyFile, StandardCharsets.UTF_8.name());
            properties.load(new FileInputStream(propertyFile));
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            String sql1 = "update employee set salary=salary+1000 where eno=?";
            String sql2 = "update employee set salary=salary-500 where eno=?";
            QueryRunner qr = new QueryRunner();
            qr.update(conn,sql1,new Object[]{1000});
            qr.update(conn,sql2,new Object[]{1001});
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        query();
        update();
    }
}
