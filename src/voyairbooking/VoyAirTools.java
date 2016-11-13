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

import org.joda.time.LocalDate;

import sql_driver.SQL_Driver;
import voyairbooking.Graph.Edge;

public class VoyAirTools {
	private String user_id;
	protected SQL_Driver sqld;
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
			ArrayList<HashMap<String, String>> res = this.sqld.select("airport", "city", "", true);

			ArrayList<String> toReturn = get_field(res, "city");
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
	private String surround_parens(String str){
		return "(" + str + ")";
	}

	public void get_routes(String start_city, String end_city, LocalDate arrivalDate, LocalDate takeoffDate, String arrivalTime, String takeoffTime){
		//Dietakeoff_airport_id
		try {
			ArrayList<String> end_id  = get_field(this.sqld.select("airport", "*", "city="+end_city), "airport_id");

			ArrayList<String> start_id = get_field(this.sqld.select("airport", "*", "city="+start_city), "airport_id");
			String end_ids = surround_parens(String.join(", ",end_id));
			String start_ids = surround_parens(String.join(", ", start_id));

			// #ThingsThatAreInsane
			ArrayList<HashMap<String, String>> all_routes = this.sqld.select_all("route");
			List<Graph.Edge> graph_list = new ArrayList<Graph.Edge>();
			for(HashMap<String, String> row : all_routes){
				graph_list.add(new Edge(row.get("takeoff_airport_id"), row.get("destination_airport_id"), Float.valueOf(row.get("price"))));
			}
			ArrayList<String[]> Possibilities = new ArrayList<String[]>();
			Graph.Edge[] route_graph = new Graph.Edge[graph_list.size()];
			Graph g = new Graph(graph_list.toArray(route_graph));
			for(String start: start_id){
				for(String end: end_id){
					g.dijkstra(start);
					String path = g.getThePath(end);
					Possibilities.add(path.split("->"));
					System.out.println(path);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
