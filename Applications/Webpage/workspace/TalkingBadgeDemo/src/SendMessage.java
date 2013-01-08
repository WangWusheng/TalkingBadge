import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import javacode.AConnection;
import javacode.TBService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SendMessage
 */
@WebServlet("/SendMessage")
public class SendMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private boolean toPlaces = true;
	private String userGender = null;
	private boolean userValidate = false;
	private String Username = null;
	private String messageFrom;
	private String message;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SendMessage() {
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
		Username = request.getParameter("Username");
		String Password = request.getParameter("Password");

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE><link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1><font color=\"#FFA500\">TalkingBadge Demo</font></h1>");
		if (request.getHeader("Referer").contains("ListDevices")){
			toPlaces = false;
		//System.out.println(request.getHeader("Referer"));	
		}

		AConnection aConnection = TBService
				.dbQuery("select * from talkingbadge.user where name='"
						+ Username + "' and passwd='" + Password + "';");
		ResultSet resultSet = aConnection.resultSet;
		try {
			if (resultSet.next()) {
				userValidate = true;
				userGender = resultSet.getString("gender");
				if (!userGender.equals("M"))
					userGender = "F";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		aConnection.closeConnection();

		if (!userValidate) {
			out.println("You can not access this page directly, please ");
			out.println("<h3><a href=\"LogIn\">log in</a></h3>");
			out.println("</BODY></HTML>");
			return;
		}

		out.println("<h5>Welcome, " + Username
				+ "!<br><a href=\"LogIn\">Log out</a></h5>");

		messageFrom = request.getParameter("contentSource");
		if (messageFrom.equals("userInput"))
			message = request.getParameter("massage");
		else
			message = request.getParameter("preRecordMessage");
		message = new String(message.getBytes("ISO-8859-1"), "utf-8");
		// System.out.println("Message: "+message);
		if (message.replace(" ", "").equals("")) {
			out.println("you forgot to write a word!<br>");
		} else {
			if (!toPlaces) {
				String[] users = request.getParameterValues("users");
				String Language = request.getParameter("Language");

				if (users == null) {
					out.println("you didn't choose any user or there is no user in the selected places at this time!<br>");
				} else {
					// for (String user : users) {
					sendMessageTo(out, users, message, Language, userGender);
					// }
				}
			} else {
				String[] places = request.getParameterValues("places");
				String Language = request.getParameter("Language");
				boolean uerAvalible = false;
				if (places == null) {
					out.println("you didn't choose any place!<br>");
				} else {
					LinkedList<String> zonesToSend = new LinkedList<String>();
					for (String aPlace : places) {
						if (aPlace.equals("5")) {
							zonesToSend.add("itu.zone5.zone5b");
							zonesToSend.add("itu.zone5.zone5c");
							zonesToSend.add("itu.zone5.zone5d");
							zonesToSend.add("itu.zone5.zone5e");
						} else if (aPlace.equals("4")) {
							zonesToSend.add("itu.zone4.zone4b");
							zonesToSend.add("itu.zone4.zone4c");
							zonesToSend.add("itu.zone4.zone4c1");
							zonesToSend.add("itu.zone4.zone4d");
							zonesToSend.add("itu.zone4.zone4e");
						} else if (aPlace.equals("3")) {
							zonesToSend.add("itu.zone3.zone3b");
							zonesToSend.add("itu.zone3.zone3c");
							zonesToSend.add("itu.zone3.zone3d");
							zonesToSend.add("itu.zone3.zone3e");
						} else if (aPlace.equals("2")) {
							zonesToSend.add("itu.zone2.zone2b");
							zonesToSend.add("itu.zone2.zone2c");
							zonesToSend.add("itu.zone2.zone2d");
							zonesToSend.add("itu.zone2.zone2e");
						} else {
							zonesToSend.add(aPlace);
						}
					}
					// System.out.println(zonesToSend);
					LinkedList<String> theUsers = new LinkedList<String>();
					LinkedList<String> usersLocation = TBService
							.findUsersLocation(true);
					for (String userLocation : usersLocation) {
						String[] user = userLocation.split(" ");
						for (String zoneToSend : zonesToSend) {
							if (zoneToSend.equals(user[2])) {
								uerAvalible = true;
								//System.out.println(user[1]+user[2]);
								theUsers.add(user[1]);
							}
						}
					}
					if (!uerAvalible) {
						out.println("No TalkingBadge user exists in the selected places!<br>");

					} else {
						String[] userss = theUsers.toArray(new String[0]);
						sendMessageTo(out, userss, message, Language,
								userGender);
					}
				}
			}
		}

		out.println("<form action=\""
				+ (toPlaces ? "ListPlaces4NordiCHIiSpeech"
						: "ListDevicesiSpeech") + "\" method=\"post\">");
		out.println("<input type=\"hidden\" name=\"Username\" value=\""
				+ Username + "\"/>");
		out.println("<input type=\"hidden\" name=\"Password\" value=\""
				+ Password + "\"/>");
		out.println("<input type=\"submit\" value=\"Back\" />");
		out.println("</form>");

		out.println("</BODY></HTML>");
	}

	private void sendMessageTo(PrintWriter out, String[] users, String message,
			String language, String gender) {
		// if (user.equals("null")) {
		// out.println("Send file to "
		// + user
		// +
		// ": Send message file failed, because the device can not be found.<br>");
		// } else
		if (messageFrom.equals("userInput"))
			out.println(TBService.sendMessageForTBs(Username, users, message,
					language, gender, false) + "<br>");
		else {
			for (String user : users)
				out.println(TBService.sendPreRecordMessageForTB(Username, user,
						(gender.equals("M") ? "male" : "female") + message,
						language, gender));
		}
	}
}
