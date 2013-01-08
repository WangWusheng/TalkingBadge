import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Admin
 */
@WebServlet("/Admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Admin() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String UsernameAdmin = request.getParameter("UsernameAdmin");
		String PasswordAdmin = request.getParameter("PasswordAdmin");

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE><link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1>TalkingBadge Demo Administrator</h1>");
		if (UsernameAdmin.equals(LogInAdmin.adminName)
				&& PasswordAdmin.equals(LogInAdmin.adminPasswd)) {
			out.println("Please choose what do you want to do<br>");

			out.println("<form action=\"ManageUser1\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"UsernameAdmin\" value=\""
					+ UsernameAdmin + "\"/>");
			out.println("<input type=\"hidden\" name=\"PasswordAdmin\" value=\""
					+ PasswordAdmin + "\"/>");
			out.println("<input type=\"submit\" value=\"User Management\" />");
			out.println("</form>");

			out.println("<form action=\"ManageMessage1\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"UsernameAdmin\" value=\""
					+ UsernameAdmin + "\"/>");
			out.println("<input type=\"hidden\" name=\"PasswordAdmin\" value=\""
					+ PasswordAdmin + "\"/>");
			out.println("<input type=\"submit\" value=\"Message Management\" />");
			out.println("</form>");
			out.println("<br><h3><a href=\"LogInAdmin\">Log out</a></h3>");
		} else {
			out.println("Sorry, wrong account information.<br>");
			out.println("<br><h3><a href=\"LogInAdmin\">Log in as system administrator<h3></a>");
			out.println("<br><h3><a href=\"LogIn\">Log out as TalkingBadge user<h3></a>");
		}
		out.println("</BODY>");
	}

}
