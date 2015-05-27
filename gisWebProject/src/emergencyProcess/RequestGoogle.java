package emergencyProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
			//all work with requst from http
			StringBuffer jb = new StringBuffer();
			String stringToParse = null;
			try {
				BufferedReader reader = request.getReader();
				while ((stringToParse = reader.readLine()) != null){
					jb.append(stringToParse);
				}
			} catch (Exception e) { /*report an error*/ }
			//start work with jsson obj
			//create parser to saperte the data from json array
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(stringToParse);

			JSONArray jsonArrayOb=(JSONArray) jsonObject.get("JSONFile");
			// take each value from the json array separately
			Iterator i = jsonArrayOb.iterator();
			//			JSONObject innerObj = (JSONObject) i.next();
			//{"RequestID":"routineLocation"}
			//if (innerObj.get("RequestID").equals("routineLocation")){
			//{"comunity_member_id":123456, "x":7.777, "y":8.999}
			while (i.hasNext()) {
				// create inner obj to get the data from all json obd in json array
				JSONObject innerObj = (JSONObject) i.next();
				if (innerObj.get("RequestID").equals("UsersArrivalTimes")){
					//int cmid = Integer.parseInt(innerObj.get("comunity_member_id").toString());
					String cmid  = innerObj.get("comunity_member_id").toString();
					int eventid = Integer.parseInt(innerObj.get("event_id").toString());
					//         	double radius=Double.parseDouble((innerObj.get("radius").toString()))
					System.out.println("comunity_member_id "+ innerObj.get("comunity_member_id") +
							" with eventid cor. " + innerObj.get("eventid"));
					//		"radius "+innerObj.get(radius));
					//update by michal sql table
					//get the location from table michal
					//    	sqlDataBase.updateLocation(cmid, x, y);
					try {
						JSONArray jsonToSent=new JSONArray();
						JSONObject obj=new JSONObject();
						obj.put("comunity_member_id", cmid);

						String driving=sendGet("driving",34.731161,31.880611,34.663870,31.812951);
						obj.put("eta_by_car",driving);
						String walking=sendGet("walking",34.731161,31.880611,34.663870,31.812951);
						obj.put("eta_by_foot", walking);
						jsonToSent.add(obj);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		} catch (ParseException ex) {

			ex.printStackTrace();

		} catch (NullPointerException ex) {

			ex.printStackTrace();

		}

	}
	private static String sendGet(String mod,double yCurrent,double xCurrent,double needToY,double needToX) throws Exception {
		String USER_AGENT="Chrome";
		String API_KEY="AIzaSyD_RZV_mPtJda32KgGgMGcJxPPA83KyEI0";
		String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+
				xCurrent+","+yCurrent+"&destination="+needToX+","+needToY+"&departure_time=now&mode="+mod+"&key="+API_KEY;
		// ask google time by driving 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		//				System.out.println("\nSending 'GET' request to URL : " + url);
		//				System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();
		String output=response.toString();
		String time=prase(output);
		System.out.println(time);
		//print result
		System.out.println(response.toString());
		return time;

	}
	private static String prase(String output)
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

		JSONObject durationObject = (JSONObject)leg.get("duration");
		return (durationObject.get("text").toString());

	}


}


