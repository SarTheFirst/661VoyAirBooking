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
		HashSet<String> listOfCities = new HashSet<String>();
		try {
			ArrayList<HashMap<String, String>> res = this.sqld.select("airport", "city", "", true);
			for(HashMap<String, String> row : res){
				listOfCities.add(row.get("city"));
			}
			ArrayList<String> toReturn = new ArrayList<String>(listOfCities);
			Collections.sort(toReturn, String.CASE_INSENSITIVE_ORDER);
			return toReturn;

		} catch (SQLException e) {
			return null;
		}
	}

}
