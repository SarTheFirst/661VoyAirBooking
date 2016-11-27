package voyairbooking;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.validator.routines.EmailValidator;

public class Utils{
	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';

	public List<String> parseLine(String cvsLine) {
		return parseCSVLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
	}

	public List<String> parseLine(String cvsLine, char separators) {
		return parseCSVLine(cvsLine, separators, DEFAULT_QUOTE);
	}
	public String getValidEmail(Scanner scanner, String prompt){
		String email;
		boolean need_repeat = false;

		do{
			System.out.println(prompt);
			email = scanner.nextLine();
			if (email != null && !("".equals(email))){
				email = email.trim();
				EmailValidator ev = EmailValidator.getInstance();
				if(ev.isValid(email)){
					return email;
				}

			}
			need_repeat = true;
			System.out.println("Invalid email address.");

		}while(need_repeat);
		return email;
	}
	public String getYesOrNo(Scanner scanner, String prompt){
		String toReturn = "";
		boolean need_repeat = false;
		do{
			System.out.println(prompt);
			toReturn = scanner.nextLine();
			if(need_repeat){
				System.out.println("You need to enter (Y)es or (N)o.");
				need_repeat = true;
			}
		}while( toReturn.equalsIgnoreCase("y") || toReturn.equalsIgnoreCase("yes") ||
				toReturn.equalsIgnoreCase("n") || toReturn.equalsIgnoreCase("no"));
		return toReturn;

	}
	public int getPositiveNumber(Scanner scanner, String prompt){
		try{
			System.out.println(prompt);
			int aNum = Integer.valueOf(scanner.nextLine());
			return aNum;
		}
		catch(NumberFormatException e){
			System.out.println("Invalid input");
			return getPositiveNumber(scanner, prompt);
		}
	}
	@SuppressWarnings("null")
	public List<String> parseCSVLine(String cvsLine, char separators, char customQuote) {

		List<String> result = new ArrayList<>();

		//if empty, return!
		if (cvsLine == null && cvsLine.isEmpty()) {
			return result;
		}

		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();

		for (char ch : chars) {

			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else {

					//Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}

				}
			} else {
				if (ch == customQuote) {

					inQuotes = true;

					//Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}

					//double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}

				} else if (ch == separators) {

					result.add(curVal.toString());

					curVal = new StringBuffer();
					startCollectChar = false;

				} else if (ch == '\r') {
					//ignore LF characters
					continue;
				} else if (ch == '\n') {
					//the end, break!
					break;
				} else {
					curVal.append(ch);
				}
			}

		}

		result.add(curVal.toString());

		return result;
	}
	public int countLines(String filename) throws IOException {
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
}



