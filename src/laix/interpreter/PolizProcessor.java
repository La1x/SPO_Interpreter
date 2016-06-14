package laix.interpreter;

import java.util.List;
import java.util.Stack;
import java.util.Scanner;
import java.lang.Integer;

public class PolizProcessor {
	private List<PostfixToken> postfixTokens;
	private PostfixToken currentToken;
	private VarTable varTable;
	private Stack<PostfixToken> stack;

	public PolizProcessor(List<PostfixToken> postfixTokens, VarTable vt) {
		this.postfixTokens = postfixTokens;
		this.varTable = vt;
	}

	public int go() throws Exception {
		stack = new Stack<PostfixToken>();
		int i = 0;
		while ( i < (postfixTokens.size()) ) {
			currentToken = postfixTokens.get(i);
			if ( digit() ) {
				stack.push(currentToken);
			}

			if ( var() ) {
				stack.push(currentToken);	
			}

			if ( op() ) {
				int a = 0;
				int b = 0;
				switch ( currentToken.getName() ) {
					case "PLUS_OP":
						a = getVarValue();
						b = getVarValue();
						stack.push( new PostfixToken( "DIGIT", Integer.toString(b + a) ) );
						break;
					case "MINUS_OP":
						a = getVarValue();
						b = getVarValue();
						stack.push( new PostfixToken( "DIGIT", Integer.toString(b - a) ) );
						break;
					case "DEL_OP":
						a = getVarValue();
						b = getVarValue();
						stack.push( new PostfixToken( "DIGIT", Integer.toString(b / a) ) );
						break;
					case "MULT_OP":
						a = getVarValue();
						b = getVarValue();
						stack.push( new PostfixToken( "DIGIT", Integer.toString(b * a) ) );
						break;
					case "GRT_OP":
						a = getVarValue();
						b = getVarValue();
						stack.push( new PostfixToken( "DIGIT", Integer.toString( (b > a) ? 1 : 0) ) );
						break;
					case "LST_OP":
						a = getVarValue();
						b = getVarValue();
						stack.push( new PostfixToken( "DIGIT", Integer.toString( (b < a) ? 1 : 0) ) );
						break;
				}
			}

			if ( function() ) {
				int a = 0;
				int b = 0;
				switch ( currentToken.getValue() ) {
					case "pow":
						a = getVarValue();
						b = getVarValue();
						Double tempValue = Math.pow(b,a);
						stack.push( new PostfixToken( "DIGIT", Integer.toString( tempValue.intValue() ) ) );
						break;
					case "fact":
						a = getVarValue();
						int fact = 1;
						for (; a > 0; fact *= a--);
						stack.push( new PostfixToken( "DIGIT", Integer.toString(fact) ) );
						break;
					case "print":
						a = getVarValue();
						System.out.println(a);
						return 0;
					case "write":
						int inputValue;
						String varName = stack.pop().getValue();
						// read from console
						Scanner in = new Scanner(System.in);
        				inputValue = in.nextInt();
        				System.out.println("Put in var table " + varName + '=' + inputValue);
						varTable.put(varName, inputValue);
						return 0;

				}
			}
			i++;
		}

		return Integer.parseInt( stack.pop().getValue() );
	}

	public boolean digit() {
		return currentToken.getName().equals("DIGIT");
	}

	public boolean var() {
		return currentToken.getName().equals("VAR");
	}

	public boolean op() {
		return ( currentToken.getName().equals("PLUS_OP") ||
				 currentToken.getName().equals("MINUS_OP") ||
				 currentToken.getName().equals("MULT_OP") ||
				 currentToken.getName().equals("DEL_OP") ||
				 currentToken.getName().equals("GRT_OP") ||
				 currentToken.getName().equals("LST_OP") );
	}

	public boolean function() {
		return currentToken.getName().equals("FUNCTION");
	}

	public void setVarTable(VarTable vt) {
		varTable = vt;
	}

	public VarTable getVarTable() {
		return varTable;
	}

	private Integer getVarValue() throws Exception {
		Integer varValue = null;
		if ( !stack.empty() && stack.peek().getName().equals("DIGIT") ) {
			varValue = Integer.parseInt( stack.pop().getValue() );
		}
		if ( !stack.empty() && stack.peek().getName().equals("VAR") ) {
			varValue = varTable.get( stack.pop().getValue() );
		}
		if ( varValue == null ) {
			throw new Exception("Variable must be declared: " + currentToken);
		}
		return varValue;
	}
}
