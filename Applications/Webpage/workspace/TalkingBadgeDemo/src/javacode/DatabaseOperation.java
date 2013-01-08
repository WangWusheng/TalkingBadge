package javacode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseOperation {

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager
					.getConnection("jdbc:mysql://localhost/talkingbadge?"
							+ "user=talkingbadge&password=TalkingBadge");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public void initDB() {
		try {
			// backup();
			// statement.executeUpdate("DROP DATABASE IF EXISTS TalkingBadge;");
			statement
					.executeUpdate("CREATE DATABASE IF NOT EXISTS TalkingBadge;");
			statement.executeUpdate("USE TalkingBadge;");
			
			//statement.executeUpdate("DROP TABLE IF EXISTS user;");
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS user(id INT NOT NULL AUTO_INCREMENT, name VARCHAR(30), passwd VARCHAR(30),email VARCHAR(30), gender VARCHAR(1), age INT, mac VARCHAR(12), StudentStatus VARCHAR(3), Live VARCHAR(300), Born VARCHAR(300), InstalledApps VARCHAR(300), Frequency VARCHAR(50), Concern VARCHAR(1), TagUser VARCHAR(5), PRIMARY KEY (ID), UNIQUE KEY (`name`));");
			// 1 successful; 0 fail;-1 re-sent;-2 waiting for approval; -3
			// reject
			
			//statement.executeUpdate("DROP TABLE IF EXISTS messagerecord;");
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS messagerecord(id INT NOT NULL AUTO_INCREMENT, time VARCHAR(30), fromname VARCHAR(30), tomac VARCHAR(12), content VARCHAR(500), language VARCHAR(20), gender VARCHAR(1), success INT, remark VARCHAR(500),PRIMARY KEY (ID));");
			
			statement.executeUpdate("DROP TABLE IF EXISTS detectrecord;");
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS detectrecord(id INT NOT NULL AUTO_INCREMENT, time VARCHAR(30), terminalid VARCHAR(12), terminalfriendlyname VARCHAR(30), lastrssi  VARCHAR(5), blipnodeid VARCHAR(12), currentzone VARCHAR(30), previouszone VARCHAR(30), PRIMARY KEY (ID));");

			// statement.executeUpdate("INSERT INTO user values (default, 'wusheng', 'wusheng','M', 26, 'b0ec71740c41','TalkingBadge');");
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
		// DatabaseOperation db = new DatabaseOperation();
		// db.readDataBase();

		System.out.println("back-uping...");
		backup();
		System.out.println("back-uped...");

		// System.out.println("开始还原...");
		// load1();
		// System.out.println("还原成功...");

	}

	public static void backup() {
		try {
			Runtime rt = Runtime.getRuntime();
			// 调用 调用mysql的安装目录的命令
			Process child = rt
					.exec("C://Program Files//MySQL//MySQL Server 5.5//bin//mysqldump -h localhost -utalkingbadge -pTalkingBadge talkingbadge");
			// 设置导出编码为utf-8。这里必须是utf-8
			// 把进程执行中的控制台输出信息写入.sql文件，即生成了备份文件。注：如果不对控制台信息进行读出，则会导致进程堵塞无法运行
			InputStream in = child.getInputStream();// 控制台的输出信息作为输入流
			InputStreamReader xx = new InputStreamReader(in, "utf-8");
			// 设置输出流编码为utf-8。这里必须是utf-8，否则从流中读入的是乱码
			String inStr;
			StringBuffer sb = new StringBuffer("");
			String outStr;
			// 组合控制台输出信息字符串
			BufferedReader br = new BufferedReader(xx);
			while ((inStr = br.readLine()) != null) {
				sb.append(inStr + "\r\n");
			}
			outStr = sb.toString();
			// 要用来做导入用的sql目标文件：
			FileOutputStream fout = new FileOutputStream(
					"c://wusheng//bddump//talkingbadge"
							+ new SimpleDateFormat("yyyyMMddHHmmss")
									.format(new Date()) + ".sql");
			OutputStreamWriter writer = new OutputStreamWriter(fout, "utf-8");
			writer.write(outStr);
			writer.flush();
			in.close();
			xx.close();
			br.close();
			writer.close();
			fout.close();
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			String fPath = "c:/test.sql";
			Runtime rt = Runtime.getRuntime();
			// 调用 mysql 安装目录的命令
			Process child = rt
					.exec("C://Program Files//MySQL//MySQL Server 5.1//bin//mysql -u root -p root dlgs_test");

			OutputStream out = child.getOutputStream();// 控制台的输入信息作为输出流
			String inStr;
			StringBuffer sb = new StringBuffer("");
			String outStr;
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fPath), "utf-8"));
			while ((inStr = br.readLine()) != null) {
				sb.append(inStr + "\r\n");
			}
			outStr = sb.toString();
			System.out.println(outStr);
			OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");
			System.out.println("7777777777777777777777777777777777777");
			writer.write(outStr);
			System.out.println("888888888888888888888888888888888888888");
			writer.flush();
			out.close();
			br.close();
			writer.close();
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void load1() {
		try {
			String fPath = "c:/test.sql";
			Runtime rt = Runtime.getRuntime();

			// 调用 mysql 的 cmd:
			Process child = rt
					.exec("C://Program Files//MySQL//MySQL Server 5.1//bin//mysql.exe -u talkingbadge -p TalkingBadge talkingbadge");
			OutputStream out = child.getOutputStream();// 控制台的输入信息作为输出流
			String inStr;
			StringBuffer sb = new StringBuffer("");
			String outStr;
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fPath), "utf8"));
			while ((inStr = br.readLine()) != null) {
				sb.append(inStr + "\r\n");
			}
			outStr = sb.toString();

			OutputStreamWriter writer = new OutputStreamWriter(out, "utf8");
			writer.write(outStr);
			// 注：这里如果用缓冲方式写入文件的话，会导致中文乱码，用flush()方法则可以避免
			writer.flush();
			// 别忘记关闭输入输出流
			out.close();
			br.close();
			writer.close();

			System.out.println("");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
