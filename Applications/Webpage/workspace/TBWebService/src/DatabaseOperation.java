import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseOperation {

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager
					.getConnection("jdbc:mysql://localhost/TalkingBadge?"
							+ "user=root&password=TalkingBadge");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public void initDB() {
		try {
			//statement.executeUpdate("DROP DATABASE IF EXISTS TalkingBadge;");
			statement
					.executeUpdate("CREATE DATABASE IF NOT EXISTS TalkingBadge;");
			statement.executeUpdate("USE TalkingBadge;");
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS user(id INT NOT NULL AUTO_INCREMENT, name VARCHAR(30), passwd VARCHAR(30), genda VARCHAR(1), age INT, origin VARCHAR(30)default 'TalkingBadge',PRIMARY KEY (ID), UNIQUE KEY (`name`));");
			// statement.executeUpdate("INSERT INTO user values (default, 'wusheng', 'wusheng','M', 26, 'TalkingBadge');");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DatabaseOperation() {
		super();

		try {
			connect = getConnection();
			statement = connect.createStatement();
			initDB();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void readDataBase() throws Exception {
		try {
			connect = getConnection();
			statement = connect.createStatement();
			initDB();
			resultSet = statement
					.executeQuery("select * from TalkingBadge.user");
			while (resultSet.next()) {
				String user = resultSet.getString("name");
				System.out.println("User: " + user);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

	}

	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

	public static void main(String[] args) throws Exception {
		DatabaseOperation db = new DatabaseOperation();
		db.readDataBase();

	}

}
