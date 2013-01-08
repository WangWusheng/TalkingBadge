import java.io.IOException;
import java.io.PrintWriter;

import javacode.TBService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ListPlaces4NordiCHIiSpeech
 */
@WebServlet("/ListPlaces4NordiCHIiSpeech")
public class ListPlaces4NordiCHIiSpeech extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListPlaces4NordiCHIiSpeech() {
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
		out.println("<h1><font color=\"#FFA500\">TalkingBadge Demo</font></h1>");
		if (!TBService.validateUser(Username, Password)) {
			out.println("Invalidate username or password, please ");
			out.println("<h3><a href=\"LogIn\">log in</a></h3>");
			out.println("</BODY></HTML>");
		} else {
			out.println("<h5>Welcome, " + Username
					+ "!<br><a href=\"LogIn\">Log out</a></h5>");

			out.println("<form action=\"ListDevicesiSpeech\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<input type=\"submit\" value=\"Send a message to selected users\"/></form>");
			out.println("......or<form action=\"ListPlaces4NordiCHIiSpeech\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<input type=\"submit\" disabled=\"disabled\"  value=\"Send a message for people who are currently at specific locations\"/></form>");

			out.println("<br><h2><font size=\"2\" face=\"verdana\" color=\"#800080\">Select one or more places that you want to send message to :</font></h2><br>");
			out.println("<form name=\"selectPlaceForm\" action=\"SendMessage\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<table border=\"1\">");
			out.println("<tr align=\"center\"><th>Select All<br><input type=\"checkbox\" name=\"allplaces\" value=\"allplaces\" onClick=\"checkboxselect(document.selectPlaceForm.places, document.selectPlaceForm.allplaces.checked)\"/></th><th>Position</th></tr>");

			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zoneaud1\" /></td><td>Aud 1</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zoneaud2\" /></td><td>Aud 2</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonescroll\" /></td><td>Scroll bar</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zoneanalog\" /></td><td>Analog</td></tr>");

			// out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonekandisk\" /></td><td>Kandisk</td></tr>");
			// out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonekanvindfang\" /></td><td>itu.zone0.zonekanvindfang</td></tr>");
			// out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonekanspisesal\" /></td><td>itu.zone0.zonekanspisesal</td></tr>");
			// out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zone0c\" /></td><td>itu.zone0.zone0c</td></tr>");
			// out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonedornord\" /></td><td>itu.zone0.zonedornord</td></tr>");
			// out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"itu.zone0.zonedorsyd\" /></td><td>itu.zone0.zonedorsyd</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"2\" /></td><td>Floor 2</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"3\" /></td><td>Floor 3</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"4\" /></td><td>Floor 4</td></tr>");
			out.println("<tr align=\"center\"><td><input type=\"checkbox\" name=\"places\" value=\"5\" /></td><td>Floor 5</td></tr>");

			out.println("</table>");

			//out.println("<br><br><input type=\"radio\" name=\"contentSource\" value = \"userInput\" checked=\"checked\">Input a persionalized message");
			out.println("<input type=\"hidden\" name=\"contentSource\" value = \"userInput\"");
			out.println("<br><br><h2><font size=\"2\" face=\"verdana\" color=\"#800080\">Enter your message here (no more than 140  characters)</font></h2><br><textarea maxlength=\"140\" rows=\"3\" cols=\"50\" name=\"massage\"></textarea>");
			out.println("<br><h2><font size=\"2\" face=\"verdana\" color=\"#800080\">Language: </font></h2><br><select name=\"Language\"> <option value =\"ukenglish\">English (UK)</option> <option value =\"usenglish\">English (USA)</option> "
					+ "<option value =\"auenglish\">English (Australia)</option><option value =\"caenglish\">English (Canada)</option>"
					+ "<option value =\"eurdanish\">Danish</option><option value =\"eurfinnish\">Finnish</option><option value =\"eurnorwegian\">Norwegian</option><option value =\"swswedish\">Swedish</option>"
					+ "<option value =\"chchinese\">Chinese (Mainland)</option>"
					+ "<option value =\"hkchinese\">Chinese (HongKong)</option><option value =\"twchinese\">Chinese (Taiwan)</option>"
					+ "<option value =\"eurczech\">Czech</option><option value =\"eurdutch\">Dutch</option>"
					+ "<option value =\"eurfrench\">French (France)</option><option value =\"cafrench\">French (Canada)</option>"
					+ "<option value =\"eurgerman\">German</option><option value =\"eurgreek\">Greek</option><option value =\"huhungarian\">Hungarian</option><option value =\"euritalian\">Italian</option><option value =\"jpjapanese\">Japanese</option><option value =\"krkorean\">Korean</option>"
					+ "<option value =\"eurpolish\">Polish</option><option value =\"brportuguese\">Portuguese</option>"
					+ "<option value =\"rurussian\">Russian</option><option value =\"eurspanish\">Spanish</option>"
					+ "<option value =\"eurturkish\">Turkish</option>"
					+ "</select><br>");
//			out.println("<br><br><input type=\"radio\" name=\"contentSource\" value = \"preRecord\">Select a pre-prepared message <br>");
//			out.println("<br>Message: <select name=\"preRecordMessage\"> "
//					+ "<option value =\"floor2en.mp3\">You are now on the second floor.</option>"
//					+ "<option value =\"floor2dk.mp3\">Du er nu på anden sal.</option>"
//					+ "<option value =\"floor2ch.mp3\">您现在在二层.</option>"
//					+ "<option value =\"floor3en.mp3\">You are now on the third floor.</option>"
//					+ "<option value =\"floor3dk.mp3\">Du er nu på tredie sal.</option>"
//					+ "<option value =\"floor3ch.mp3\">您现在在三层.</option>"
//					+ "<option value =\"floor4en.mp3\">You are now on the fourth floor.</option>"
//					+ "<option value =\"floor4dk.mp3\">Du er nu på fjerede sal.</option>"
//					+ "<option value =\"floor4ch.mp3\">您现在在四层.</option>"
//					+ "<option value =\"floor5en.mp3\">You are now on the fifth floor.</option>"
//					+ "<option value =\"floor5dk.mp3\">Du er nu på femte sal.</option>"
//					+ "<option value =\"floor5ch.mp3\">您现在在五层.</option>"
//					+ "<option value =\"happyen.mp3\">Have a nice day.</option>"
//					+ "<option value =\"happydk.mp3\">Ha en god dag.</option>"
//					+ "<option value =\"happych.mp3\">祝您今天愉快.</option>"
//					+ "</select><br>");

			out.println("<br><input type=\"submit\" value=\"Send\" /><input type=\"reset\" value=\"Reset\" /></form><br>");

			out.println("</BODY></HTML>");
		}
	}

}
