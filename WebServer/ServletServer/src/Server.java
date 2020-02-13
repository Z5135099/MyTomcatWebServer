import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import pigeongroup.APIWorker.EventbriteAPI;
import pigeongroup.Data.Event;
import pigeongroup.Data.MessageData;
import pigeongroup.Data.User;
import pigeongroup.DataCommon.DataCommonHandle;
import pigeongroup.DataCommon.DataCommonManager;

/**
 * Servlet implementation class Server
 */
@WebServlet("/Server")
public class Server extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HashMap<String, String> tokenMap = new HashMap<String, String>();
	private HashMap<String, String> locationMap = new HashMap<String, String>();
	private int counter = 0;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Server() {
        super();
    }
    
    private boolean checkToken(HttpServletRequest request) {
    	String token = "";
    	String userName = "";
    	
    	if(request.getMethod().equals("GET")) {
    		token = request.getHeader("Token");
    		userName = request.getHeader("UserName");
    	}else {
    		token = request.getParameter("Token");
    		userName = request.getParameter("UserName");
    	}
    	
    	if(token == null) {
    		return false;
    	}
    	
		String storeToken = tokenMap.get(userName);
		
		System.out.println("receive: " + token);
		System.out.println("right: " + storeToken);
		
		if((storeToken == null) || (!storeToken.equals(token))){
			return false;
		}

    	return true;
    }
    
    private void sendRespond(HttpServletResponse response, String message) throws IOException {
    	//response.setHeader("Access-Control-Allow-Origin", "*");
    	//response.setHeader("Access-Control-Allow-Methods", "GET, POST");
    	//response.setHeader("Access-Control-Allow-Headers", "Origin, X-requested-with, Content-Type, Accept");
    	
    	response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(message);
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Receive GET");
		System.out.println(request.getHeader("MessageType"));
		
		if(checkToken(request)) {
			if(request.getHeader("MessageType").equals("NormalUpdate")) {
				System.out.println("normal update");
				EventbriteAPI contributer = new EventbriteAPI();
				LinkedList<Event> eventList = contributer.getEventInfoFromApi("UNSW", "5");
				String userName = request.getHeader("UserName");
				String jsonStr = "{\"username\":\"";
				jsonStr += userName;
				jsonStr += "\"";
				jsonStr += ",\"events\":[";
				
				for(int i = 0; i < eventList.size(); i++){
					
					if(i != 0) {
						jsonStr += ",";
					}
					
					jsonStr += "{";
					
					jsonStr += "\"name\":\"";
					Event event = eventList.get(i);
					String str = event.name.replace("\"", "");
					jsonStr += str;
					jsonStr += "\"";
					
					jsonStr += ",\"id\":\"";
					jsonStr += event.id;
					jsonStr += "\"";
					
					jsonStr += ",\"destination\":\"";
					if(!(this.locationMap.containsKey(event.id)) || counter >= 10) {
						JSONObject locations = contributer.getEventLocation(event.id);
						String eventLocation = locations.getString("localized_address_display").toString();
						jsonStr += eventLocation;
						this.locationMap.put(event.id, eventLocation);
						counter = 0;
						
					}else {
						jsonStr += this.locationMap.get(event.id);
					}
					jsonStr += "\"";
					
					jsonStr += ",\"information\":\"";
					String des = event.description.replace("\"", "");
					jsonStr += des;
					jsonStr +="\"";
					
					jsonStr += ",\"comment\":[";
					DataCommonHandle handler = new DataCommonHandle();
					LinkedList<MessageData> messageList = handler.getEventIDMessage(event.id);
					
					for(int j = 0; j < messageList.size(); j++) {
						MessageData message = messageList.get(j);
						
						jsonStr += "{";
						jsonStr += "\"username\":\"";
						jsonStr += message.getFrom();
						jsonStr += "\"";
						
						jsonStr += ",\"commenttext\":\"";
						jsonStr += message.getMessage();
						jsonStr += "\"";
						
						jsonStr += ",\"date\":\"";
						jsonStr += message.getDate();
						jsonStr += "\"";
						
						jsonStr += "}";
						
						if(j != messageList.size() - 1) {
							jsonStr += ",";
						}
					}
					
					jsonStr += "]}";
				}
				
				jsonStr += "]}";
				
				//System.out.println(jsonStr);
				Gson g = new Gson();
				JsonObject obj = g.fromJson(jsonStr, JsonObject.class);
				System.out.println(obj.toString());
				
				sendRespond(response, jsonStr);
				
			}else if(request.getHeader("MessageType").equals("PersonalUpdate")) {
				System.out.println("PersonalUpdate");
				DataCommonHandle handler = new DataCommonHandle();
				String userName = request.getHeader("UserName");
				User user= DataCommonManager.CM.createCommon(userName, "");
				LinkedList<Event> eventList = handler.SelectMyEvents(user);
			    System.out.println(eventList.size());
				String jsonStr = "{\"username\":\"";
				jsonStr += userName;
				jsonStr += "\"";
				jsonStr += ",\"events\":[";
				
				for(int i = 0; i < eventList.size(); i++){
					
					if(i != 0) {
						jsonStr += ",";
					}
					
					jsonStr += "{";
					
					jsonStr += "\"name\":\"";
					Event event = eventList.get(i);
					String str = event.name.replace("\"", "");
					jsonStr += str;
					jsonStr += "\"";
					
					jsonStr += ",\"id\":\"";
					jsonStr += event.id;
					jsonStr += "\"";
					
					jsonStr += ",\"destination\":\"";
					if(!(this.locationMap.containsKey(event.id)) || counter >= 10) {
						EventbriteAPI contributer = new EventbriteAPI();
						JSONObject locations = contributer.getEventLocation(event.id);
						String eventLocation = locations.getString("localized_address_display").toString();
						jsonStr += eventLocation;
						this.locationMap.put(event.id, eventLocation);
						counter = 0;
						
					}else {
						jsonStr += this.locationMap.get(event.id);
					}
					jsonStr += "\"";
					
					jsonStr += ",\"information\":\"";
					String des = event.description.replace("\"", "");
					jsonStr += des;
					jsonStr +="\"";
					
					jsonStr += ",\"comment\":[";
					LinkedList<MessageData> messageList = handler.getEventIDMessage(event.id);
					
					for(int j = 0; j < messageList.size(); j++) {
						MessageData message = messageList.get(j);
						
						jsonStr += "{";
						jsonStr += "\"username\":\"";
						jsonStr += message.getFrom();
						jsonStr += "\"";
						
						jsonStr += ",\"commenttext\":\"";
						jsonStr += message.getMessage();
						jsonStr += "\"";
						
						jsonStr += ",\"date\":\"";
						jsonStr += message.getDate();
						jsonStr += "\"";
						
						jsonStr += "}";
						
						if(j != messageList.size() - 1) {
							jsonStr += ",";
						}
					}
					
					jsonStr += "]}";
				}
				
				jsonStr += "]}";
				
				Gson g = new Gson();
				JsonObject obj = g.fromJson(jsonStr, JsonObject.class);
				System.out.println(obj.toString());
				
				sendRespond(response, jsonStr);
			}

		}else {
			sendRespond(response, "Wrong token");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Receice Post");
		System.out.println(request.getParameter("MessageType"));
		
		if(request.getParameter("MessageType").equals("Chat")) {
			if(checkToken(request)) {
				String fromId = request.getParameter("UserName");
				String eventId = request.getParameter("EventId");
				String message = request.getParameter("Message");
				
				Date currentDate = new Date();
				SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd.hh.mm");
				String formateDate = ft.format(currentDate);
				
				System.out.println("POST£º " + request.getParameter("Message") + " from:" + fromId + " id:" + eventId);
				MessageData newMessage = new MessageData(fromId,eventId,message,formateDate);
				DataCommonHandle handler = new DataCommonHandle();
			    handler.InsertMessage(newMessage);
			}else {
				sendRespond(response, "Wrong token");
			}

		}else if(request.getParameter("MessageType").equals("Login")) {
			String userName = request.getParameter("UserName");
			String password = request.getParameter("Password");
			System.out.println(userName);
			System.out.println(password);
			
			User user = DataCommonManager.CM.createCommon(userName, "");
			if(user!= null && user.name.equals(userName) && user.password.equals(password)) {
				Date currentDate = new Date();
				SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd.hh.mm");
				String formateDate = ft.format(currentDate);

				String token = userName + formateDate;
				this.tokenMap.put(userName, token);
				System.out.println("???" + this.tokenMap.get(userName));
				sendRespond(response, token);
			}else {
				sendRespond(response, "False");
			}

		}else if(request.getParameter("MessageType").equals("Signup")) {
			String userName = request.getParameter("UserName");
			String password = request.getParameter("Password");
			User newUser = new User(userName,"","",password);
			User existUser = DataCommonManager.CM.createCommon(userName, "");
			
			if(existUser != null) {
				sendRespond(response, "Exist");
			}else {
				DataCommonHandle handler = new DataCommonHandle();
				handler.insertUser(newUser);
				sendRespond(response, "Success");
			}
			
		}else if(request.getParameter("MessageType").equals("Logout")) {
			String userName = request.getParameter("UserName");
			this.tokenMap.remove(userName);
			
		}else if(request.getParameter("MessageType").equals("Join")) {
			System.out.println("Join");
			if(checkToken(request)) {
				String eventIdStr = request.getParameter("EventId");
				System.out.println(eventIdStr);
				String username = request.getParameter("UserName");
				
				DataCommonHandle handler = new DataCommonHandle();
				User user = handler.getUser(username, "");
				LinkedList<Event> eventList = handler.SelectMyEvents(user);
				
				boolean alreadyIn = false;
				for(int i = 0; i < eventList.size(); i++) {
					Event event = eventList.get(i);
					
					if(event.id.equals(eventIdStr)) {
						alreadyIn = true;
						System.out.println("Already in");
						break;
					}
				}
				
				if(!alreadyIn) {
					Event event = handler.getEventInfoFromAPI(eventIdStr);
					System.out.println(user);
					handler.InsertMyEvent(user, event);
				}
			}else {
				sendRespond(response, "Wrong token");
			}
			
		}else if(request.getParameter("MessageType").equals("UpdatePassword")) {
			String userName = request.getParameter("UserName");
			String password = request.getParameter("Password");
			User newUser = new User(userName,"","",password);
			DataCommonHandle handler = new DataCommonHandle();
			handler.insertUser(newUser);
			sendRespond(response, "Success");
		}
	}
}