import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javacode.AConnection;
import javacode.TBService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegistResult
 */
@WebServlet("/RegistResult")
public class RegistResult extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegistResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String Username = request.getParameter("Username");
		String Password = request.getParameter("Password");
		String RepeatPassword = request.getParameter("RepeatPassword");
		String Gender = request.getParameter("Gender");
		String Email = request.getParameter("Email");
		String Age = request.getParameter("Age");
		String StudentStatus = request.getParameter("StudentStatus");
		String Live = request.getParameter("Live");
		String Born = request.getParameter("Born");
		String[] InstalledApps = request.getParameterValues("InstalledApps");
		String Frequency = request.getParameter("Frequency");
		String Concern = request.getParameter("Concern");

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE><link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1><font color=\"#FFA500\">TalkingBadge Demo</font></h1>");
		if (Username.equals("") || Password.equals("")) {
			out.println("Invalid Username or password. Please go back to <a href=\"Regist\">Regist</a> and try again.");
		} else {
			AConnection aConnection = TBService
					.dbQuery("select * from talkingbadge.user where name='"
							+ Username + "';");
			try {
				if (aConnection.resultSet.next()) {
					out.println("Username "
							+ Username
							+ " already exist. Please go back to <a href=\"Regist\">Regist</a> and try another one.");
				} else if (!Password.equals(RepeatPassword)) {
					out.println("The two passwords are not the same. Please go back to <a href=\"Regist\">Regist</a> and try another one.");
				} else {
					if (!Age.matches("\\d+")) {
						// out.println("Invalate age, and we assume that you are 0 years old. <br>");
						Age = "0";
					}
					if (InstalledApps == null)
						InstalledApps = new String[] {};
					String InstalledAppString = "";
					for (String InstalledApp : InstalledApps) {
						InstalledAppString += InstalledApp + ", ";
					}
					if (!InstalledAppString.equals(""))
						InstalledAppString = InstalledAppString.substring(0,
								InstalledAppString.length() - 2);
					out.println("Username: " + Username + "<br>Gender: "
							+ Gender + "<br>Age: " + Age);
					out.println("<br>StudentStatus: " + StudentStatus
							+ "<br>Live: " + Live + "<br>Born: " + Born);
					out.println("<br>InstalledApp: " + InstalledAppString
							+ "<br>Frequency: " + Frequency + "<br>Concern: "
							+ Concern);

					String sql = "INSERT INTO user values (default, '"
							+ Username + "', '" + Password + "','" +Email+"','"+ Gender
							+ "', " + Age + ",'NULL','" + StudentStatus
							+ "', '" + Live + "','" + Born + "', '"
							+ InstalledAppString + "', '" + Frequency + "','"
							+ Concern + "', 'false');";
					// System.out.println(sql);
					TBService.dbUpdate(sql);
					out.println("<br>Register successfully!");

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			aConnection.closeConnection();
			out.println("<br><h3><a href=\"LogIn\">Log in</a></h3>");
			out.println("</BODY></HTML>");
		}
	}
}
