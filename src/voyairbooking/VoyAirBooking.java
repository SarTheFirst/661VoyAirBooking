package voyairbooking;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class VoyAirBooking {
	public VoyAirTools vabTools;
	public Utils util;
	public VoyAirBooking(){
		this.vabTools = new VoyAirTools(true);
		this.util = new Utils();
	}
	public void readInData(File f){
		try {
			String tableName = FilenameUtils.getBaseName(f.getName()).toLowerCase();
			if (tableName.endsWith("s")){
				tableName = this.vabTools.sqld.replaceStringEnding(tableName, "");
			}
			Scanner scanner = new Scanner(f);
			List<String> headers = this.util.parseLine(scanner.nextLine());
			HashMap<String, String> fields = new HashMap<String, String>();
			for(int i = 0; i < headers.size(); i++){
				String fname = headers.get(i).toLowerCase().replace(" ", "_");
				if(!fname.contains(tableName)){
					fields.put(tableName + "_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
				}
				if(fname.contains("_id")){
					String[] options = fname.split("_");
					String other_id = options[options.length-2];
					fields.put(fname, "STRING REFERENCES " + other_id + "(" + other_id + "_id) ON UPDATE CASCADE");
					headers.set(i, fname);
				}
				else{
					fields.put(fname, "STRING");
					headers.set(i, fname);
				}
			}
			this.vabTools.sqld.create_table(tableName, fields);

			if(this.vabTools.sqld.count_rows(tableName)+1 != this.util.countLines(f.toString())){
				ArrayList<Map<String, String>> rows = new ArrayList<Map<String, String>>();
				while(scanner.hasNext()){
					HashMap<String, String> entries = new HashMap<String, String>();
					List<String> line = this.util.parseLine(scanner.nextLine());
					for(int i = 0; i < headers.size(); i++){
						entries.put(headers.get(i), line.get(i));
					}
					rows.add(entries);
				}
				this.vabTools.sqld.batch_insert(tableName, rows);

			}
			scanner.close();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public void read_in_files(){
		File dir = Paths.get(System.getProperty("user.dir"), "data").toFile();
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if(!child.getName().startsWith(".")){
					this.readInData(child);
				}
			}
		}
		System.out.println("Done.");
	}
	public static void main(String[] args) {
		ArrayList<String> arguments = new ArrayList<String>();
		for(String s : args){
			arguments.add(s.toLowerCase());
		}
		Scanner scanner = new Scanner(System.in);

		VoyAirBooking vab;
		try {
			vab = new VoyAirBooking();
			if(arguments.contains("-t") || arguments.contains("--text-only")){
				if(arguments.contains("-r") || arguments.contains("--reset")){
					vab.vabTools.sqld.drop_all();
					vab.read_in_files();
				}
				else if(arguments.contains("-h") || arguments.contains("--help")){
					System.out.println("Welcome to VoyAirBooking!\n Available commands: ");
					System.out.println("-r [--reset] drops all tables in the database and repopulates\n\tthem with data within the data/ subfolder.");
					System.out.println("-t [--text-only] runs application through text-only interface.");
					System.out.println("-g [--graphics] runs application with graphical interface. [default]");
					System.out.println("-h [--help] displays this help text.");
				}
				else{
					System.out.println("Do you need to look at the list of cities available?");
					String need_list = scanner.nextLine();
					if(need_list.equalsIgnoreCase("y") || need_list.equalsIgnoreCase("yes")){
						ArrayList<String> city_list = vab.vabTools.get_cities();
						System.out.println("The cities will be displayed in groups of 20.");
						int city_counter = 0;
						boolean keepListingCities = true;
						do{
							int i = city_counter;
							while(i < city_counter + 20){
								if(i < city_list.size()){
									System.out.println(city_list.get(i));
								}
								i++;
							}
							city_counter = i;
							System.out.println("Keep listing cities? (Y/n)");
							need_list = scanner.nextLine();
							keepListingCities = need_list.equalsIgnoreCase("y") || need_list.equalsIgnoreCase("yes");
						}while(keepListingCities);
					}
					System.out.println("Where are you departing from?");
					String departing_airport = scanner.nextLine();
					System.out.println("Where are you going to?");
					String arrival_airport = scanner.nextLine();

					try{
						System.out.println("When do you want to depart? Please use the format dd/MM/YYYY HH:mm");
						String[] departureInput = scanner.nextLine().split(" ");
						LocalDate departureDate = LocalDate.parse(departureInput[0]);
						LocalTime departureTime = LocalTime.parse(departureInput[1]);

						System.out.println("When do you want to arrive? Please use the format dd/MM/YYYY HH:mm");
						String[] arrivalInput = scanner.nextLine().split(" ");
						LocalDate arrivalDate = LocalDate.parse(arrivalInput[0]);
						LocalTime arrivalTime = LocalTime.parse(arrivalInput[1]);

						//System.out.println("Going from Kelowna to Campbell River");
						ArrayList<ArrayList<ArrayList<String>>> route_results = vab.vabTools.get_routes(departing_airport, arrival_airport);
						//ArrayList<ArrayList<ArrayList<String>>> res = vab.vabTools.get_routes("Kelowna", "Campbell River");
						/*
						 * HashMap: Route row
						 * ArrayList: Leg Options
						 * ArrayList: Flight Leg
						 * ArrayList: Flight Options
						 */
						ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> trimmed_flights = vab.vabTools.trim_routes(route_results, arrivalDate, departureDate, arrivalTime, departureTime);
						System.out.println("There are " + trimmed_flights.size() + " options for you to choose from.");
						for(int i = 0; i < trimmed_flights.size(); i++){
							System.out.println(i + ") ");
							for(ArrayList<HashMap<String, String>> flight_leg: trimmed_flights.get(i)){
								System.out.println("hello world");
							}
						}
					}

					catch(IllegalArgumentException e){
						System.err.println("Invalid date input! Relaunch the program and adhere to the the format provided.");
						System.exit(0);
					}
				}
			}
			else{
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
