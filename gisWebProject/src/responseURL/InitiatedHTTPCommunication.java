package responseURL;

import org.json.JSONArray;
import org.jsoup.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class InitiatedHTTPCommunication {
	//get data and target- this is the URL of the server
    public InitiatedHTTPCommunication_V1(HashMap<Integer,HashMap<String,String>> data, ArrayList<String> target) {
        super(data);
        targets = target;
    }
    public void sendResponse () {
    	//TODO 1 iteration
        //communicate the JSON file to each target URL provided
    	try {
    		Jsoup.connect(url server)
            .data("username", "GIS_TEAM")
            .data("password", "9999999")
            .data("JSONFile", objToSend.toString())
            .method(Connection.Method.POST).execute();
    	} catch (IOException e) {
            e.printStackTrace();
        }
    }
}
