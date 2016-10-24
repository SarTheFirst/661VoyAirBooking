/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voyairbooking;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import sql_driver.SQL_Driver;

public class VoyAirTools {
	protected SQL_Driver sqld;
	public VoyAirTools(boolean debugMode){
		try {
			this.sqld = new SQL_Driver(debugMode);
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
	public boolean save_route(String route_id, String user_id){
		return false;
	}
}
