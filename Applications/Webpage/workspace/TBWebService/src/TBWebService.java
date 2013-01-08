import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;

public class TBWebService {

	private static DatabaseOperation db = null;

	// Get scan result
	public static String getDevicesList() {
		try {
			SearchDevice.startSearchingService();
		} catch (BlipServerAccessException e) {
			e.printStackTrace();
		} catch (BlipServerConnectionException e) {
			e.printStackTrace();
		}
		LinkedList<UserDevice> devs = SearchDevice.devicesList;
		String result = new String("Devices:");
		for (int i = 0; i < devs.size(); i++) {
			result = result + " "
					+ devs.get(i).getCd().getTerminalID().toString();
		}
		return result;
	}

	// OPP
	public static void sendFile(String terminalMac, String content) {
		new OPPService(terminalMac, content);
	}

	// SPP
	public static void sendCommand(String terminalMac, String content) {
		new SPPService(terminalMac, content);
	}

	// DB update
	public static String dbUpdate(String sqlContent) {
		Connection connect = null;
		Statement statement = null;
		if (db == null)
			db = new DatabaseOperation();

		connect = db.getConnection();

		try {
			statement = connect.createStatement();
			statement.executeUpdate(sqlContent);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.toString();
		}
		try {
			if (statement != null) {
				statement.close();
			}
			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			return e.toString();
		}

		return "True";
	}

	// DB query
	public static String dbQueryAllUsers() {
		String result = "";
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		if (db == null)
			db = new DatabaseOperation();
		if (connect == null)
			connect = db.getConnection();
		if (statement == null)
			try {
				statement = connect.createStatement();
				resultSet = statement
						.executeQuery("select * from TalkingBadge.user");
				while (resultSet.next()) {
					result = result + "\n" + resultSet.getString("name") + " "
							+ resultSet.getString("passwd") + " "
							+ resultSet.getString("genda") + " "
							+ resultSet.getInt("age") + " "
							+ resultSet.getString("origin");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				return e.toString();
			}
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
			return e.toString();
		}
		return result.equals("")?"":result.substring(1);
	}
}
