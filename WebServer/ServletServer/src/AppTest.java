import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import pigeongroup.APIWorker.EventbriteAPI;
import pigeongroup.Data.Event;
import pigeongroup.Data.MessageData;
import pigeongroup.Data.User;
import pigeongroup.DataBase.DataBaseManager;
import pigeongroup.DataCommon.DataCommonHandle;
import pigeongroup.DataCommon.DataCommonManager;

public class AppTest {
    public static void main( String[] args )
    {
//    	String str = "LUIS CHATAING \"NuevaMente\" SYDNEY";
//    	System.out.println(str);
//    	
//    	String str1 = str.replace("\"", "");
//    	System.out.println(str1);
//    	Gson g = new Gson();
//    	String[] numbers = {"asd", "sfd", "I \"An\" asd"};
//    	String numbersJson = g.toJson(numbers);
//    	String jsonStr = "{\"username\":\"Andrew\", \"event\":";
//    	String finalStr = jsonStr + numbersJson;
//    	finalStr += "}";
//    	JsonObject obj = g.fromJson(finalStr, JsonObject.class);
//    	System.out.println(finalStr);
//		System.out.println(obj.toString());
    	
    	EventbriteAPI contributer = new EventbriteAPI();
		LinkedList<Event> eventList = contributer.getEventInfoFromApi("UNSW", "6");
		String jsonStr = "{event:[";
		for(int i = 0; i < eventList.size(); i++){
			if(i != 0) {
				jsonStr += ",";
			}
			
			jsonStr += "{";
			
			jsonStr += "\"index\":\"";
			String index = String.valueOf(i);
			jsonStr += index;
			jsonStr += "\"";
			
			jsonStr += ",\"name\":\"";
			Event event = eventList.get(i);
			String str = event.name.replace("\"", "");
			jsonStr += str;
			jsonStr += "\"";
			
			jsonStr += ",\"id\":\"";
			jsonStr += event.id;
			jsonStr += "\"";
			
			jsonStr += ",\"information\":\"";
			String des = event.description.replace("\"", "");
			jsonStr += des;
			jsonStr +="\"";
			
			jsonStr += "}";
		}
		
		jsonStr += "]}";
		
		Gson g = new Gson();
		JsonObject obj = g.fromJson(jsonStr, JsonObject.class);
		System.out.println(obj.toString());
    }
}