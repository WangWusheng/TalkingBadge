import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javacode.AConnection;
import javacode.TBService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PreListen
 */
@WebServlet("/PreListen")
public class PreListen extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PreListen() {
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
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();

		String Username = request.getParameter("Username");
		String userGender = "F";
		String message = request.getParameter("massage");
		String Language = request.getParameter("Language");

		AConnection aConnection = TBService
				.dbQuery("select * from talkingbadge.user where name='"
						+ Username + "';");
		ResultSet resultSet = aConnection.resultSet;
		try {
			if (resultSet.next()) {
				userGender = resultSet.getString("gender");
				if (!userGender.equals("M"))
					userGender = "F";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		aConnection.closeConnection();

		String TTSGenderFormat = userGender.equals("F") ? "female" : "male";
		String TTSOppositeGenderFormat = userGender.equals("F") ? "male"
				: "female";
		String result = Language + TTSGenderFormat;
		for (String voice : TBService.iSpeechVOICE_LIST) {
			// System.out.println(voice[0]+voice[4]+voice[5]);
			if (voice.equals(result)) {
				Language = result;
				break;
			}
		}
		if (Language != result) {
			Language = Language + TTSOppositeGenderFormat;
		}

		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE>");

		// out.println("<meta http-equiv=\"refresh\" content=\"5; url=http://api.ispeech.org/api/rest?apikey=81ee22255c029d5755445fb11c2f6a40&action=convert&voice="
		// + Language + "&text=" + message + "\" />");
		out.println("<BODY>");
		out.println("<script>window.open(\"http://api.ispeech.org/api/rest?apikey=81ee22255c029d5755445fb11c2f6a40&action=convert&voice="
				+ Language + "&text=" + message + "\");</script>");

		out.println("</BODY></HTML>");
		// request.setAttribute("Username", request.getParameter("Username"));
		// request.setAttribute("Password", request.getParameter("Password"));
		String address = request.getHeader("Referer");
		if (!address.contains("?")) {
			address = request.getHeader("Referer") + "?Username="
					+ request.getParameter("Username") + "&Password="
					+ request.getParameter("Password");
		}
		// RequestDispatcher dispatcher = getServletContext()
		// .getRequestDispatcher(address.substring(address.lastIndexOf("/")));
		// dispatcher.forward(request, response);
		// response.sendRedirect(address);
	}
}
