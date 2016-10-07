package voyairbooking;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Utils extends DefaultParser{
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}

	static CommandLine parseCommandLine(String[] args){
		DefaultParser parser = new DefaultParser();
		final Options options = setUpCLIParsing();
		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Parsing failed.  Reason: " + e.getMessage()); 
			return null;
		} 
	}
	public static String replaceStringEnding(String str, String suffix){
		return str.substring(0, str.length()-1) + suffix;
	}

	private static Options setUpCLIParsing() { 
		Options options = new Options(); 
		final Option resetOption = Option.builder("r").hasArg() 
				.desc("Delete the database and start again.").longOpt("Reset").build(); 

		final Option textOnlyOption = Option.builder("s").hasArg() 
				.desc("Text-only interface.").longOpt("simple").build(); 

		final OptionGroup serverOrCli = new OptionGroup(); 
		serverOrCli.addOption(resetOption); 
		serverOrCli.addOption(textOnlyOption); 
		options.addOptionGroup(serverOrCli); 
		return options; 
	} 
}



