package emergencyProcess;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import SQL_DataBase.SQL_db;

public class Emergency extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public Emergency() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			SQL_db sqlDataBase = new SQL_db();
			StringBuffer jb = new StringBuffer();
			String stringToParse = null;
			try {
				BufferedReader reader = request.getReader();
			    while ((stringToParse = reader.readLine()) != null){
			    	jb.append(stringToParse);
			    }
			  } catch (Exception e) { /*report an error*/ }
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(stringToParse);
			
			JSONArray jsonArrayOb=(JSONArray) jsonObject.get("JSONFile");
			// take each value from the json array separately
			Iterator i = jsonArrayOb.iterator();
			List<String> cmidAtRadius = new ArrayList<String>();
			double x = 0,y = 0;
			int radius;
	        while (i.hasNext()) {
	             	JSONObject innerObj = (JSONObject) i.next();
	                if (innerObj.get("RequestID").equals("AroundLocation")){
	                	String region_type;
	                	//get from Json the data
	                	String eventID = innerObj.get("eventID").toString();
	                	String cmid  = innerObj.get("comunity_member_id").toString();
	                	x = Double.parseDouble(innerObj.get("x").toString());
	                    y = Double.parseDouble(innerObj.get("y").toString());
	                	String state = innerObj.get("region_type").toString();	                	
	                	String medical_condition_description  = innerObj.get("medical_condition_description").toString();
	                	float age = Float.parseFloat(innerObj.get("age").toString());
	                	radius = Integer.parseInt(innerObj.get("radius").toString());
	                	
	                	//need to implement the function
	                	region_type = sqlDataBase.getregion_type();
	                	
	                	/**/radius=3;
	                	//if we haven't a radius
	                	//TODO
	                	if(radius == 0 /*|| radius == null*/) {
	                		//need to implement the function
	                		radius = sqlDataBase.getRadiusFromDesicionTable(eventID, cmid, x, y, state, region_type, medical_condition_description, age);
	                	}
                		sqlDataBase.updateDecisionTable(eventID, cmid, x, y, state, region_type, medical_condition_description, age, radius);
            			cmidAtRadius = sqlDataBase.getCMIDByRadius(radius, x, y);
	               	}
            }
	        JSONArray jsonToSend=new JSONArray();
	        JSONObject obj=new JSONObject();
	        RequestGoogle req=new RequestGoogle();
	        String address=req.getAddresss(x, y);
	        String[] split=address.split(",");
	        obj.put("state", split[2]);
	        obj.put("location_remark",address);
	        obj.put("region_type", sqlDataBase.getregion_type());
	        for (int j=0; j<cmidAtRadius.size();j++) {
	        	obj.put(cmidAtRadius.get(j), "NULL");
	        }
	        jsonToSend.add(obj);
	        //obj.sendResponse();
	        //send with sendResponse
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}
}



