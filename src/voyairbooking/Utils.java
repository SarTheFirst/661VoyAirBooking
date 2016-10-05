package voyairbooking;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Utils extends DefaultParser{
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
	public static String replaceCommaWithEndParen(String str){
		if(str.endsWith(",")){
			str = str.substring(0, str.length()-1) + ")";
		}
		return str;
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


	 
