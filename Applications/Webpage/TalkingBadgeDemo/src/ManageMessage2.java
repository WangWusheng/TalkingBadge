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
 * Servlet implementation class ManageMessage2
 */
@WebServlet("/ManageMessage2")
public class ManageMessage2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ManageMessage2() {
		super();
		// TODO Auto-generated constructor stub
	}

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
		String action = request.getParameter("submit");
		// System.out.println(action);
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE>");
		out.println("<link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1>TalkingBadge Demo Administrator</h1>");
		if (UsernameAdmin.equals(LogInAdmin.adminName)
				&& PasswordAdmin.equals(LogInAdmin.adminPasswd)) {
			String[] messageIDs = request.getParameterValues("message");
			if (messageIDs == null) {
				out.println("you didn't choose any message!<br>");
			} else {
				for (String messageID : messageIDs) {
					AConnection aConnection = TBService
							.dbQuery("select * from talkingbadge.messagerecord where id="
									+ messageID + ";");
					ResultSet aMessage = aConnection.resultSet;
					try {
						if (aMessage.next()) {
							if (action.equals("Reject")) {
								TBService
										.dbUpdate("UPDATE messagerecord SET success=-3 WHERE id='"
												+ messageID + "';");
								out.println("Message with id=" + messageID
										+ " is rejected!<br>");
							} else {
								TBService
										.dbUpdate("UPDATE messagerecord SET success=-1 WHERE id='"
												+ messageID + "';");
								out.println(TBService.sendMessageForTB(
										aMessage.getString("fromname"),
										aMessage.getString("tomac"),
										aMessage.getString("content"),
										aMessage.getString("language"),
										aMessage.getString("gender"), true)
										+ "<br>");
							}
						} else {
							out.println("Message with id=" + messageID
									+ " does not exist!<br>");
						}

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					aConnection.closeConnection();
				}

			}

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
