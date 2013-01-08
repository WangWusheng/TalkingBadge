
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LogInAdmin
 */
@WebServlet("/LogInAdmin")
public class LogInAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String adminName="admin";
	public static final String adminPasswd="haohaoxinying";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LogInAdmin() {
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

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE><link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1>Welcome to the TalkingBadge Demo Administrator</h1>");
		out.println("<form action=\"Admin\" method=\"post\">");
		out.println("Username: <input type=\"text\" name=\"UsernameAdmin\" /><br>");
		out.println("Password: <input type=\"password\" name=\"PasswordAdmin\" /><br>");
		out.println("<input type=\"submit\" value=\"Submit\" /><input type=\"reset\" value=\"Reset\" /></form><br>");

		out.println("<br><h3><a href=\"LogIn\">Log in as a user</h3></a>");
		out.println("</BODY></HTML>");

	}
}
