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
//			JSONObject innerObj = (JSONObject) i.next();
			List<String> cmidAtRadius = new ArrayList<String>();
	        while (i.hasNext()) {
	             	JSONObject innerObj = (JSONObject) i.next();
	                if (innerObj.get("RequestID").equals("AroundLocation")){
	                	//get from Json the data
	                	String eventID = innerObj.get("eventID").toString();
	                	String cmid  = innerObj.get("comunity_member_id").toString();
	                	double x = Double.parseDouble(innerObj.get("x").toString());
	                	double y = Double.parseDouble(innerObj.get("y").toString());
	                	String area = innerObj.get("area").toString();
	                	String state = innerObj.get("state").toString();	                	
	                	String disease  = innerObj.get("disease").toString();
	                	float age = Float.parseFloat(innerObj.get("age").toString());
	                	int radius = Integer.parseInt(innerObj.get("radius").toString());
	                	/**/radius=3;
	                	//if we haven't a radius
	                	if(radius == 0) {
	                		radius = sqlDataBase.getRadiusFromDesicionTable(eventID, cmid, x, y, state, area, disease, age);
	                	}
            			cmidAtRadius = sqlDataBase.getCMIDByRadius(radius, x, y);
            			sqlDataBase.updateDecisionTable(eventID, cmid, x, y, state, area, disease, age, radius);
	               	}
            }
	        //Elior need add Json Array
	        //Thank you!!
	       //get the parameter from arcgis to json object	        
		} catch (ParseException ex) {
			ex.printStackTrace();

		} catch (NullPointerException ex) {
			ex.printStackTrace();

		}
		//return cmidAtRadius;

	}
}



