package laix.interpreter;

import java.io.*;

public class FileHelper {

	public void testRead(String fileName) throws IOException {
		File file = new File(fileName);
		Reader reader = new FileReader(file);
		BufferedReader breader = new BufferedReader(reader);
		String line;
		System.out.println("->Input file:");
		while( (line = breader.readLine()) != null ) {
			System.out.println(line);
		}
		System.out.println("");
	}
}