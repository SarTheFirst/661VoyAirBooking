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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sql_driver.SQL_Driver;
import voyairbooking.Graph.Edge;

public class VoyAirTools {
	private String user_id;
	protected SQL_Driver sqld;
	protected Utils util;
	protected DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
	protected DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
	protected DateTimeFormatter sql_formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
	protected DateTimeFormatter print_datetime = DateTimeFormat.forPattern("MMM dd, y");

	public VoyAirTools(boolean debugMode){
		try {
			this.sqld = new SQL_Driver(debugMode);
			this.util = new Utils();
			this.setUser_id("-1");
		} catch (SQLException ex) {
			System.err.println("Failed to initate VoyAirBooking. Please try again later.");
		}

	}
	public ArrayList<String> get_cities(){
		try {
			ArrayList<HashMap<String, String>> res = this.sqld.select("airport", "airport_city", "", "ORDER BY airport_city", true);
			ArrayList<String> toReturn = get_field(res, "airport_city");
			return toReturn;
		} catch (SQLException e) {
			return null;
		}
	}
	private ArrayList<String> get_field(ArrayList<HashMap<String, String>> result, String field){
		ArrayList<String> toReturn = new ArrayList<String>();
		for(HashMap<String, String> row : result){
			toReturn.add(row.get(field));
		}
		return toReturn;
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
					List<String> allRes = this.util.splitOnChar(g.getThePath(end), "->");
					Possibilities.add(allRes.toArray(new String[allRes.size()]));
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

	public ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> trim_routes(ArrayList<ArrayList<ArrayList<String>>> routes, LocalDate arrivalDate, LocalDate takeoffDate, LocalTime arrivalTime, LocalTime takeoffTime, int numTickets){
		try {
			ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> flight_options = new ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> ();
			for(ArrayList<ArrayList<String>> aRoute : routes){
				ArrayList<ArrayList<HashMap<String, String>>> route_leg = new ArrayList<ArrayList<HashMap<String, String>>>();
				for(ArrayList<String> flight: aRoute){
					ArrayList<HashMap<String, String>> route_details = this.sqld.select("route", "*", "route_id IN " + flight.toString().replaceAll("\\[", "(").replaceAll("\\]",")"), "ORDER BY date") ;

					ArrayList<HashMap<String, String>> time_working_flights = new ArrayList<HashMap<String, String>>();
					boolean first_pass = true;

					for(HashMap<String, String> row: route_details){
						LocalDate date = this.sql_formatter.parseLocalDate(row.get("date"));
						LocalTime time = this.fmt.parseLocalTime(row.get("time"));
						if(numTickets + Integer.valueOf(row.get("num_reserved_seats")) <= Integer.valueOf(row.get("num_total_seats"))
								&& ( takeoffDate.isBefore(date) || (takeoffDate.isEqual(date) && !takeoffTime.isBefore(time)))){
							if(first_pass){
								HashMap<String, String> dep_airport_data = this.sqld.select_first("airport", "*",  "airport_id=" + row.get("departure_airport_id"));
								HashMap<String, String> dest_airport_data = this.sqld.select_first("airport", "*", "airport_id="+ row.get("destination_airport_id"));
								row.put("departure_city_name", dep_airport_data.get("airport_city"));
								row.put("destination_city_name", dest_airport_data.get("airport_city"));
								first_pass = false;
							}
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
	public boolean save_route(String route_id){
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("route_id",  route_id);
		fields.put("passenger_id", this.user_id);
		fields.put("cancelled", "0");
		if(!this.sqld.insert("reservation", fields)){
			HashMap<String, String> update_to = new HashMap<String, String>();
			try {
				HashMap<String, String> route_res = this.sqld.select_first("route", "num_reserved_seats", "route_id="+route_id);
				update_to.put("num_reserved_seats", String.valueOf(Integer.valueOf(route_res.get("num_reserved_seats")) + 1));
				return !(this.sqld.update("route", update_to, "route_id="+route_id));
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	public boolean register(String username, String password){
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(password.getBytes());
			String encryptedPassword = new String(messageDigest.digest());

			Map<String, String> toInsert = new HashMap<String, String>();
			toInsert.put("username", username);
			toInsert.put("password", encryptedPassword);
			Scanner scanner = new Scanner(System.in);
			System.out.println("What's the name to associate with this account? (First and last only)");
			String[] name = scanner.nextLine().split(" ");
			toInsert.put("first_name", name[0]);
			toInsert.put("last_name", name[1]);
			String email = this.util.getValidEmail(scanner,"What is the email address that can be associated with this account?");
			toInsert.put("email", email);
			if(this.sqld.insert("passenger", toInsert)){
				System.out.println("Failed to register account.");
				return false;
			}
			else{
				System.out.println("Account registration was successful!");
				try {
					this.setUser_id(this.sqld.count_rows("passenger"));
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
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

			HashMap<String, String> res;
			try{
				res = this.sqld.select_first("passenger", "*", "username=" + username);
			}catch(IndexOutOfBoundsException e){
				System.out.println("No user by the username " + username + ".");
				System.out.println("Registering "+ username +" now.");
				return register(username, password);
			}
			messageDigest.update(password.getBytes());
			String encryptedPassword = new String(messageDigest.digest());

			if(res.get("password").equals(encryptedPassword)){
				System.out.println("Welcome back, " + username + "!");
				this.setUser_id(res.get("passenger_id"));
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
