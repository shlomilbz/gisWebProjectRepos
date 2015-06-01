package emergencyProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import com.sun.org.apache.xml.internal.resolver.readers.XCatalogReader;


import SQL_DataBase.SQL_db;

/**
 * Servlet implementation class RequsteGoogle
 */
@WebServlet("/RequsteGoogle")
public class RequestGoogle extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RequestGoogle() {
		super();
		// TODO Auto-generated constructor stub
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
			//init sql data base 
			SQL_db sqlDataBase = new SQL_db();
			//all work with requste from http
			StringBuffer jb = new StringBuffer();
			String stringToParse = null;
			try {
				BufferedReader reader = request.getReader();
				while ((stringToParse = reader.readLine()) != null){
					jb.append(stringToParse);
				}
			} catch (Exception e) { /*report an error*/ }
			//start work with json obj
			//create parser to saperte the data from json array
			//	JSONParser parser = new JSONParser();
			//	JSONObject jsonObject = (JSONObject) parser.parse(stringToParse);

			//	JSONArray jsonArrayOb=(JSONArray) jsonObject.get("JSONFile");
			// take each value from the json array separately
			//	Iterator i = jsonArrayOb.iterator();
			//			JSONObject innerObj = (JSONObject) i.next();
			//{"RequestID":"routineLocation"}
			//if (innerObj.get("RequestID").equals("routineLocation")){
			//{"comunity_member_id":123456, "x":7.777, "y":8.999}
			/**
			 * need to check if we need this
			 * while (i.hasNext()) {
				// create inner obj to get the data from all json obd in json array
				JSONObject innerObj = (JSONObject) i.next();
				if (innerObj.get("RequestID").equals("UsersArrivalTimes")){
					//int cmid = Integer.parseInt(innerObj.get("comunity_member_id").toString());
					String cmid  = innerObj.get("comunity_member_id").toString();
					int eventid = Integer.parseInt(innerObj.get("event_id").toString());
					//         	double radius=Double.parseDouble((innerObj.get("radius").toString()))
					System.out.println("comunity_member_id "+ innerObj.get("comunity_member_id") +
							" with eventid cor. " + innerObj.get("eventid"));
					//		"radius "+innerObj.get(radius));**/
			//create new sql table by michal with eventid cmid and attribute helper or sick person (String)
			//get the location by cmid michal

			try {
				JSONArray jsonToSent=new JSONArray();
				JSONObject obj=new JSONObject();
				String cmid="";
				obj.put("comunity_member_id", cmid);
				String driving=sendGet("driving",34.731161,31.880611,34.663870,31.812951);
				obj.put("eta_by_car",driving);
				String walking=sendGet("walking",34.731161,31.880611,34.663870,31.812951);
				obj.put("eta_by_foot", walking);
				String address=getAddresss(34.731161,31.880611);
				obj.put("address", address);
				jsonToSent.add(obj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}





		} catch (NullPointerException ex) {

			ex.printStackTrace();

		}

	}
	// requte for get time by mod and source and target
	private static String sendGet(String mod,double yCurrent,double xCurrent,double needToY,double needToX) throws Exception {
		//the user browser
		String USER_AGENT="Chrome";
		//key to use google maps api
		String API_KEY="AIzaSyD_RZV_mPtJda32KgGgMGcJxPPA83KyEI0";
		// ask google time by mod 
		String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+
				xCurrent+","+yCurrent+"&destination="+needToX+","+needToY+"&departure_time=now&mode="+mod+"&key="+API_KEY;
		
		URL obj = new URL(url);
		//open connection
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

	//	int responseCode = con.getResponseCode();


		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();
		//get the answer from google maps and convert him to string 
		//for parse
		String output=response.toString();
		//send to parse func
		String time=parse(output);
		//disconnect
		con.disconnect();
		return time;

	}
	private static String parse(String output)
	{
		//now parse
		JSONParser parser = new JSONParser();
		Object object=null;
		try {
			object = parser.parse(output);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jb = (JSONObject) object;



		// routesArray contains ALL routes
		JSONArray routesArray = (JSONArray)jb.get("routes");
		// Grab the first route
		JSONObject route = (JSONObject)routesArray.get(0);
		// Take all legs from the route
		JSONArray legs = (JSONArray)route.get("legs");
		// Grab first leg
		JSONObject leg = (JSONObject)legs.get(0);
		//duration time
		JSONObject durationObject = (JSONObject)leg.get("duration");
		//convert to streng and send beck
		return (durationObject.get("text").toString());

	}

	/**
	 *
	 * @param lng
	 * @param lat
	 * @return
	 */
	private static String getAddresss(double x, double y)
			throws MalformedURLException, IOException, org.json.simple.parser.ParseException {
		//get double and convert them to string for url format
		String lng=Double.toString(x);
		String lat=Double.toString(y);
		//send url and get geo json
		URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng="
				+ lat + "," + lng + "&sensor=true");
		//open connection
		HttpURLConnection con= (HttpURLConnection) url.openConnection();
		String formattedAddress = "";



		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		JSONParser parser = new JSONParser();
		JSONObject rsp = (JSONObject) parser.parse(response.toString());
		//parse json obj we get
		if (rsp.containsKey("results")) {
			JSONArray matches = (JSONArray) rsp.get("results");
			JSONObject data = (JSONObject) matches.get(0); 
			formattedAddress = (String) data.get("formatted_address");
		}

		//disconnect
		con.disconnect();
		return formattedAddress;

	}
}




