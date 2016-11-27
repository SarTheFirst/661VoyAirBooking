package voyairbooking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class VoyAirBooking {
	public VoyAirTools vabTools;
	public VoyAirBooking(boolean debug){
		this.vabTools = new VoyAirTools(debug);
	}
	public void readInData(File f){
		try {
			String tableName = FilenameUtils.getBaseName(f.getName()).toLowerCase();
			if (tableName.endsWith("s")){
				tableName = this.vabTools.sqld.replaceStringEnding(tableName, "");
			}
			Scanner scanner = new Scanner(f);
			List<String> headers = this.vabTools.util.parseLine(scanner.nextLine());
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

			if(this.vabTools.sqld.count_rows(tableName)+1 != this.vabTools.util.countLines(f.toString())){
				ArrayList<Map<String, String>> rows = new ArrayList<Map<String, String>>();
				while(scanner.hasNext()){
					HashMap<String, String> entries = new HashMap<String, String>();
					List<String> line = this.vabTools.util.parseLine(scanner.nextLine());
					for(int i = 0; i < headers.size(); i++){
						// Honestly? Not sure why this happens. But sometimes a leading " occurs.
						// #Don'tUseOpenSourceCodeWithoutComments
						if(line.get(i).startsWith("\"") && !line.get(i).endsWith("\"")){
							line.set(i, line.get(i).substring(1));
						}
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
		boolean DEBUG_MODE = true;
		ArrayList<String> arguments = new ArrayList<String>();
		for(String s : args){
			arguments.add(s.toLowerCase());
		}
		Scanner scanner = new Scanner(System.in);
		BufferedReader br = null;
		if(DEBUG_MODE){
			try {
				br = new BufferedReader(new FileReader(Paths.get(System.getProperty("user.dir"), "tests", "test1.txt").toFile()));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		VoyAirBooking vab;
		try {
			vab = new VoyAirBooking(DEBUG_MODE);
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
					boolean successful_login;
					String username, password;
					if(!DEBUG_MODE){
						System.out.println("What is the username you will be using today?");
						username = scanner.nextLine();
						System.out.println("What's your password, " + username + "?");
						password = scanner.nextLine();
						successful_login = vab.vabTools.tryLoggingIn(username, password);
					}
					else{
						System.out.println("What is the username you will be using today?");
						username = br.readLine();
						System.out.println("What's your password, " + username + "?");
						password = br.readLine();
						successful_login = vab.vabTools.tryLoggingIn(username, password);
					}
					if(successful_login){
						String need_list;
						if(!DEBUG_MODE){
							need_list = vab.vabTools.util.getYesOrNo(scanner, "Do you need to look at the list of cities available?");

						}else{
							System.out.println("Do you need to look at the list of cities available?");
							need_list = br.readLine();

						}
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
						String departing_airport, arrival_airport;
						if(!DEBUG_MODE){
							System.out.println("Where are you departing from?");
							departing_airport = scanner.nextLine();
							System.out.println("Where are you going to?");
							arrival_airport = scanner.nextLine();
						}
						else{
							System.out.println("Where are you departing from?");
							departing_airport = br.readLine();
							System.out.println("Where are you going to?");
							arrival_airport = br.readLine();
						}

						LocalDate departureDate, arrivalDate;
						LocalTime departureTime, arrivalTime;
						int numTickets;
						try{
							if(!DEBUG_MODE){
								System.out.println("When do you want to depart? Please use the format dd/MM/YYYY HH:mm");
								String[] departureInput = scanner.nextLine().split(" ");
								departureDate =  vab.vabTools.dtf.parseLocalDate(departureInput[0]);
								departureTime =  vab.vabTools.fmt.parseLocalTime(departureInput[1]);

								System.out.println("When do you want to arrive? Please use the format dd/MM/YYYY HH:mm");
								String[] arrivalInput = scanner.nextLine().split(" ");
								arrivalDate = vab.vabTools.dtf.parseLocalDate(arrivalInput[0]);
								arrivalTime = vab.vabTools.fmt.parseLocalTime(arrivalInput[1]);

								String prompt = "How many tickets would you like to purchase? Please enter a positive integer";
								numTickets = vab.vabTools.util.getPositiveNumber(scanner, prompt);
							}
							else{
								System.out.println("When do you want to depart? Please use the format dd/MM/YYYY HH:mm");
								String[] departureInput = br.readLine().split(" ");
								departureDate =  vab.vabTools.dtf.parseLocalDate(departureInput[0]);
								departureTime =  vab.vabTools.fmt.parseLocalTime(departureInput[1]);

								System.out.println("When do you want to arrive? Please use the format dd/MM/YYYY HH:mm");
								String[] arrivalInput = br.readLine().split(" ");
								arrivalDate = vab.vabTools.dtf.parseLocalDate(arrivalInput[0]);
								arrivalTime = vab.vabTools.fmt.parseLocalTime(arrivalInput[1]);

								System.out.println("How many tickets would you like to purchase? Please enter a positive integer");
								numTickets = Integer.valueOf(br.readLine());
							}
							ArrayList<ArrayList<ArrayList<String>>> route_results = vab.vabTools.get_routes(departing_airport, arrival_airport);
							/*
							 * HashMap: Route row
							 * ArrayList: Leg Options
							 * ArrayList: Flight Leg
							 * ArrayList: Flight Options
							 */
							// HAHAHA NOPE.
							ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> trimmed_flights = vab.vabTools.trim_routes(route_results, arrivalDate, departureDate, arrivalTime, departureTime, numTickets);
							for(int i = 0; i < trimmed_flights.size(); i++){
								for(ArrayList<HashMap<String, String>> flight_leg: trimmed_flights.get(i)){
									System.out.println("\n\nOptions for the flight from " + flight_leg.get(0).get("departure_city_name") + " to " + flight_leg.get(0).get("destination_city_name") + ": ");

									for(HashMap<String, String> route: flight_leg){
										SortedSet<String> keys = new TreeSet<String>(route.keySet());
										for (String key : keys) { 
											System.out.printf("%-60s %-10s\n", key, route.get(key));
										}
										System.out.println("\n");
									}
								}
							}
						}


						catch(IllegalArgumentException e){
							System.err.println("Invalid date input! Relaunch the program and adhere to the the format provided.");
							System.exit(0);
						}
					}
					else{
						System.err.println("You failed to log in.");
						System.err.println("You must login to use VoyAirBooking.");
						System.exit(0);
					}
				}
			}
			else{
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
