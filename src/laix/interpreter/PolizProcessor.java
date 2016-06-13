package laix.interpreter;

import java.util.List;
import java.util.Stack;
import java.lang.Integer;

public class PolizProcessor {
	private List<PostfixToken> postfixTokens;
	private PostfixToken currentToken;
	private VarTable varTable;

	public PolizProcessor(List<PostfixToken> postfixTokens, VarTable vt) {
		this.postfixTokens = postfixTokens;
		this.varTable = vt;
	}

	public int go() throws Exception {
		Stack<PostfixToken> stack = new Stack<PostfixToken>();
		int i = 0;
		while ( i < (postfixTokens.size()) ) {
			currentToken = postfixTokens.get(i);
			if ( digit() ) {
				stack.push(currentToken);
			}

			if ( var() ) {
				Integer varValue = varTable.get( currentToken.getValue() );
				if ( varValue == null ) {
					throw new Exception("Variable must be declared: " + currentToken);
				}
				stack.push( new PostfixToken( "DIGIT", varValue.toString() ) );	
			}

			if ( op() ) {
				int a = 0;
				int b = 0;
				switch ( currentToken.getName() ) {
					case "PLUS_OP":
						a = Integer.parseInt( stack.pop().getValue() );
						b = Integer.parseInt( stack.pop().getValue() );
						stack.push( new PostfixToken( "DIGIT", Integer.toString(b + a) ) );
						break;
					case "MINUS_OP":
						a = Integer.parseInt( stack.pop().getValue() );
						b = Integer.parseInt( stack.pop().getValue() );
						stack.push( new PostfixToken( "DIGIT", Integer.toString(b - a) ) );
						break;
					case "DEL_OP":
						a = Integer.parseInt( stack.pop().getValue() );
						b = Integer.parseInt( stack.pop().getValue() );
						stack.push( new PostfixToken( "DIGIT", Integer.toString(b / a) ) );
						break;
					case "MULT_OP":
						a = Integer.parseInt( stack.pop().getValue() );
						b = Integer.parseInt( stack.pop().getValue() );
						stack.push( new PostfixToken( "DIGIT", Integer.toString(b * a) ) );
						break;
				}
			}

			if ( function() ) {
				int a = 0;
				int b = 0;
				switch ( currentToken.getValue() ) {
					case "pow":
						a = Integer.parseInt( stack.pop().getValue() );
						b = Integer.parseInt( stack.pop().getValue() );
						Double tempValue = Math.pow(b,a);
						stack.push( new PostfixToken( "DIGIT", Integer.toString( tempValue.intValue() ) ) );
						break;
					case "fact":
						a = Integer.parseInt( stack.pop().getValue() );
						int fact = 1;
						for (; a > 0; fact *= a--);
						stack.push( new PostfixToken( "DIGIT", Integer.toString(fact) ) );
						break;

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
				 currentToken.getName().equals("DEL_OP") );
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
}
