/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voyairbooking;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sql_driver.SQL_Driver;
import voyairbooking.Graph.Edge;

public class VoyAirTools {
	private String user_id;
	protected SQL_Driver sqld;
	protected DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
	protected DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
	public VoyAirTools(boolean debugMode){
		try {
			this.sqld = new SQL_Driver(debugMode);
			this.setUser_id("-1");
		} catch (SQLException ex) {
			System.err.println("Failed to initate VoyAirBooking. Please try again later.");
		}

	}
	public ArrayList<String> get_cities(){
		try {
			ArrayList<HashMap<String, String>> res = this.sqld.select("airport", "airport_city", "", true);
			ArrayList<String> toReturn = get_field(res, "airport_city");
			Collections.sort(toReturn, String.CASE_INSENSITIVE_ORDER);
			return toReturn;
		} catch (SQLException e) {
			return null;
		}
	}
	private ArrayList<String> get_field(ArrayList<HashMap<String, String>> result, String field){
		HashSet<String> toReturn = new HashSet<String>();
		for(HashMap<String, String> row : result){
			toReturn.add(row.get(field));
		}
		return new ArrayList<String>(toReturn);
	}

	public ArrayList<ArrayList<ArrayList<String>>> get_routes(String start_city, String end_city){
		try {
			ArrayList<String> end_id  = get_field(this.sqld.select("airport", "*", "airport_city="+end_city), "airport_id");
			ArrayList<String> start_id = get_field(this.sqld.select("airport", "*", "airport_city="+start_city), "airport_id");
			ArrayList<HashMap<String, String>> result = this.sqld.parseResultSet(this.sqld.executeQuery("select price, destination_airport_id, departure_airport_id from route"), "price, destination_airport_id, departure_airport_id");
			List<Graph.Edge> graph_list = new ArrayList<Graph.Edge>();
			for(HashMap<String, String> row: result){
				graph_list.add(new Edge(row.get("departure_airport_id"), row.get("destination_airport_id"), Double.valueOf(row.get("price"))));
			}
			ArrayList<String[]> Possibilities = new ArrayList<String[]>();
			Graph.Edge[] route_graph = new Graph.Edge[graph_list.size()];
			Graph g = new Graph(graph_list.toArray(route_graph));

			for(String start: start_id){
				for(String end: end_id){
					g.dijkstra(start);
					Possibilities.add(g.getThePath(end).split("\\s*->\\s*"));
					System.out.println(g.getThePath(end));
				}
			}
			ArrayList<ArrayList<ArrayList<String>>> route_ids = new ArrayList<ArrayList<ArrayList<String>>>();
			for(String[] p : Possibilities){
				ArrayList<ArrayList<String>> aRoute = new ArrayList<ArrayList<String>>();
				for(int i = 0; i < p.length-1; i++){
					ArrayList<String> flight = new ArrayList<String>();
					ArrayList<HashMap<String, String>> execRes = this.sqld.parseResultSet(this.sqld.executeQuery("SELECT * FROM route WHERE departure_airport_id="+p[i].trim() +" AND destination_airport_id="+p[i+1].trim()), "*");
					for(HashMap<String,String> row: execRes){
						flight.add(row.get("route_id"));
					}
					aRoute.add(flight);
				}
				route_ids.add(aRoute);
			}
			return route_ids;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> trim_routes(ArrayList<ArrayList<ArrayList<String>>> routes, LocalDate arrivalDate, LocalDate takeoffDate, LocalTime arrivalTime, LocalTime takeoffTime){
		try {
			ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> flight_options = new ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> ();
			for(ArrayList<ArrayList<String>> aRoute : routes){
				ArrayList<ArrayList<HashMap<String, String>>> route_leg = new ArrayList<ArrayList<HashMap<String, String>>>();
				for(ArrayList<String> flight: aRoute){
					ArrayList<HashMap<String, String>> route_details = this.sqld.select("route", "*", "route_id IN " + flight.toString().replaceAll("\\[", "(").replaceAll("\\]",")")) ;
					ArrayList<HashMap<String, String>> time_working_flights = new ArrayList<HashMap<String, String>>();
					for(HashMap<String, String> row: route_details){
						LocalDate date = this.dtf.parseLocalDate(row.get("date"));
						LocalTime time = this.fmt.parseLocalTime(row.get("time"));
						if(!takeoffDate.isBefore(date) || (takeoffDate.isEqual(date) && !takeoffTime.isBefore(time))){
							time_working_flights.add(row);
						}
					}
					route_leg.add(time_working_flights);
				}
				flight_options.add(route_leg);
			}
			return flight_options;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	public boolean save_route(String route_id, String user_id){
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("route_id",  route_id);
		fields.put("account_id", user_id);
		return this.sqld.insert("transaction", fields);
	}

	public boolean register(String username, String password){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			Map<String, String> toInsert = new HashMap<String, String>();
			toInsert.put("username", username);
			toInsert.put("password", md.digest(password.getBytes()).toString());
			if(this.sqld.insert("account", toInsert)){
				System.out.println("Failed to register account.");
				return false;
			}
			else{
				System.out.println("Account registration was successful!");
				try {
					this.setUser_id(this.sqld.count_rows("account"));
					return true;

				} catch (SQLException e) {
					System.err.println("How the hell did you get here?");
				}
			}
		} catch (NoSuchAlgorithmException e) {
			System.err.println("No Such Algorithm");
		}
		return false;

	}
	private void setUser_id(Integer aNum) {
		this.setUser_id(aNum.toString());

	}
	public boolean tryLoggingIn(String username, String password){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			HashMap<String, String> res = this.sqld.select_first("accounts", "*", "username=" + username);
			if(res.isEmpty()){
				System.out.println("No user by the username " + username + ".");
				System.out.println("Registering them now.");
				return register(username, password);
			}
			if(res.get("password").equals(md.digest(password.getBytes()))){
				System.out.println("Welcome back, " + username + "!");
				this.setUser_id(res.get("account_id"));
				return true;
			}
			else{
				System.out.println("Inccorrect password");
				return false;
			}
		} catch (NoSuchAlgorithmException e) {
			System.err.println("No Such Algorithm");
			return false;
		} catch (SQLException e) {
			System.err.println("SQL Error");
			return false;
		}
	}
	public boolean is_logged_in() {
		return user_id.equalsIgnoreCase("-1");
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
