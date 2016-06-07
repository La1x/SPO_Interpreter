package laix.interpreter;

import java.util.List;
import java.util.Stack;
import java.lang.Integer;

public class PolizProcessor {
	private List<PostfixToken> postfixTokens;
	private PostfixToken currentToken;

	public PolizProcessor(List<PostfixToken> postfixTokens) {
		this.postfixTokens = postfixTokens;
	}

	public int go() {
		Stack<PostfixToken> stack = new Stack<PostfixToken>();
		int i = 0;
		while ( i < (postfixTokens.size()) ) {
			currentToken = postfixTokens.get(i);
			if ( digit() ) {
				stack.push(currentToken);
			}

			if ( var() ) {
					
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

}
