package voyairbooking;

import org.apache.commons.cli.CommandLine;

public class VoyAirBooking {

	public static void main(String[] args) {
		CommandLine cl = Utils.parseCommandLine(args);
		if(cl.hasOption("r")){
			// Delete db and run
		}
		else if(cl.hasOption("s")){
			// Run as text-only
		}
		else{
			// Keep current db and run inside a GUI. Default option
		}
	}

}
