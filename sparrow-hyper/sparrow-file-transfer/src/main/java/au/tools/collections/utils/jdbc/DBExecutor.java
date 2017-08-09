package au.tools.collections.utils.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBExecutor {

	public static boolean test(Properties properties) {
		try {
			JDBCConnectionFactory cf = new JDBCConnectionFactory(properties);
			Connection conn = cf.getConnection();
			Statement stat = conn.createStatement();
			stat.executeQuery("SELECT COUNT(*) FROM TRANSFER_TASK");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
	}
}
