package com.FCI.SWE.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import sun.tools.jar.resources.jar;

import com.FCI.SWE.Models.User;
import com.FCI.SWE.ServicesModels.UserEntity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.urlfetch.HTTPRequest;

import java.lang.String;

/**
 * This class contains REST services, also contains action function for web
 * application
 * 
 * @author Mohamed Samir
 * @version 1.0
 * @since 2014-02-12
 *
 */
@Path("/")
@Produces("text/html")
public class UserController {
	/**
	 * Action function to render Signup page, this function will be executed
	 * using url like this /rest/signup
	 * 
	 * @return sign up page
	 */
	@GET
	@Path("/signup")
	public Response signUp() {
		return Response.ok(new Viewable("/jsp/register")).build();
	}
	@GET
	@Path("/test")
	public Response t() {
		return Response.ok(new Viewable("/jsp/test")).build();
	}


	/**
	 * Action function to render home page of application, home page contains
	 * only signup and login buttons
	 * 
	 * @return enty point page (Home page of this application)
	 */
	@GET
	@Path("/")
	public Response index() {
		return Response.ok(new Viewable("/jsp/entryPoint")).build();
	}

	/**
	 * Action function to render login page this function will be executed using
	 * url like this /rest/login
	 * 
	 * @return login page
	 */
	@GET
	@Path("/login")
	public Response login( ) {
		// @Context HttpServletRequest req
//		HttpSession session= req.getSession(true);
//		req.setAttribute("map", "123");
//		session.setAttribute("map","123");
		Map<String ,String>map = new HashMap<String ,String>();
		map.put("message","");
		return Response.ok(new Viewable("/jsp/login", null)).build();
	}

	/**
	 * Action function to response to signup request, This function will act as
	 * a controller part and it will calls RegistrationService to make
	 * registration
	 * 
	 * @param uname
	 *            provided user name
	 * @param email
	 *            provided user email
	 * @param pass
	 *            provided user password
	 * @return Status string
	 */
	@POST
	@Path("/response")
	@Produces(MediaType.TEXT_PLAIN)
	public String response(@FormParam("uname") String uname,
			@FormParam("email") String email, @FormParam("password") String pass) {
		String serviceUrl = "http://localhost:8888/rest/RegistrationService";
		try {
			URL url = new URL(serviceUrl);
			String urlParameters = "uname=" + uname + "&email=" + email
					+ "&password=" + pass;
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(60000);  //60 Seconds
			connection.setReadTimeout(60000);  //60 Seconds
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(urlParameters);
			writer.flush();
			String line, retJson = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			while ((line = reader.readLine()) != null) {
				retJson += line;
			}
			writer.close();
			reader.close();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("OK"))
				return "Registered Successfully";
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * UserEntity user = new UserEntity(uname, email, pass);
		 * user.saveUser(); return uname;
		 */
		return "Failed";
	}

