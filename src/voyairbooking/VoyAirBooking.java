package voyairbooking;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import sql_driver.SQL_Driver;

public class VoyAirBooking {
	private SQL_Driver sqld;
	public VoyAirBooking() throws SQLException{
		this.sqld = new SQL_Driver(true); //debug = false

	}
	public void readInData(File f){
		try {
			String tableName = FilenameUtils.getBaseName(f.getName()).toLowerCase();
			if (tableName.endsWith("s")){
				tableName = Utils.replaceStringEnding(tableName, "");
			}
			boolean created_id = false;
			Scanner scanner = new Scanner(f);
			List<String> headers = Utils.parseLine(scanner.nextLine());
			HashMap<String, String> fields = new HashMap<String, String>();
			for(int i = 0; i < headers.size(); i++){
				String fname = headers.get(i).toLowerCase().replace(" ", "_");
				if(!fname.endsWith("_id") && i == 0){
					fields.put(tableName + "_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
					created_id = true;
					headers.set(i,tableName + "_id");
				}
				else if(fname.contains("_id") && i != 0){
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
			this.sqld.create_table(tableName, fields);

			if(this.sqld.count_rows(tableName) != Utils.countLines(f.toString())){
				ArrayList<Map<String, String>> rows = new ArrayList<Map<String, String>>();
				while(scanner.hasNext()){
					HashMap<String, String> entries = new HashMap<String, String>();
					List<String> line = Utils.parseLine(scanner.nextLine());
					if(created_id){
						headers.remove(0);
						created_id = false;
					}
					for(int i = 0; i < headers.size(); i++){
						entries.put(headers.get(i), line.get(i));
					}
					rows.add(entries);
				}
				this.sqld.batch_insert(tableName, rows);

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

		VoyAirBooking vab;
		try {
			vab = new VoyAirBooking();
			if(arguments.contains("-t") || arguments.contains("--text-only")){
				if(arguments.contains("-r") || arguments.contains("--reset")){
					vab.sqld.drop_all();
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
					Scanner scanner = new Scanner(System.in);
					System.out.println("Where are you headed? Do you need to see the cities? Y/n");
					String know_where = scanner.nextLine();
					if(know_where.equalsIgnoreCase("y") || know_where.equalsIgnoreCase("yes")){
						ArrayList<String> listOfCities = vab.sqld.select("airport", "city");
						Collections.sort(listOfCities, String.CASE_INSENSITIVE_ORDER);
						for(String city :listOfCities){
							System.out.println(city);
						}
						System.out.println("\nWhere are you headed?");
					}
					String going_to = scanner.nextLine();
					ArrayList<HashMap<String, String>> res = vab.sqld.select("airport", "*", "city="+going_to);
					scanner.close();
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
