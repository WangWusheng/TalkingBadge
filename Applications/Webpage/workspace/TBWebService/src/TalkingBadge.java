

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

/**
 * Servlet implementation class TalkingBadge
 */
public class TalkingBadge extends HttpServlet {

	public static String listDevices() throws AxisFault {
		RPCServiceClient serviceClient = new RPCServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetEPR = new EndpointReference(
				"http://localhost:8080/TBWebService/services/TBWebService");
		options.setTo(targetEPR);
		Object[] opAddEntryArgs = new Object[] {};
		Class[] classes = new Class[] { String.class };
		QName opAddEntry = new QName("http://ws.apache.org/axis2",
				"getDevicesList");
		// 调用getGreeting方法并输出该方法的返回值
		return (String) serviceClient.invokeBlocking(opAddEntry,
				opAddEntryArgs, classes)[0];
	}

	public static String sendFile(String destMac, String content)
			throws AxisFault {
		RPCServiceClient serviceClient = new RPCServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetEPR = new EndpointReference(
				"http://localhost:8080/TBWebService/services/TBWebService");
		options.setTo(targetEPR);
		Object[] opAddEntryArgs = new Object[] { destMac, content };
		Class[] classes = new Class[] { String.class };
		QName opAddEntry = new QName("http://ws.apache.org/axis2", "sendFile");
		// 调用getGreeting方法并输出该方法的返回值
		return (String) serviceClient.invokeBlocking(opAddEntry,
				opAddEntryArgs, classes)[0];
	}

	public static String sendCommand(String destMac, String content)
			throws AxisFault {
		RPCServiceClient serviceClient = new RPCServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetEPR = new EndpointReference(
				"http://localhost:8080/TBWebService/services/TBWebService");
		options.setTo(targetEPR);
		Object[] opAddEntryArgs = new Object[] { destMac, content };
		Class[] classes = new Class[] { String.class };
		QName opAddEntry = new QName("http://ws.apache.org/axis2",
				"sendCommand");
		// 调用getGreeting方法并输出该方法的返回值
		return (String) serviceClient.invokeBlocking(opAddEntry,
				opAddEntryArgs, classes)[0];
	}

	public static String HelloAXIS() throws Exception {
		String targetEendPoint = "http://localhost:8080/axis/HelloAXIS.jws";
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setOperationName(new QName(targetEendPoint, "Hello"));
		call.setTargetEndpointAddress(new URL(targetEendPoint));
		String result = (String) call.invoke(new Object[] {});
		return result;
	}

	public static String WStestWeb() throws Exception {
		String targetEendPoint = "http://localhost:8080/WStestWeb/services/WStestWeb";
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setOperationName(new QName(targetEendPoint, "divide"));
		call.setTargetEndpointAddress(new URL(targetEendPoint));
		String result = (String) call.invoke(new Object[] { 4, 2 });
		return result;
	}

	public static String WStest() throws Exception {
		String targetEendPoint = "http://localhost:8080/WStestWeb/services/WStest";
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setOperationName(new QName(targetEendPoint, "chu"));
		call.setTargetEndpointAddress(new URL(targetEendPoint));
		String result = (String) call.invoke(new Object[] { 4, 16 });
		return result;
	}

	public static String getDevices() throws Exception {
		String targetEendPoint = "http://localhost:8080/WStestWeb/services/TBWebService";
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setOperationName(new QName(targetEendPoint, "getDevicesList"));
		call.setTargetEndpointAddress(new URL(targetEendPoint));
		String result = (String) call.invoke(new Object[] {});
		return result.toString();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>TalkingBadge Service</TITLE></HEAD>");
		out.println("<BODY>");
		out.println("<h1>Welcome to use the TalkingBadge webpage service</h1>");
		String devices = new String();
		try {
			devices = listDevices();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println("<table>");
		devices = devices.replace("Devices: ", "");
		if (!devices.equals("")) {
			String[] devicesArray = devices.split(" ");
			for(String device:devicesArray){
				out.println("<tr><td>"+device+"</td></tr>");
				
			}
		}
		out.println("</table>");
		out.println("</BODY></HTML>");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doGet(req, res);
	}
}
