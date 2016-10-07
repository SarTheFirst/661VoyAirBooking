package voyairbooking;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;

import sql_driver.SQL_Driver;

public class VoyAirBooking {
	private SQL_Driver sqld;
	private CommandLine cl;
	public VoyAirBooking(String[] args) throws SQLException{
		this.sqld = new SQL_Driver(false); //debug = false
		this.cl = Utils.parseCommandLine(args);

	}
	public void readInData(File f){
		// Look man, don't reinvent the wheel when apache's done that for you...
		String tableName = FilenameUtils.getBaseName(f.getName()).toLowerCase();
		if (tableName.endsWith("s")){
			// In addition to the whole consistency thing, traditionally databases are singular, I've experienced
			tableName = Utils.replaceStringEnding(tableName, "");
		}
		Scanner scanner;
		try {
			scanner = new Scanner(f);
			String[] headers = scanner.nextLine().split(",");
			ArrayList<String> fields = new ArrayList<String>();
			for (int i = 0; i < headers.length; i++){
				String str =  "'" + headers[i].toLowerCase() + "' string";
				if(i == 0 && str.contains(tableName)){
					str += " primary key";
				}
				else if(i == 0){
					fields.add("'" + tableName + "_id' string primary key");
				}
				else if(str.contains("id")){
					String[] reference = headers[i].split("\\s+");
					String other_table = reference[1].toLowerCase();;
					if(reference.length == 2){
						other_table = reference[0].toLowerCase();
					}
					str += " REFERENCES " + other_table + "(" + other_table + "_id) ON UPDATE CASCADE";
				}
				fields.add(str);
			}
			this.sqld.create_table(tableName, fields);
			
			while (scanner.hasNext()){
				String[] line = scanner.nextLine().split(",");
				Map<String, String> entries = new HashMap<String, String>();
				for(int i = 0; i < headers.length; i++){
					// WHAT DO YOU MEAN I'VE BEEN DOING THIS FOR YEARS AND JUST DISCOVERED THIS LIBRARY.
					entries.put("'"+headers[i]+"'", StringEscapeUtils.escapeSql(line[i]));
				}
				this.sqld.insert(tableName, entries);
			}

			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			VoyAirBooking vab = new VoyAirBooking(args);
			if(vab.cl.hasOption("r")){
				// Delete db and run
			}
			else if(vab.cl.hasOption("s")){
				// Run as text-only
			}
			else{
				// get all the data files
				File dir = Paths.get(System.getProperty("user.dir"), "data").toFile();

				File[] directoryListing = dir.listFiles();
				if (directoryListing != null) {
					for (File child : directoryListing) {
						vab.readInData(child);
					}
				} 
				else {
				}
			}
		}
		catch (SQLException e) {
			System.err.println("Unable to connect to database.");
		}

	}
}
