package laix.interpreter;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class PostfixMaker {
	private final String OPERATORS = "+-*/";

	private Stack<PostfixToken> stack = new Stack<PostfixToken>();
	private List<PostfixToken> outTokens = new ArrayList<PostfixToken>();
	private int currentTokenNumber;
	private PostfixToken currentToken;

	public void make( List<Token> infixTokens ) throws Exception {
		stack.clear();
		outTokens.clear();
		currentTokenNumber = -1;
		currentToken = null;

		String inputStatement = new String();
		for (Token t : infixTokens) {
			inputStatement += t.getValue();
		}

		while ( currentTokenNumber < (infixTokens.size()-1) ) {
			currentTokenNumber++;
			currentToken = new PostfixToken( infixTokens.get(currentTokenNumber) );

			if ( isSeparator(currentToken) ) {
				while ( !stack.empty() && !isOpenBracket(stack.peek()) ) {
					outTokens.add(stack.pop());
				}
			} else if ( isOpenBracket(currentToken) ) {
				stack.push(currentToken);
			} else if ( isCloseBracket(currentToken) ) {
				while ( !stack.empty() && !isOpenBracket(stack.peek()) ) {
					outTokens.add(stack.pop());
				}

				if ( stack.empty() || !isOpenBracket(stack.peek()) ) {
					throw new Exception("\n[!]Syntax error: open bracket expected at statement\n\"" + inputStatement + "\"\n");
				}
				stack.pop();

				if ( !stack.empty() && isFunction(stack.peek()) ) {
					outTokens.add( stack.pop() );
				}
			} else if ( isNumber(currentToken) ) {
					outTokens.add(currentToken);
			} else if ( isOperator(currentToken) ) {
				while (!stack.empty() && 
						isOperator( stack.peek() ) && 
						getPriority( currentToken ) <= getPriority( stack.peek() )
						) {
					outTokens.add( stack.pop() );
				}
				stack.push(currentToken);
			} else if ( isFunction(currentToken) ) {
				stack.push(currentToken);
			}			
		}

		while ( !stack.empty() ) {
			if ( isOpenBracket(stack.peek()) ) {
				throw new Exception("\n[!]Syntax error: close bracket expected at statement\n\"" + inputStatement + "\"\n");
			}
			outTokens.add( stack.pop() );
		}
	}

	public void print() {
		/*for(PostfixToken t : outTokens) {
            System.out.print(t.getValue() + " ");
        }
		System.out.println("");*/
	}

	public List<PostfixToken> get() {
		return outTokens;
	}

	private boolean isSeparator( PostfixToken t) {
		return t.getName().equals("SEP");
	}

	private boolean isOpenBracket( PostfixToken t) {
		return t.getName().equals("BRK_O");
	}

	private boolean isCloseBracket( PostfixToken t) {
		return t.getName().equals("BRK_C");
	}

	private boolean isOperator( PostfixToken t) {
		return ( t.getName().equals("PLUS_OP") ||
				 t.getName().equals("MINUS_OP") ||
				 t.getName().equals("MULT_OP") ||
				 t.getName().equals("DEL_OP") ||
				 t.getName().equals("GRT_OP") ||
				 t.getName().equals("LST_OP") ||
				 t.getName().equals("DOT_OP") );
	}

	private boolean isFunction( PostfixToken t) {
		return t.getName().equals("FUNCTION");
	}

	private int getPriority( PostfixToken t) {
		if (t.getValue().equals("+") || t.getValue().equals("-")) {
			return 1;
		}
		return 2;
	}

	private boolean isNumber( PostfixToken t) {
		if ( t.getName().equals("VAR") || t.getName().equals("DIGIT") ) {
			return true;
		}
		return false;
	}
}



	/*public List<PostfixToken> getPostfixToken( List<Token> infixTokens ) throws Exception {
		List<PostfixToken> postfixTokens = new ArrayList<PostfixToken>();
		Stack<PostfixToken> stack = new Stack<PostfixToken>();
		// int lastPriority = -1;
		int currentInfixTokenNumber = -1;

		Token savedToken = currentToken; //save origin cur token

		while (currentInfixTokenNumber < (infixTokens.size()-1) ) {
			// match
			currentInfixTokenNumber++;
			currentToken = infixTokens.get(currentInfixTokenNumber);

			if ( stmtUnit() ) {
				postfixTokens.add( new PostfixToken(currentToken));
				continue;
			}

			if ( op() ) {
				if ( stack.peek().getName().equals("BRK_O") ) {
					stack.push( new PostfixToken(currentToken));
					continue;
				}
				while( (!stack.empty() || !brOpen()) && stack.peek().getOpPriority() <= new PostfixToken(currentToken.getOpPriority()) ) {
					postfixTokens.add( stack.pop() );
				}
				stack.push( new PostfixToken(currentToken));
				continue;
			}

			if ( brOpen() ) {
				stack.push( new PostfixToken(currentToken) );
				continue;
			}

			if ( brClose() ) {
				while( !stack.peek().getName().equals("BRK_O") ) {
					postfixTokens.add( stack.pop() );
				}				
				stack.pop();
				continue;
			}

		}

		while( !stack.empty() ) {
			say("Stack.pop4: " + stack.peek() );
			postfixTokens.add( stack.pop() );
		}

		System.out.print("Parser: postfix tokens: ");
		for(int i = 0; i < postfixTokens.size(); i++) {
			System.out.print(postfixTokens.get(i).getValue() + " ");
		}
		System.out.println("");

		currentToken = savedToken;
		return postfixTokens;
	}*/
	