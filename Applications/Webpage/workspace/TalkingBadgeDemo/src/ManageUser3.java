import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javacode.AConnection;
import javacode.TBService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ManageUser3
 */
@WebServlet("/ManageUser3")
public class ManageUser3 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ManageUser3() {
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
		String UsernameAdmin = request.getParameter("UsernameAdmin");
		String PasswordAdmin = request.getParameter("PasswordAdmin");
		String UserId = request.getParameter("UserId");

		String Username = request.getParameter("Username");
		String Password = request.getParameter("Password");
		String Gender = request.getParameter("Gender");
		String Mac = request.getParameter("Mac");
		String Age = request.getParameter("Age");
		String StudentStatus = request.getParameter("StudentStatus");
		String Live = request.getParameter("Live");
		String Born = request.getParameter("Born");
		String InstalledApps = request.getParameter("InstalledApps");
		String Frequency = request.getParameter("Frequency");
		String Concern = request.getParameter("Concern");
		String TagUser = request.getParameter("TagUser");

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE><link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1>TalkingBadge Demo Administrator</h1>");
		if (UsernameAdmin.equals(LogInAdmin.adminName)
				&& PasswordAdmin.equals(LogInAdmin.adminPasswd)) {
			if (!Age.matches("\\d+")) {
				// out.println("Invalate age, and we assume that you are 20 years old. <br>");
				Age = "0";
			}
			out.println("Username: " + Username + "<br>Gender: " + Gender
					+ "<br>Age: " + Age);
			out.println("<br>StudentStatus: " + StudentStatus + "<br>Live: "
					+ Live + "<br>Born: " + Born);
			out.println("<br>InstalledApp: " + InstalledApps
					+ "<br>Frequency: " + Frequency + "<br>Concern: " + Concern
					+ "<br>TagUser: " + TagUser);
			String sql = "UPDATE user SET name='" + Username + "', passwd='"
					+ Password + "',gender='" + Gender + "', age='" + Age
					+ "',mac='" + Mac + "', StudentStatus='" + StudentStatus
					+ "',Live='" + Live + "', Born='" + Born
					+ "',InstalledApps='" + InstalledApps + "', Frequency='"
					+ Frequency + "',Concern='" + Concern + "',TagUser='"
					+ TagUser + "' WHERE id='" + UserId + "';";
			// System.out.println(sql);
			TBService.dbUpdate(sql);
			out.println("<br>Update successfully!");

			out.println("<form action=\"Admin\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"UsernameAdmin\" value=\""
					+ UsernameAdmin + "\"/>");
			out.println("<input type=\"hidden\" name=\"PasswordAdmin\" value=\""
					+ PasswordAdmin + "\"/>");
			out.println("<input type=\"submit\" value=\"Go back to main page\" />");
			out.println("</form>");
			out.println("<br><h3><a href=\"LogInAdmin\">Log out</a></h3>");
		} else {
			out.println("Sorry, wrong account information.<br>");
			out.println("<br><h3><a href=\"LogInAdmin\">Log in as system administrator</a></h3>");
			out.println("<br><h3><a href=\"LogIn\">Log in as TalkingBadge user</a></h3>");
		}
		out.println("</BODY>");
	}

}
