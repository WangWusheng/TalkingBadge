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
 * Servlet implementation class UserValidate4App
 */
@WebServlet("/UserValidate4App")
public class UserValidate4App extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserValidate4App() {
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
		PrintWriter out = response.getWriter();
		String Username = request.getParameter("Username");
		String Password = request.getParameter("Password");
		String MAC = request.getParameter("MAC");

		// System.out.println(username+passwd);
		AConnection aConnection = TBService
				.dbQuery("select * from talkingbadge.user where name='"
						+ Username + "' AND passwd='" + Password
						+ "' AND TagUser='false';");
		ResultSet resultSet = aConnection.resultSet;
		try {
			if (resultSet.next()) {
				TBService.dbUpdate("UPDATE user SET mac='" + MAC
						+ "' WHERE name='" + Username + "';");
				out.println("1");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		aConnection.closeConnection();
		out.println("0");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
