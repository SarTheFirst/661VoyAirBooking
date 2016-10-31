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
import java.util.Map;

import sql_driver.SQL_Driver;

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
	public ArrayList<HashMap<String, String>> get_routes(String start_city, String end_city){
		try {
			ArrayList<String> end_id = get_field(this.sqld.select("airport", "*", "city="+end_city), "airport_id");
			ArrayList<String> start_id = get_field(this.sqld.select("airport", "*", "city="+start_city), "airport_id");
			String end_ids = surround_parens(String.join(", ",end_id));
			String start_ids = surround_parens(String.join(", ", start_id));
			return this.sqld.select("route", "*", "takeoff_airport_id IN " + start_ids + " AND destination_airport_id IN " + end_ids);
		} catch (SQLException e) {
			return null;
		}
	}
	public ArrayList<HashMap<String, String>> get_joined_routes(String start_city, String end_city){
		//Die
		return null;
	}
	public boolean save_route(String route_id, String user_id){
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("route_id",  route_id);
		fields.put("account_id", user_id);
		return this.sqld.insert("transaction", fields);
	}

	public void register(String username, String password){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			Map<String, String> toInsert = new HashMap<String, String>();
			toInsert.put("username", username);
			toInsert.put("password", md.digest(password.getBytes()).toString());
			if(this.sqld.insert("account", toInsert)){
				System.out.println("Failed to register account.");
			}
			else{
				System.out.println("Account registration was successful!");
				try {
					this.setUser_id(this.sqld.count_rows("account"));
				
				} catch (SQLException e) {
					System.err.println("How the hell did you get here?");
				}
			}
		} catch (NoSuchAlgorithmException e) {
			System.err.println("No Such Algorithm");
		}

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
				System.out.println("");
				return false;
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
