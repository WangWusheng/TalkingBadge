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
 * Servlet implementation class ListDevicesiSpeech
 */
@WebServlet("/ListDevicesiSpeechAdmin")
public class ListDevicesiSpeechAdmin extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListDevicesiSpeechAdmin() {
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

			out.println("<form action=\"ListDevicesiSpeech\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"Username\" value=\""
					+ Username + "\"/>");
			out.println("<input type=\"hidden\" name=\"Password\" value=\""
					+ Password + "\"/>");
			out.println("<input type=\"submit\" disabled=\"disabled\" value=\"Select Users\"/></form>");
			out.println("<form action=\"ListPlaces4NordiCHIiSpeech\" method=\"post\">");
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
			out.println("<br><br><input type=\"radio\" name=\"contentSource\" value = \"userInput\" checked=\"checked\">Input a persionalized message");
			out.println("<br>Enter your message here (no more than 140  characters)<br><textarea  maxlength=\"140\"  rows=\"3\" cols=\"50\" name=\"massage\"></textarea>");
			out.println("<br>Language: <select name=\"Language\"> <option value =\"ukenglish\">English (UK)</option> <option value =\"usenglish\">English (USA)</option> "
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
			out.println("<br><br><input type=\"radio\" name=\"contentSource\" value = \"preRecord\">Select a pre-prepared message <br>");
			out.println("<br>Message: <select name=\"preRecordMessage\"> "
					+ "<option value =\"floor2en.mp3\">You are now on the second floor.</option>"
					+ "<option value =\"floor2dk.mp3\">Du er nu pÃ¥ anden sal.</option>"
					+ "<option value =\"floor2ch.mp3\">æ‚¨çŽ°åœ¨åœ¨äºŒå±‚.</option>"
					+ "<option value =\"floor3en.mp3\">You are now on the third floor.</option>"
					+ "<option value =\"floor3dk.mp3\">Du er nu pÃ¥ tredie sal.</option>"
					+ "<option value =\"floor3ch.mp3\">æ‚¨çŽ°åœ¨åœ¨ä¸‰å±‚.</option>"
					+ "<option value =\"floor4en.mp3\">You are now on the fourth floor.</option>"
					+ "<option value =\"floor4dk.mp3\">Du er nu pÃ¥ fjerede sal.</option>"
					+ "<option value =\"floor4ch.mp3\">æ‚¨çŽ°åœ¨åœ¨å››å±‚.</option>"
					+ "<option value =\"floor5en.mp3\">You are now on the fifth floor.</option>"
					+ "<option value =\"floor5dk.mp3\">Du er nu pÃ¥ femte sal.</option>"
					+ "<option value =\"floor5ch.mp3\">æ‚¨çŽ°åœ¨åœ¨äº”å±‚.</option>"
					+ "<option value =\"happyen.mp3\">Have a nice day.</option>"
					+ "<option value =\"happydk.mp3\">Ha en god dag.</option>"
					+ "<option value =\"happych.mp3\">ç¥�æ‚¨ä»Šå¤©æ„‰å¿«.</option>"
					+ "</select><br>");
			out.println("<br><input type=\"submit\" value=\"Send\" /><input type=\"reset\" value=\"Reset\" /></form><br>");

			out.println("</BODY></HTML>");
		}
	}

}
