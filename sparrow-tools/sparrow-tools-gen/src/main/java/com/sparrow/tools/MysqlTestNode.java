package com.sparrow.tools;

import com.sparrow.tools.utils.StringUtils;

import java.io.*;
import java.sql.*;

/**
 * Created by Administrator on 2017/3/31.
 */
public class MysqlTestNode {
    public static Connection getConnectionEx() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("No driver setting ... ");
        }
        return DriverManager.getConnection("jdbc:mysql://192.168.2.143:3306/fhir1", "root", "123456");
    }

    public static void main(String args[]) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("D:/test.txt")));
            BufferedReader reader1 = new BufferedReader(new FileReader(new File("D:/test1.txt")));
            String line = null;
            long max = 0;
            while ((line = reader.readLine()) != null) {
                String ln=reader1.readLine();
                if(!org.apache.commons.lang3.StringUtils.equals(line,ln))
                    System.out.println(String.format("line=%s ",line));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main1(String args[]) {
        try {
            Connection connection = getConnectionEx();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select id,date_t from category ORDER BY date_t asc;");
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(new File("D:/test1.txt"))));
            while (rs.next()) {
                writer.println(rs.getString(1));
            }
            rs.close();
            stmt.close();
            connection.close();
            writer.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
