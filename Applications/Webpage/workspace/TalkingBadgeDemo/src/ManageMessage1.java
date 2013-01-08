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
 * Servlet implementation class ManageMessage1
 */
@WebServlet("/ManageMessage1")
public class ManageMessage1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ManageMessage1() {
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

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE>");
		out.println("<script language=\"JavaScript\">function checkboxselect(itemname,checkstatus) {if(!itemname.length) {itemname.checked=checkstatus;	} else {for(var i=0;i<itemname.length;i++) { itemname[i].checked=checkstatus;}}}</script>");
		out.println("<link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1>TalkingBadge Demo Administrator</h1>");
		if (UsernameAdmin.equals(LogInAdmin.adminName)
				&& PasswordAdmin.equals(LogInAdmin.adminPasswd)) {

			AConnection aConnection = TBService
					.dbQuery("select * from talkingbadge.messagerecord where success=0 OR success=-2;");
			ResultSet messages = aConnection.resultSet;
			try {
				if (!messages.next()) {
					out.println("There is no message to approve<br>");
				} else {
					out.println("Please choose messages to approve<br>");
					out.println("<form name=\"messagesForm\" action=\"ManageMessage2\" method=\"post\">");
					out.println("<input type=\"hidden\" name=\"UsernameAdmin\" value=\""
							+ UsernameAdmin + "\"/>");
					out.println("<input type=\"hidden\" name=\"PasswordAdmin\" value=\""
							+ PasswordAdmin + "\"/>");
					// out.println("In success colomn, 1");
					out.println("<table border=\"1\">");
					out.println("<tr><th>Select All<br><input type=\"checkbox\" name=\"allmessages\" value=\"allmessages\" onClick=\"checkboxselect(document.messagesForm.message, document.messagesForm.allmessages.checked)\"/></th><th>Time</th><th>From</th><th>To</th><th>Content</th><th>Remark</th></tr>");

					do {
						out.println("<tr><td><input type=\"checkbox\" name=\"message\" value=\""
								+ messages.getInt("id")
								+ "\"/></td><td>"
								+ messages.getString("time")
								+ "</td><td>"
								+ messages.getString("fromname")
								+ "</td><td>"
								+ messages.getString("tomac")
								+ "</td><td>"
								+ messages.getString("content")
								+ "</td><td>"
								+ messages.getString("remark") + "</td></tr>");
					} while (messages.next());
					out.println("</table>");
					out.println("<input name=\"submit\" type=\"submit\" value=\"Send\" />");
					out.println("<input name=\"submit\" type=\"submit\" value=\"Reject\" /></form>");

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			aConnection.closeConnection();
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
