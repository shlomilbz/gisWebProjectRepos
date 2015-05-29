package SQL_DataBase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Time;





import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import emergencyProcess.Cmid;

public class SQL_db {
	private Connection connection;
	private Statement statement;
	private static final int maxCMID = 20;
	
	// ctor
	public SQL_db(){
		try {
			connect();
			statement.execute("USE GIS_DB;");
			statement.execute("CREATE TABLE IF NOT EXISTS updatedLocation (cmid VARCHAR(20), x DOUBLE(9,6), y DOUBLE(9,6), createdDate DATE, createdTime TIME, lastUpdatedDate DATE, lastUpdatedTime TIME);");/*  */
			statement.execute("CREATE TABLE IF NOT EXISTS locationHistory (cmid VARCHAR(20), x DOUBLE(9,6), y DOUBLE(9,6), createdDate DATE, createdTime TIME, lastUpdatedDate DATE, lastUpdatedTime TIME);");
			statement.execute("CREATE TABLE IF NOT EXISTS decisionTable (eventID VARCHAR(20), cmid VARCHAR(20), x DOUBLE(9,6), y DOUBLE(9,6), state VARCHAR(20), area VARCHAR(15), disease VARCHAR(25), age FLOAT(5,2), radius INT);");
		}
		catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		 }
		 catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		 }
		// disconnect
		finally
		{
			disconnect();
		}
	}
	//if we dont have a radius we take radius from decision table
	public int getRadiusFromDesicionTable(String cmid, double x, double y, double age, String disease) {
		int radius=0;
		
		return radius;
	}
	
	public List<String> getCMIDByRadius(double radius, double x, double y) {
		List<String> cmidAtRadius = new ArrayList<String>();
		int countCMIDAtRadius=0;
    	double secondX, secondY;
    	String secondCmid;
    	double distance;
		try {
			connect();
			statement.execute("USE GIS_DB;");
			ResultSet rs=statement.executeQuery("SELECT * FROM updatedLocation;");
	    	//List<Cmid> listOfObjects = new ArrayList<Cmid>();
			while(rs.next() && countCMIDAtRadius < maxCMID){
	    		secondCmid = rs.getString(1);
	    		secondX = rs.getDouble(2);
	    		secondY= rs.getDouble(3);
	    	    //Cmid newCmid = new Cmid (secondCmid, secondX, secondY);
	    	    distance = Math.sqrt((x-secondX)*(x-secondX) + (y-secondY)*(y-secondY));
	    	    if(distance <= radius) {
	    	    	//listOfObjects.add(newCmid);
	    	    	cmidAtRadius.add(secondCmid);
	    	    	countCMIDAtRadius++;
	    	    }
			}
		}
		catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		 }
		 catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		 }
		// disconnect
		finally
		{
			disconnect();
		}
		return cmidAtRadius;
	}
	
	
	public void updateLocation(String cmid, double x, double y){
		try {
			connect();
			statement.execute("USE GIS_DB;");
			ResultSet rs=statement.executeQuery("SELECT * FROM updatedLocation WHERE cmid='"+cmid+"';");
			if(!rs.next()){
				statement.executeUpdate("INSERT INTO updatedLocation VALUES ('"+cmid+"',"+x+","+y+",CURDATE(),CURTIME(),CURDATE(),CURTIME());");
			}
			else{
				//rs.previous();
				double x_val = rs.getDouble("x");
				double y_val = rs.getDouble("y");
				String cmid_val = rs.getString("cmid");
				Date date_val = rs.getDate("createdDate");
				Time time_val = rs.getTime("createdTime");
				Date lastUpdatedDate_val = rs.getDate("lastUpdatedDate");
				Time lastUpdatedTime_val = rs.getTime("lastUpdatedTime");
				// if the location changed
				if((x!=x_val)||(y!=y_val)){
					statement.executeUpdate("INSERT INTO locationHistory VALUES ('"+cmid_val+"',"+x_val+","+y_val+",'"+date_val+"','"+time_val+"','"+lastUpdatedDate_val+"','"+lastUpdatedTime_val+"');");
					statement.executeUpdate("UPDATE updatedLocation SET x="+x+", y="+y+", createdDate = CURDATE(), createdTime = CURTIME(), lastUpdatedDate = CURDATE(), lastUpdatedTime = CURTIME() WHERE cmid='"+cmid+"';");
				}
				else{// the location didn't changed
					statement.executeUpdate("UPDATE updatedLocation SET lastUpdatedDate = CURDATE(), lastUpdatedTime = CURTIME() WHERE cmid='"+cmid+"';");
				}
			}
		}
		
		catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		 }
		 catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		 }
		// disconnect
		finally
		{
			disconnect();
		}
	}
	
	//eventID, cmid, x, y, disease, age
	/*public void updateDesicionTable(String cmid, double x, double y){
		try {
			connect();
			statement.execute("USE GIS_DB;");
			ResultSet rs=statement.executeQuery("SELECT * FROM updatedLocation WHERE cmid='"+cmid+"';");
			if(!rs.next()){
				statement.executeUpdate("INSERT INTO updatedLocation VALUES ('"+cmid+"',"+x+","+y+",CURDATE(),CURTIME(),CURDATE(),CURTIME());");
			}
			else{
				//rs.previous();
				double x_val = rs.getDouble("x");
				double y_val = rs.getDouble("y");
				String cmid_val = rs.getString("cmid");
				Date date_val = rs.getDate("createdDate");
				Time time_val = rs.getTime("createdTime");
				Date lastUpdatedDate_val = rs.getDate("lastUpdatedDate");
				Time lastUpdatedTime_val = rs.getTime("lastUpdatedTime");
				// if the location changed
				if((x!=x_val)||(y!=y_val)){
					statement.executeUpdate("INSERT INTO locationHistory VALUES ('"+cmid_val+"',"+x_val+","+y_val+",'"+date_val+"','"+time_val+"','"+lastUpdatedDate_val+"','"+lastUpdatedTime_val+"');");
					statement.executeUpdate("UPDATE updatedLocation SET x="+x+", y="+y+", createdDate = CURDATE(), createdTime = CURTIME(), lastUpdatedDate = CURDATE(), lastUpdatedTime = CURTIME() WHERE cmid='"+cmid+"';");
				}
				else{// the location didn't changed
					statement.executeUpdate("UPDATE updatedLocation SET lastUpdatedDate = CURDATE(), lastUpdatedTime = CURTIME() WHERE cmid='"+cmid+"';");
				}
			}
		}
		
		catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		 }
		 catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		 }
		// disconnect
		finally
		{
			disconnect();
		}
	}*/
	
	private void connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String dbUrl = "jdbc:mysql://localhost";
			connection = DriverManager.getConnection(dbUrl,"root", "");
			MysqlDataSource ds = new MysqlConnectionPoolDataSource();
			ds.setServerName("localhost");
			ds.setDatabaseName("GIS_DB");
			statement=connection.createStatement();
			String dbName = new String("GIS_DB");
			statement.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
		}
		catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		 }
		 catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		 }
	}
	private void disconnect(){
	      //finally block used to close resources
	      try{
	         if(statement!=null)
	        	 statement.close();
	      }
	      catch(SQLException se2){
	      }// nothing we can do
	      try{
	         if(connection!=null)
	        	 connection.close();
	      }
	      catch(SQLException se){
	         se.printStackTrace();
	      }//end finally try
	}
}
