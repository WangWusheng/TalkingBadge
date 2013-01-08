import java.io.IOException;
import java.io.PrintWriter;

import javacode.TBService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LogIn
 */
@WebServlet("/LogIn")
public class LogIn extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// public static TBService tb;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LogIn() {
		super();
		// if (tb == null)
		// tb = new TBService();
		// TODO Auto-generated constructor stub
	}

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
		out.println("<h1><font color=\"#FFA500\">Welcome to the TalkingBadge Demo</font></h1>");
		out.println("<TABLE border=0> <TR> <TD>");
		out.println("<br><h2><font size=\"3\" face=\"verdana\" color=\"#800080\">Log in here</font></h2><br>");
		out.println("<form action=\"ListDevicesiSpeech\" method=\"post\">");
		out.println("Username: <input type=\"text\" name=\"Username\" /><br><br>");
		out.println("Password: <input type=\"password\" name=\"Password\" /><br><br>");
		out.println("<input type=\"submit\" value=\"Log In\" /><input type=\"reset\" value=\"Reset\" /></form><br>");
		// out.println("<img src =\"TB.png\" width=\"145\" height=\"245\">");
		// out.println("<div style=\"position:fixed; width:145px; height:245px; visibility:visible; right: 440px; top: 60px; border: 0px; background-image: url(TB.png); \"> </div>");
		out.println("</TD><TD>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		out.println("<img src =\"TB.png\" alt=\"TB\">");
		out.println("</TD> </TR> </TABLE> ");
		out.println("<h2><font size=\"3\" face=\"verdana\" color=\"#800080\">Registration and Installation</font></h2>");
		out.println("<br><font size=\"2\"><a href=\"Regist\">Create your account</a></font><br>");
		out.println("<br><font size=\"2\"><a href=\"TalkingBadge.apk\">Download the TalkingBadge Application for Android (4.0)</a></font><br>");
		out.println("<br><font size=\"2\"><a href=\"https://play.google.com/store/apps/details?id=itupku.genie.talkingbadge\">Find the TalkingBadge Application in Google Play</a></font><br>");

		out.println("<br><h2><font size=\"3\" face=\"verdana\" color=\"#800080\">Instructions and References</font></h2>");
		out.println("<br><font size=\"2\"><a href=\"TB_demo.pdf\">TalkingBadge Demo paper for NordiCHI 2012</a></font><br>");
		out.println("<br><font size=\"2\"><a href=\"TB_web_instruction.pdf\">Instruction for this website</a></font><br>");
		out.println("<br><font size=\"2\"><a href=\"TB_app_instruction.pdf\">Instruction for installing the TalkingBadge app on Android-based devices</a></font><br>");
		out.println("<br><font size=\"2\"><a href=\"http://tiger.itu.dk:8000/ITUitter/\">Bluetooth antennas distribution inside ITU building</a></font><br>");

		out.println("<br><h2><font size=\"3\" face=\"verdana\" color=\"#800080\">Scan the QR-code to access this website on your mobile</font></h2>");
		out.println("<br><img src =\"TBQR.png\" alt=\"TBQR\" width=\"100\" height=\"100\">");

		out.println("<br><h4><a href=\"LogInAdmin\">Log in as system administrator</a></h4>");
		out.println("</BODY></HTML>");

	}
}
