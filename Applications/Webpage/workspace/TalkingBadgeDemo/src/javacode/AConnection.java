package javacode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AConnection {

	Connection connect = null;
	Statement statement = null;
	public ResultSet resultSet = null;

	public AConnection(ResultSet resultSet, Statement statement,
			Connection connect) {
		super();
		this.resultSet = resultSet;
		this.statement = statement;
		this.connect = connect;
	}

	public void closeConnection() {
		try {
			if (resultSet != null)
				resultSet.close();
			if (statement != null) {
				statement.close();
			}
			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
