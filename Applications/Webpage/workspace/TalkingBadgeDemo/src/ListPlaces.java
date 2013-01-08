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
 * Servlet implementation class ListPlaces
 */
@WebServlet("/ListPlaces")
public class ListPlaces extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListPlaces() {
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
			out.println("<h3><a href=\"LogIn\">log in</a></h3>");
			out.println("</BODY></HTML>");
		} else {
			out.println("<h5>Welcome, " + Username
					+ "!<br><a href=\"LogIn\">Log out</a></h5>");

			out.println("<form action=\"ListDevices\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<input type=\"submit\" value=\"Select Users\"/></form>");
			out.println("<form action=\"ListPlaces\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<input type=\"submit\" disabled=\"disabled\"  value=\"Select Places\"/></form>");

			out.println("<br>Select one or more places that you want to send message to :<br>");
			out.println("<form name=\"selectPlaceForm\" action=\"SendMessage\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<table border=\"1\">");
			out.println("<tr align=\"center\"><th>Select All<br><input type=\"checkbox\" name=\"allplaces\" value=\"allplaces\" onClick=\"checkboxselect(document.selectPlaceForm.places, document.selectPlaceForm.allplaces.checked)\"/></th><th>Position</th></tr>");

			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone5.zone5b\" /></td><td>itu.zone5.zone5b</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone5.zone5c\" /></td><td>itu.zone5.zone5c</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone5.zone5d\" /></td><td>itu.zone5.zone5d</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone5.zone5e\" /></td><td>itu.zone5.zone5e</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone4.zone4b\" /></td><td>itu.zone4.zone4b</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone4.zone4c\" /></td><td>itu.zone4.zone4c</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone4.zone4c1\" /></td><td>itu.zone4.zone4c1</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone4.zone4d\" /></td><td>itu.zone4.zone4d</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone4.zone4e\" /></td><td>itu.zone4.zone4e</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone3.zone3b\" /></td><td>itu.zone3.zone3b</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone3.zone3c\" /></td><td>itu.zone3.zone3c</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone3.zone3d\" /></td><td>itu.zone3.zone3d</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone3.zone3e\" /></td><td>itu.zone3.zone3e</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone2.zone2b\" /></td><td>itu.zone2.zone2b</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone2.zone2c\" /></td><td>itu.zone2.zone2c</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone2.zone2d\" /></td><td>itu.zone2.zone2d</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone2.zone2e\" /></td><td>itu.zone2.zone2e</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone2.zone2m18\" /></td><td>itu.zone2.zone2m18</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone2.zone2m28\" /></td><td>itu.zone2.zone2m28</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone2.zone2m31\" /></td><td>itu.zone2.zone2m31</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone2.zone2m52\" /></td><td>itu.zone2.zone2m52</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone1.zone1c\" /></td><td>itu.zone1.zone1c</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone1.zonelaesesal\" /></td><td>itu.zone1.zonelaesesal</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zoneaud2\" /></td><td>itu.zone0.zoneaud2</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonekandisk\" /></td><td>itu.zone0.zonekandisk</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zoneanalog\" /></td><td>itu.zone0.zoneanalog</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zoneaud1\" /></td><td>itu.zone0.zoneaud1</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonekanvindfang\" /></td><td>itu.zone0.zonekanvindfang</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonekanspisesal\" /></td><td>itu.zone0.zonekanspisesal</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonescroll\" /></td><td>itu.zone0.zonescroll</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zone0c\" /></td><td>itu.zone0.zone0c</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonedornord\" /></td><td>itu.zone0.zonedornord</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonedorsyd\" /></td><td>itu.zone0.zonedorsyd</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone-1.zonekaeldernord\" /></td><td>itu.zone-1.zonekaeldernord</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone-1.zonekaeldersyd\" /></td><td>itu.zone-1.zonekaeldersyd</td></tr>");

			out.println("</table>");

			out.println("<br>Enter your message here (no more than 140  characters)<br><textarea maxlength=\"140\" rows=\"3\" cols=\"50\" name=\"massage\"></textarea>");
			out.println("<br>Language: <select name=\"Language\">   <option value =\"en_GB\">English (UK)</option> <option value =\"en_US\">English (USA)</option> "
					+ "<option value =\"da_DK\">Danish</option><option value =\"fi_FI\">Finnish</option><option value =\"no_NO\">Norwegian</option><option value =\"sv_SE\">Swedish</option></select><br>");

			out.println("<br><input type=\"submit\" value=\"Submit\" /><input type=\"reset\" value=\"Reset\" /></form><br>");

			out.println("</BODY></HTML>");
		}
	}

}
