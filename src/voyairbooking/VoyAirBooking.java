package voyairbooking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class VoyAirBooking {
	public VoyAirTools vabTools;
	public Utils util;
	public VoyAirBooking(boolean debug){
		this.vabTools = new VoyAirTools(debug);
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
				}
				else if(fname.contains("date")){
					fields.put(fname,"DATE");

				}
				else{
					fields.put(fname, "STRING");
				}
				headers.set(i, fname);

			}
			this.vabTools.sqld.create_table(tableName, fields);

			if(this.vabTools.sqld.count_rows(tableName)+1 != this.util.countLines(f.toString())){
				ArrayList<Map<String, String>> rows = new ArrayList<Map<String, String>>();
				while(scanner.hasNext()){
					HashMap<String, String> entries = new HashMap<String, String>();
					List<String> line = this.util.parseLine(scanner.nextLine());
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
		System.out.println("Finished processing files.");
	}
	public void printMenu(){
		System.out.println("0) Exit."); 
		if(!this.vabTools.is_logged_in())
			System.out.println("1) Log in");
		else
			System.out.println("1) Log out");
		System.out.println("2) Look at available cities.");
		System.out.println("3) Look at airline details.");

		if(!this.vabTools.is_logged_in()){
			System.out.println("4) Book a flight.");
			System.out.println("5) Look at previously booked flight information.");
			System.out.println("6) Edit flight reservation information.");
			System.out.println("7) Edit profile information."); 
		}
		System.out.println("What is your choice?");


	}
	public void previous_flight_info(){
		ArrayList<HashMap<String, String>> reserved_flights = this.vabTools.display_reserved_flights();
		for(HashMap<String, String> flight:reserved_flights){
			if(flight.get("cancelled").equals("0")){
				System.out.format("Flight number %s is departing from %s on %s.\nYou've reserved %s tickets at %s each.\n", 
						flight.get("route_id"), flight.get("airport_name"), this.vabTools.print_datetime.print(this.vabTools.sql_formatter.parseLocalDate((flight.get("date")))), flight.get("numTickets"), flight.get("price"));
			}
			else{
				System.out.format("You have CANCELLED flight number %s that was departing from %s on %s.\nYou had reserved %s tickets at %s each.\n", 
						flight.get("route_id"), flight.get("airport_name"), this.vabTools.print_datetime.print(this.vabTools.sql_formatter.parseLocalDate((flight.get("date")))), flight.get("numTickets"), flight.get("price"));
			}
		}
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
					int menu_choice;
					do{
						if(!DEBUG_MODE){
							if(vab.vabTools.is_logged_in()){
								do{
									vab.printMenu();
									menu_choice = scanner.nextInt();
								}while(vab.util.getValidInRange(0, 7, menu_choice));
							}
							else{
								do{
									vab.printMenu();
									menu_choice = scanner.nextInt();
								}while(vab.util.getValidInRange(0, 3, menu_choice));
							}
						}
						else{
							if(!vab.vabTools.is_logged_in()){
								do{
									vab.printMenu();
									menu_choice = Integer.valueOf(br.readLine());
								}while(vab.util.getValidInRange(0, 7, menu_choice));
							}
							else{
								do{
									vab.printMenu();
									menu_choice = Integer.valueOf(br.readLine());
								}while(vab.util.getValidInRange(0, 3, menu_choice));
							}
						}

						switch(menu_choice){
						case 0:
							System.out.println("Thank you for using VonAirBooking!");
							System.exit(0);
						case 1:
							if(vab.vabTools.is_logged_in()){
								String username, password;
								if(!DEBUG_MODE){
									System.out.println("What is the username you will be using today?");
									username = scanner.nextLine();
									System.out.println("What's your password, " + username + "?");
									password = scanner.nextLine();
								}
								else{
									System.out.println("What is the username you will be using today?");
									username = br.readLine();
									System.out.println("What's your password, " + username + "?");
									password = br.readLine();

								}
								if(vab.vabTools.tryLoggingIn(username, password)){
									System.out.println("Login succesful!");
									System.out.println("Welcome back, " + username);
								}
								else{
									System.out.println("Sorry you were not logged in correctly.");
								}
							}
							else{
								vab.vabTools.log_out();
								System.out.println("Logged out successfully");
							}
							break;
						case 2:
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
								String need_list = scanner.nextLine();
								keepListingCities = need_list.equalsIgnoreCase("y") || need_list.equalsIgnoreCase("yes");
							}while(keepListingCities);
							break;
						case 3:
							ArrayList<HashMap<String, String>> airline_list = vab.vabTools.get_airline_info();
							System.out.println("The arilines will be displayed in groups of 20.");
							int airline_counter = 0;
							boolean keep_listing_airlines = true;
							do{
								int i = airline_counter;
								while(i < airline_counter + 20){
									if(i < airline_list.size()){
										System.out.printf("%s is located in %s\n", airline_list.get(i).get("airline_name"), airline_list.get(i).get("airline_country"));
									}
									i++;
								}
								airline_counter = i;
								System.out.println("Keep listing cities? (Y/n)");
								String need_list = scanner.nextLine();
								keep_listing_airlines = need_list.equalsIgnoreCase("y") || need_list.equalsIgnoreCase("yes");
							}while(keep_listing_airlines);
							break;

						case 4:
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
							if(!DEBUG_MODE){
								try{
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
								catch(IllegalArgumentException e){
									System.err.println("Invalid date input!");
									continue;
								}
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
										System.out.printf("%-15s %-5s\n", WordUtils.capitalizeFully("route_id".replaceAll("_", " ")).replaceAll("Id", "ID"), route.get("route_id"));

										for (String key : keys) { 
											// Users don't need to see that much stuff.
											if(!key.contains("id") && !key.contains("num") && !key.contains("city")){
												if(key.equalsIgnoreCase("price")){
													System.out.printf("%-15s $%-5s\n", WordUtils.capitalizeFully(key.replaceAll("_", " ")), route.get(key));
												}
												else if(key.equalsIgnoreCase("date")){
													System.out.printf("%-15s %-5s\n", WordUtils.capitalizeFully(key.replaceAll("_", " ")), vab.vabTools.print_datetime.print(vab.vabTools.sql_formatter.parseLocalDate(route.get(key))));
												}
												else{
													System.out.printf("%-15s %-5s\n", WordUtils.capitalizeFully(key.replaceAll("_", " ")), route.get(key));
												}
											}
										}
										System.out.println("\n");
									}
								}
							}
							System.out.println("Please enter the route_ids of the flights you wish to take, seperated by commas.");
							List<String> user_choices;
							if(!DEBUG_MODE){
								user_choices = vab.vabTools.util.splitOnChar(scanner.nextLine(), ",");
							}
							else{
								user_choices = vab.vabTools.util.splitOnChar(br.readLine(), ",");
							}
							for(String c : user_choices){
								if(!vab.vabTools.save_route(c, numTickets)){
									System.out.println("Could not reserve seats");
								}
							}

							System.out.println("Did you want to make this a round trip? (Y)es/(No)");
							String round_trip;
							if(DEBUG_MODE) round_trip = br.readLine();
							else round_trip = scanner.nextLine();
							//TODO: Round trip
							
							break;
						case 5:
							vab.previous_flight_info();
							break;

						case 6:
							vab.previous_flight_info();

							ArrayList<HashMap<String, String>> reserved_flights = vab.vabTools.display_reserved_flights();
							System.out.println("Which flight do you want to change?");
							HashMap<String, String> target_flight = new HashMap<String, String>();
							String flight_number, action;
							boolean keep_going = true;
							do{
								if(DEBUG_MODE) flight_number = br.readLine();
								else flight_number = scanner.nextLine();

								for(HashMap<String, String> flight: reserved_flights){
									if(flight.get("route_id").equalsIgnoreCase(flight_number)){
										keep_going = false;
										target_flight = flight;
									}

								}
								if(keep_going){
									System.out.println("You haven't reserved seats on flight " + flight_number);
								}
							}while(keep_going);

							keep_going = true;
							int seat_amount;
							do{
								System.out.println("What do you want to do?");
								System.out.println("A) Add Seats");
								System.out.println("R) Remove Seats");
								System.out.println("C) Cancel Flight");
								System.out.println("C) Re-book a cancelled flight.");
								if(DEBUG_MODE) action = br.readLine();
								else action = scanner.nextLine();

								if(Arrays.asList(("a,r,c,b".split(","))).contains(action.toLowerCase())){
									keep_going = false;
								}
								else{
									System.out.println("Invalid value. Please enter one of the menu options");
								}
								switch(action.toLowerCase()){
								case "a":
									seat_amount = 0;
									System.out.println("How many seats do you want to add to your reservation?");
									if(DEBUG_MODE){
										seat_amount = Integer.valueOf(br.readLine());
									}
									else{
										keep_going = true;
										while(keep_going){
											try{
												seat_amount = scanner.nextInt();
												keep_going = false;
											}catch(InputMismatchException e){

											}
										}
									}
									vab.vabTools.add_seats(flight_number, seat_amount, Integer.parseInt(target_flight.get("numTickets")));
									break;
								case "r":
									seat_amount = 0;
									System.out.println("How many seats do you want to remove from your reservation?");
									if(DEBUG_MODE) seat_amount = Integer.valueOf(br.readLine());
									else{
										keep_going = true;
										while(keep_going){
											try{
												seat_amount = scanner.nextInt();
												if(Integer.parseInt(target_flight.get("numTickets")) < seat_amount){
													System.out.println("You can't subtract more tickets than you have reserved.");
													System.out.println("You reserved "+ target_flight.get("numTickets") + " tickets.");
												}

												else{ 
													keep_going = false;
												}
											}catch(InputMismatchException e){
												System.out.println("You need to enter an Integer number");
											}
										}
									}
									vab.vabTools.remove_seats(flight_number, seat_amount, Integer.parseInt(target_flight.get("numTickets")));
									break;

								case "c":
									if(vab.vabTools.cancelFlight(flight_number, target_flight.get("numTickets"))){
										System.out.println("Flight cancelled successfully.");
									}
									break;
								case "b":
									if(vab.vabTools.rebookFlight(flight_number, target_flight.get("numTickets"))){
										System.out.println("Flight rebooked successfully.");
									}
								}
							}while(keep_going);
							break;
						case 7:
							break;
						}

					}while(menu_choice != 0);
				}
			}
			else{
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
}
