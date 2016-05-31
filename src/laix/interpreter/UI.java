package laix.interpreter;

import java.util.List;
import java.io.*;

public class UI{

	static FileHelper fileHelper = new FileHelper();

	public static void main(String[] args) throws Exception {
		//wrongInput();
		validInput();
	}

	static void wrongInput() throws Exception {
		fileHelper.testRead("wrong-test.input");
		process("wrong-test.input");
	}

	static void validInput() throws Exception {
		fileHelper.testRead("valid-test2.input");
		process("valid-test2.input");
	}

	static void process(String fileName) throws Exception {
		Lexer lexer = new Lexer();
		lexer.processInput(fileName);
		List<Token> tokens = lexer.getTokens();

		Parser parser= new Parser();
		parser.setTokens(tokens);
		parser.lang();

		//List<PostfixToken> postfixToken = parser.getPostfixToken();
		//PolizProcessor processor = new PolizProcessor(postfixToken);
		//process poliz body , print var table
		//processor.go();
		System.out.println("Done!");
	}
}