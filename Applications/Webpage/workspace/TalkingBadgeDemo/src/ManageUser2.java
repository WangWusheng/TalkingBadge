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
 * Servlet implementation class ManageUser2
 */
@WebServlet("/ManageUser2")
public class ManageUser2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ManageUser2() {
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
		String UserId = request.getParameter("user");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE><link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1>TalkingBadge Demo Administrator</h1>");
		if (UserId == null) {
			out.println("You did not choose a user<br>");
			out.println("<form action=\"ManageUser1\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"UsernameAdmin\" value=\""
					+ UsernameAdmin + "\"/>");
			out.println("<input type=\"hidden\" name=\"PasswordAdmin\" value=\""
					+ PasswordAdmin + "\"/>");
			out.println("<input type=\"submit\" value=\"Go back to user management\" />");
			out.println("</form>");

			out.println("<form action=\"Admin\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"UsernameAdmin\" value=\""
					+ UsernameAdmin + "\"/>");
			out.println("<input type=\"hidden\" name=\"PasswordAdmin\" value=\""
					+ PasswordAdmin + "\"/>");
			out.println("<input type=\"submit\" value=\"Go back to main page\" />");
			out.println("</form>");

			out.println("<br><h3><a href=\"LogInAdmin\">Log out</a></h3>");

		} else if (UsernameAdmin.equals(LogInAdmin.adminName)
				&& PasswordAdmin.equals(LogInAdmin.adminPasswd)) {
			out.println("Please choose one user<br>");
			AConnection aConnection = TBService
					.dbQuery("select * from talkingbadge.user where id="
							+ UserId + ";");
			ResultSet users = aConnection.resultSet;

			out.println("<form action=\"ManageUser3\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"UsernameAdmin\" value=\""
					+ UsernameAdmin + "\"/>");
			out.println("<input type=\"hidden\" name=\"PasswordAdmin\" value=\""
					+ PasswordAdmin + "\"/>");
			out.println("<input type=\"hidden\" name=\"UserId\" value=\""
					+ UserId + "\"/>");

			try {
				if (users.next()) {
					out.println("Username: <input type=\"text\" name=\"Username\" value=\""
							+ users.getString("name") + "\"/><br>");
					out.println("Password: <input type=\"text\" name=\"Password\" value=\""
							+ users.getString("name") + "\"/><br>");
					out.println("Gender: <select name=\"Gender\">  <option value =\"M\" "
							+ (users.getString("gender").equals("M") ? "selected=\"selected\""
									: "")
							+ ">Male</option> <option value =\"F\" "
							+ (users.getString("gender").equals("F") ? "selected=\"selected\""
									: "") + ">Female</option></select><br>");
					out.println("Age: <input type=\"text\" name=\"Age\" value=\""
							+ users.getString("age") + "\"/><br>");
					out.println("Mac: <input type=\"text\" name=\"Mac\" value=\""
							+ users.getString("mac") + "\"/><br>");
					out.println("StudentStatus: <input type=\"text\" name=\"StudentStatus\" value=\""
							+ users.getString("StudentStatus") + "\"/><br>");
					out.println("Live: <input type=\"text\" name=\"Live\" value=\""
							+ users.getString("Live") + "\"/><br>");
					out.println("Born: <input type=\"text\" name=\"Born\" value=\""
							+ users.getString("Born") + "\"/><br>");
					out.println("InstalledApps: <input type=\"text\" name=\"InstalledApps\" value=\""
							+ users.getString("InstalledApps") + "\"/><br>");
					out.println("Frequency: <input type=\"text\" name=\"Frequency\" value=\""
							+ users.getString("Frequency") + "\"/><br>");
					out.println("Concern: <input type=\"text\" name=\"Concern\" value=\""
							+ users.getString("Concern") + "\"/><br>");
					out.println("TagUser: <input type=\"text\" name=\"TagUser\" value=\""
							+ users.getString("TagUser") + "\"/><br>");
				}
				aConnection.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			out.println("<input type=\"submit\" value=\"Submit\" />");
			out.println("</form>");

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
