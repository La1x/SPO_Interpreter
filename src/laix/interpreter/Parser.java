package laix.interpreter;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {
	private List<Token> tokens;
	private List<Token> stmtAccum;
	private Token currentToken;
	private int currentTokenNumber;
		
	public void setTokens(List<Token> tokens){
		this.tokens = tokens;
		currentTokenNumber = -1;
	}

	public boolean match(){
		currentTokenNumber++;
		currentToken = tokens.get(currentTokenNumber);
		System.out.println("[" + currentTokenNumber + "] " + currentToken);

		return false;
	}
	
	public void lang() throws Exception {
		say("Start of parsing.");
		boolean exist = false;
		while ( currentTokenNumber < (tokens.size()-1) && expr() ) {
			exist = true;
		}

		if (!exist) {
			throw new Exception("expr expected");
		}
		say("Done!");
	}
	
	public boolean expr() throws Exception {
		if( declare() || assign() ) {
			return true;
		} else {
			throw new Exception("declare or assign expected, but " + currentToken + "found.");
		}		
	}	
	
	public boolean declare() throws Exception{
		say("Calling declare:");
		match();
		if( varKw() ) {
			match();
			if ( var() ) {
				match();
				if ( sm() ) {
					return true;
				}else { //!sm()
					throw new Exception("VAR_KW, VAR found; SM expected; currentToken: " + currentToken);
				}
			}else { //!var()				
				throw new Exception("VAR_KW found; VAR expected; currentToken: " + currentToken);
			}
		}else { //!varKw()
			say("Declare not found.");
			currentTokenNumber--;
			return false;
		}
	}	
	
	public boolean assign() throws Exception{
		say("Calling assign:");
		match();
		if ( var() ) {
			match();
			if ( assignOp() ) {
				match();
				if ( stmt() ) {
					match();
					if ( sm() ) {
						PolizProcessor poliz =  new PolizProcessor( getPostfixToken(stmtAccum) );
						say("poliz result=" + poliz.go());
						return true;
					}else { //!sm
						throw new Exception("VAR, ASSIGN_OP, statment found; SM expected; currentToken: [" + currentTokenNumber + "] " + currentToken);
					}
				}else { //!stmt
					throw new Exception("VAR, ASSIGN_OP found; statment expected; currentToken: " + currentToken);
				}
			}else { //!assignOp
				throw new Exception("VAR found; ASSIGN_OP expected; currentToken: " + currentToken);
			}
		}else { //!var
			say("Assign not found.");
			currentTokenNumber--;
			return false;
		}
	}
	
	public boolean stmt() throws Exception{
		if ( digit() || var() ) {
			stmtAccum = new ArrayList<Token> ();
			stmtAccum.add( currentToken );
			match();
			if (currentToken.getName().equals("SM")) {
				currentTokenNumber--;
				return true;
			}

			while ( plus() || minus() || mult() || del() ) {
				stmtAccum.add( currentToken );
				match();
				if( !digit() && !var() ){
					throw new Exception("Statment Unit, Operation found; Statment Unit expected; currentToken: " + currentToken);
				}
				stmtAccum.add( currentToken );
				match();
			}
			if (currentToken.getName().equals("SM")) {
				currentTokenNumber--;
				return true;
			}

			return true;
		}else {
			return false;
		}		
	}

	/*
	public boolean stmtUnit() throws Exception{
		if ( digit() || var() ){
			return true;
		}else {
			return false;
		}		
	}*/
	
	public boolean sm()  {
		return currentToken.getName().equals("SM");
	}
	public boolean varKw(){
		return currentToken.getName().equals("VAR_KW");
	}
	public boolean assignOp() {
		return currentToken.getName().equals("ASSIGN_OP");
	}
	
	public boolean plus() {
		return ( currentToken.getName().equals("PLUS_OP") );
	}
	
	public boolean minus() {
		return ( currentToken.getName().equals("MINUS_OP") );
	}

	public boolean mult() {
		return ( currentToken.getName().equals("MULT_OP") );
	}

	public boolean del() {
		return ( currentToken.getName().equals("DEL_OP") );
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
				 currentToken.getName().equals("ASSIGN_OP") );
	}
	
	public List<PostfixToken> getPostfixToken( List<Token> infixTokens ) throws Exception {
		List<PostfixToken> postfixTokens = new ArrayList<PostfixToken>();
		Stack<PostfixToken> stack = new Stack<PostfixToken>();
		int lastPriority = 0;
		int currentInfixTokenNumber = -1;
		//Token currentInfixToken = null;

		// int tempTokenNumber = currentTokenNumber;
		Token tempToken = currentToken; //save origin cur token
		// currentToken = infixTokens.get(currentInfixTokenNumberI)

		while (currentInfixTokenNumber < (infixTokens.size()-1) ) {
			// match
			currentInfixTokenNumber++;
			currentToken = infixTokens.get(currentInfixTokenNumber);

			if ( digit() || var() ) {
				postfixTokens.add( new PostfixToken(currentToken) );
			}

			if ( plus() || minus() || mult() || del() ) {
				while( !stack.empty() && (new PostfixToken(currentToken).getOpPriority() <= lastPriority )) {
					postfixTokens.add( stack.pop() );
					if( stack.empty() ) {
						lastPriority = -1;
					} else {
						lastPriority = stack.peek().getOpPriority();
					}
				}

				stack.push( new PostfixToken(currentToken) );
				lastPriority = stack.peek().getOpPriority();
			}

			//infixTokens doesnt have "SM" token
			/*if ( currentToken.getName().equals("SM") ) {
				while( !stack.empty() ) {
					postfixTokens.add( stack.pop() );
				}
			}*/

			/*
			if ( brOpen() ) {
				
			}

			if ( brClose() ) {
				
			}
			*/
			//currentExprTokenNumber++;
		}

		while( !stack.empty() ) {
			postfixTokens.add( stack.pop() );
		}
		
		System.out.print("Parser: postfix tokens: ");
		for(int i = 0; i < postfixTokens.size(); i++) {
			System.out.print(postfixTokens.get(i).getValue() + " ");
		}
		System.out.println("");

		currentToken = tempToken;
		//currentTokenNumber = tempTokenNumber;
		return postfixTokens;
	}

		private void say( String str ) {
		System.out.println("Parser: " + str);
	}
}