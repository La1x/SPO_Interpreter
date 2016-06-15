package laix.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;

public class Lexer {

	private List<Token> tokens = new ArrayList<Token>();

	String accum="";

	// Global patterns
	private Pattern pDIGIT = Pattern.compile("^0|[1-9]{1}[0-9]*$");
	private Pattern pVAR = Pattern.compile("^[a-zA-Z_]+$"); 
	private Pattern pWS = Pattern.compile("^\\s*$");

	// maps
	private Map<String, Pattern> commonTerminals = new HashMap<String, Pattern> ();
	private Map<String, Pattern> keyWords = new HashMap<String, Pattern> ();

	private String currentLucky = null;
	private int i;

	public Lexer() throws Exception{

		loadGrammar("grammar");
		commonTerminals.put("DIGIT", pDIGIT);
		commonTerminals.put("VAR", pVAR);
		commonTerminals.put("WS", pWS);		
	}

	public void processInput(String fileName) throws IOException {
		File file = new File(fileName);
		Reader reader = new FileReader(file);
		BufferedReader breader = new BufferedReader(reader);
		String line;
		while( (line = breader.readLine()) != null ) {
			processLine(line);
		}
		/*
		System.out.println("TOKEN("
			+ currentLucky
			+ ") recognized with value : "
			+ accum
			);*/

		tokens.add(new Token(currentLucky, accum));

		System.out.println("List of tokens:");
		for (Token token: tokens) {
			System.out.println(token);
		}

	}

	private void processLine(String line) {
		for ( i=0; i<line.length(); i++ ) {
			accum = accum + line.charAt(i);
			processAcumm();
		}
	}

	private void processAcumm() {
		boolean found = false;
		for ( String regExpName : commonTerminals.keySet() ) {
			Pattern currentPattern = commonTerminals.get(regExpName);
			Matcher m = currentPattern.matcher(accum);
			if ( m.matches() ) {
				currentLucky = regExpName;
				found = true;
			}
		}

		if ( currentLucky != null && !found ) {
			/*System.out.println("TOKEN("
			+ currentLucky
			+ ") recognized with value : "
			+ accum.substring(0, accum.length()-1)
			);*/

			if ( !currentLucky.equals("WS") ) {
				tokens.add(new Token(currentLucky, accum.substring(0, accum.length()-1)));	
			}
			
			i--;
			accum = "";
			currentLucky = null;
		}

		for ( String regExpName : keyWords.keySet() ) {
			Pattern currentPattern = keyWords.get(regExpName);
			Matcher m = currentPattern.matcher(accum);
			if ( m.matches() ) {
				currentLucky = regExpName;
				found = true;
			}
		}

		if ( currentLucky != null && !found ) {
			/*System.out.println("TOKEN("
			+ currentLucky
			+ ") recognized with value : "
			+ accum.substring(0, accum.length()-1)
			);*/

			tokens.add(new Token(currentLucky, accum.substring(0, accum.length()-1)));
			i--;
			accum = "";
			currentLucky = null;
		}
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public void loadGrammar(String filename) throws Exception {
		System.out.println("Load grammar.");
		File file = new File(filename);
		Reader reader = new FileReader(file);
		BufferedReader breader = new BufferedReader(reader);
		String line;
		while( (line = breader.readLine()) != null ) {
			if ( line.equals("[Key words]") ) {
				System.out.println(line);
				line = breader.readLine();
				while ( !line.equals("[End]") ) {
					String[] parts = line.split(Pattern.quote(" : "));
					Pattern pTemp = Pattern.compile("^" + parts[1].replace("\'","") + "$");
					keyWords.put(parts[0], pTemp);
					line = breader.readLine();
				}
			}
			if ( line.equals("[Terminals]") ) {
				System.out.println(line);
				line = breader.readLine();
				while ( !line.equals("[End]") ) {
					String[] parts = line.split(Pattern.quote(" : "));
					Pattern pTemp = Pattern.compile("^[" + parts[1].replace("\'","") + "]$");
					commonTerminals.put(parts[0], pTemp);
					line = breader.readLine();
				}
			}
		}
	}
	
}