	/**
	 * Action function to response to login request. This function will act as a
	 * controller part, it will calls login service to check user data and get
	 * user from datastore
	 * 
	 * @param uname
	 *            provided user name
	 * @param pass
	 *            provided user password
	 * @return Home page view
	 */
	@POST
	@Path("/home")
	@Produces("text/html")
	public Response home(@Context HttpServletRequest req, @FormParam("uname") String uname,
			@FormParam("password") String pass) {
		String serviceUrl = "http://localhost:8888/rest/LoginService";
		Map<String, String> map = new HashMap<String, String>();
		try 
		{
			URL url = new URL(serviceUrl);
			String urlParameters = "uname=" + uname + "&password=" + pass;
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(60000);  //60 Seconds
			connection.setReadTimeout(60000);  //60 Seconds
			
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(urlParameters);
			writer.flush();
			String line, retJson = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			while ((line = reader.readLine()) != null) {
				retJson += line;
			}
			writer.close();
			reader.close();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("Failed"))
			{
				map.put("message","Wrong email or password \n");
				
				map.put("Status", "Failed");
				return Response.ok(new Viewable("/jsp/login", map)).build();				
			}
			
			
			User user = User.getUser(object.toJSONString());
			System.out.println("username "+ User.getCurrentActiveUser().getName());
			map.put("name", user.getName());
			map.put("email", user.getEmail());
			map.put("password", user.getPass());
			req.getSession(true).setAttribute("1", 2);
			
			HttpSession session= req.getSession(true);
			req.getSession(true).setAttribute("name",user.getName());
			req.getSession(true).setAttribute("email", user.getEmail());
			req.getSession(true).setAttribute("password", user.getPass());
			
			
			return Response.ok(new Viewable("/jsp/home", map)).build();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * UserEntity user = new UserEntity(uname, email, pass);
		 * user.saveUser(); return uname;
		 */
		 
		
//		HttpSession session= req.getSession(true);
//		req.setAttribute("map", "123");
//		session.setAttribute("map","123");
		
		map.put("Status", "Failed");
		map.put("message","Wrong email or password \n");
		
		return Response.ok(new Viewable("/jsp/login", map)).build();	

	}

////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param sUser username of the user who send the request
	 * @param fUser username of the user who accept the request
	 * @param password is the password of the user who sent the request
	 * @return status of service OK or Failed
	 */
	@POST
	@Path("/FriendRequest")
	public String sendFriendRequest(@FormParam("senderUser")String sUser,
			@FormParam("friendUser") String fUser, @FormParam("senderPassword")String password) {
		JSONObject object ;
		JSONObject returnObject = new JSONObject();
		String serviceUrl = "http://localhost:8888/rest/FriendRequestService";
		
		try {
			URL url = new URL(serviceUrl);
			String urlParameters = "senderUser=" + sUser + "&friendUser=" + fUser
					+ "&senderPassword=" + password;
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(60000);  //60 Seconds
			connection.setReadTimeout(60000);  //60 Seconds
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(urlParameters);
			writer.flush();
			String line, retJson = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			while ((line = reader.readLine()) != null) {
				retJson += line;
			}
			writer.close();
			reader.close();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(retJson);
			object = (JSONObject) obj;
			
			if (object.get("Status").equals("Failed"))
			{
				returnObject.put("Status", "Failed");
				
			}	
			else 
				returnObject.put("Status", "OK");
			
			return returnObject.toString();
			
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		returnObject.put("Status", "Failed");
		return returnObject.toString();
		
	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * accept friend request and make the 2 users friends and delete the request
	 * @param sUser username of the user who send the request
	 * @param fUser username of the user who accept the request
	 * @param password is the password of the user who accept the request
	 * @return status of service OK or Failed
	 */
	@POST
	@Path("/AddFriend")
	public String addFriend(@FormParam("senderUser")String sUser, @FormParam("friendUser") String fUser,
			@FormParam("friendPassword")String password) {
		JSONObject object ;
		JSONObject returnObject = new JSONObject();
		String serviceUrl = "http://localhost:8888/rest/AddFriendService";
		
		try {
			URL url = new URL(serviceUrl);
			String urlParameters = "senderUser=" + sUser + "&friendUser=" + fUser
					+ "&senderPassword=" + password;
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(60000);  //60 Seconds
			connection.setReadTimeout(60000);  //60 Seconds
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(urlParameters);
			writer.flush();
			String line, retJson = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			while ((line = reader.readLine()) != null) {
				retJson += line;
			}
			writer.close();
			reader.close();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(retJson);
			object = (JSONObject) obj;
			
			if (object.get("Status").equals("Failed"))
			{
				returnObject.put("Status", "Failed");
				
			}	
			else 
				object.put("Status", "OK");
			
			return returnObject.toString();
			
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		returnObject.put("Status", "Failed");
		return returnObject.toString();
		
	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * to cancel a sent friend 
	 * @param sUser : username that send the request 
	 * @param fUser : username of the request receiver 
	 * @param password: password of sender user 
	 * @return
	 */
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/CancelFriendRequest")
	@POST
	public String  cancelFriendRequest (@FormParam("friendUser") String fUser, 
								@FormParam("senderUser")String sUser,  @FormParam("senderPassword")String password)
	{
		JSONObject object = new JSONObject();
		
		
		
		return object.toString();
	}
	
	///////////////////////////////////////////////////////////////
	/**
	 * get all friends of specific user 
	 * @param uName username 
	 * @param password his password
	 * @return JSONArray carrying status and friends list 
	 */
	@Path("/MyFriends")
	@Produces("text/html")
	@POST
	public Response myFriends(@FormParam("uName")String uName, 
			@FormParam("password")String password) {
		
		JSONArray object = new JSONArray();
		
		
		String serviceUrl = "http://localhost:8888/rest/GetFriends";
		Map<String , Vector<String>>map = null;
		try {
			URL url = new URL(serviceUrl);
			String urlParameters = "uName=" + uName + "&password=" + password ;
					
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(60000);  //60 Seconds
			connection.setReadTimeout(60000);  //60 Seconds
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(urlParameters);
			writer.flush();
			String line, retJson = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			while ((line = reader.readLine()) != null) {
				retJson += line;
			}
			writer.close();
			reader.close();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(retJson);
			
			object = (JSONArray)obj;
			//System.out.println(object.toString());
			Vector<String> v = new Vector<String>();
			 map = new HashMap<String , Vector<String>>();
			for (int i = 1; i < object.size(); i++) {
				v.add((String) object.get(i));
			}
			
			map.put("myFriends", v);
			return Response.ok(new Viewable("/jsp/myFriends",map)).build();
			
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		object.add("Failed");
		
		return Response.ok(new Viewable("/jsp/myFriends",map)).build();
		

	}
	
	/////////////////////////////////////////////////////////
	/**
	 * get all received friend requests
	 * @param uName
	 * @param password
	 * @returnk8
	 */
	
	@Path("/ReceivedFriendRequests")
	@Produces("text/html")
	@POST
	public Response receivedFriendRequests(@FormParam("uName")String uName, 
			@FormParam("password")String password) {
		
		JSONArray object = new JSONArray();
		String serviceUrl = "http://localhost:8888/rest/GetFriendRequests";
		Map<String , Vector<String>>map = null;
			
		System.out.println("User " +uName + " pass " + password);
		try {
			URL url = new URL(serviceUrl);
			String urlParameters = "uName=" + uName + "&password=" + password ;
					
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(60000);  //60 Seconds
			connection.setReadTimeout(60000);  //60 Seconds
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(urlParameters);
			writer.flush();
			String line, retJson = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			while ((line = reader.readLine()) != null) {
				retJson += line;
			}
			writer.close();
			reader.close();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(retJson);
			
			object = (JSONArray)obj;
			//System.out.println(object.toString());
			Vector<String> v = new Vector<String>();
			
			 map = new HashMap<String , Vector<String>>();
			for (int i = 0; i < object.size(); i++) {
				v.add((String) object.get(i));
			}
			System.out.println("Size " + v.size() + "  status" + v.get(0));
			
			map.put("FriendRequests", v);
			System.out.println("Vector");
			for (int i = 0; i <v.size(); i++) {
				System.out.println(v.get(i));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			object.add("Failed");
			
		}
		
		return Response.ok(new Viewable("/jsp/FriendRequests",map)).build();

	}

	
///////////////////////////////////////////////////////////////
	/**
	 * signout the user and close the session and send him to entry page 
	 * @param request
	 * @return
	 */
	@Path("/signout")
	@GET

	public Response signout (@Context HttpServletRequest request)
	{
		User.signOut();
		request.getSession().invalidate();
		
		return Response.ok(new Viewable("/jsp/entryPoint")).build();
	}
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////	
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	@GET
	public String test()
	{
		// here we declare the JSONArray and put some data to send it 
		JSONArray jArray  = new JSONArray();
		jArray.add("OK");
		jArray.add("1");
		jArray.add("2");
		jArray.add("3");
		System.out.println(jArray.toString());
		return jArray.toString();
	}
	
	
	@Path("/viewtest")
	@GET
	public Response viewtest(@Context HttpServletRequest req) {
		// now we will send data using sessions firstly we have to send HTTPRequest request as a parameter
				//@Context HttpServletRequest request
				HttpSession session= req.getSession(true);
				
				req.getSession(true).setAttribute("param1","1");
				req.getSession(true).setAttribute("param2", "2");
				
				
		return Response.ok(new Viewable("/jsp/testJSONArray")).build();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
}