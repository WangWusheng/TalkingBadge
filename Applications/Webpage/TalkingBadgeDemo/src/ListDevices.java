import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javacode.TBService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ListDevices
 */
@WebServlet("/ListDevices")
public class ListDevices extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListDevices() {
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

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE>");
		out.println("<script language=\"JavaScript\">function checkboxselect(itemname,checkstatus) {if(!itemname.length) {itemname.checked=checkstatus;	} else {for(var i=0;i<itemname.length;i++) { itemname[i].checked=checkstatus;}}}</script>");
		
		out.println("<link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1>TalkingBadge Demo</h1>");

		if (!TBService.validateUser(Username, Password)) {
			out.println("Invalidate username or password, please ");
			out.println("<a href=\"LogIn\">log in</a>");
			out.println("</BODY></HTML>");
		} else {
			out.println("<h5>Welcome, " + Username
					+ "!<br><a href=\"LogIn\">Log out</a></h5>");

			out.println("<form action=\"ListDevices\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<input type=\"submit\" disabled=\"disabled\" value=\"Select Users\"/></form>");
			out.println("<form action=\"ListPlaces4NordiCHI\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<input type=\"submit\"  value=\"Select Places\"/></form>");

			out.println("<br>Select one or more users that you want to send message to:<br>");
			out.println("<form name=\"selectUserForm\" action=\"SendMessage\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<table border=\"1\">");
			out.println("<tr><th>Select All<br><input type=\"checkbox\" name=\"allusers\" value=\"allusers\" onClick=\"checkboxselect(document.selectUserForm.users, document.selectUserForm.allusers.checked)\"/></th><th>Nick Name</th><th>Current Location</th><th>Mac Address</th></tr>");

			LinkedList<String> usersLocation = TBService
					.findUsersLocation(true);
			for (String userLocation : usersLocation) {
				String[] user = userLocation.split(" ");
				out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"users\" value=\""
						+ user[1]
						+ "\" /></td><td>"
						+ user[0]
						+ "</td><td>"
						+ user[2] + "</td><td>" + user[1] + "</td></tr>");
			}
			out.println("</table>");

			out.println("<br>Enter your message here (no more than 140  characters)<br><textarea  maxlength=\"140\"  rows=\"3\" cols=\"50\" name=\"massage\"></textarea>");
			out.println("<br>Language: <select name=\"Language\"> <option value =\"en_GB\">English (UK)</option> <option value =\"en_US\">English (USA)</option> " +
					"<option value =\"da_DK\">Danish</option><option value =\"fi_FI\">Finnish</option><option value =\"no_NO\">Norwegian</option><option value =\"sv_SE\">Swedish</option></select><br>");
			
			out.println("<br><input type=\"submit\" value=\"Submit\" /><input type=\"reset\" value=\"Reset\" /></form><br>");

			out.println("</BODY></HTML>");
		}
	}

}
