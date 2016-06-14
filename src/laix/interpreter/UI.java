package laix.interpreter;

import java.util.List;
import java.io.*;

public class UI{

	static FileHelper fileHelper = new FileHelper();

	public static void main(String[] args) throws Exception {
		//wrongInput();
		System.out.println("Start.");
		validInput();
		System.out.println("Done!");
	}

	static void wrongInput() throws Exception {
		fileHelper.testRead("wrong-test.input");
		process("wrong-test.input");
	}

	static void validInput() throws Exception {
		fileHelper.testRead("valid-test.input");
		process("valid-test.input");
	}

	static void process(String fileName) throws Exception {
		Lexer lexer = new Lexer();
		lexer.processInput(fileName);
		List<Token> tokens = lexer.getTokens();

		Parser parser = new Parser();
		parser.setTokens(tokens);
		parser.lang();
	}
}