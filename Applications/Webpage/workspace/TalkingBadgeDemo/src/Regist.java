import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Regist
 */
@WebServlet("/Regist")
public class Regist extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Regist() {
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
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Demo</TITLE><link rel=\"stylesheet\" href=\"TalkingBadgeDemo.css\" type=\"text/css\"></HEAD>");
		out.println("<BODY>");
		out.println("<h1><font color=\"#FFA500\">TalkingBadge Demo</font></h1>");

		out.println("<h2>We collect all these information for research purpose. <br>We will not give the information to any third-part organizations.<br><br><h2>");
		out.println("<form action=\"RegistResult\" method=\"post\">");
		out.println("Username: <br><input type=\"text\" name=\"Username\" /><br>");
		out.println("Password: <br><input type=\"password\" name=\"Password\" /><br>");
		out.println("RepeatPassword: <br><input type=\"password\" name=\"RepeatPassword\" /><br>");
		out.println("Email address: <br><input type=\"text\" name=\"Email\" /><br>");
		out.println("Gender: <br><select name=\"Gender\">  <option value =\"M\">Male</option>  <option value =\"F\">Female</option></select><br>");
		out.println("Age: <br><input type=\"text\" name=\"Age\" /><br>");
		out.println("Are you a student? <br><select name=\"StudentStatus\">  <option value =\"Yes\">Yes</option>  <option value =\"No\">No</option></select><br>");
		out.println("Where do you currently live? <br><input type=\"text\" name=\"Live\" /><br>");
		out.println("Where were you born? <br><input type=\"text\" name=\"Born\" /><br>");
		out.println("Which of the following apps that use your location information is currently installed on your phone? <font  font-weight:lighter><br><input type=\"checkbox\" name=\"InstalledApps\" value=\"Foursquare\" />Foursquare<br><input type=\"checkbox\" name=\"InstalledApps\" value=\"Gowalla\" />Gowalla<br><input type=\"checkbox\" name=\"InstalledApps\" value=\"Google Latitude\" />Google Latitude<br><input type=\"checkbox\" name=\"InstalledApps\" value=\"Facebook Places\" />Facebook Places<br><input type=\"checkbox\" name=\"InstalledApps\" value=\"Twitter\" />Twitter (revealing geographic location of tweet)<br><input type=\"checkbox\" name=\"InstalledApps\" value=\"Iphone Find My Friends\" />iPhone Find My Friends<br><input type=\"checkbox\" name=\"InstalledApps\" value=\"Yelp\" />Yelp</font><br>");
		out.println("How often do you use any of these apps (overall)? <br><select name=\"Frequency\">  <option value =\"Every day\">Every day</option>  <option value =\"Pretty often but not daily\">Pretty often but not daily</option> <option value =\"Occasionally\">Occasionally</option> <option value =\"Tried them once or twice but no more\">Tried them once or twice but no more</option> <option value =\"Never\">Never</option></select><br>");
		out.println("How concerned are you about revealing your location to others via these apps? [On a scale of 1-5 from 1 = very concerned to 5 = not at all concerned]<br><select name=\"Concern\">  <option value =\"1\">1</option>  <option value =\"2\">2</option> <option value =\"3\">3</option> <option value =\"4\">4</option> <option value =\"5\">5</option></select><br>");

		out.println("<input type=\"submit\" value=\"Register\" /><input type=\"reset\" value=\"Reset\" /></form><br>");
		out.println("<h3><a href=\"LogIn\">Log in</a></h3>");
		out.println("</table>");
		out.println("</BODY></HTML>");

	}

}
