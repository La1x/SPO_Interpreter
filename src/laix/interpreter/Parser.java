package laix.interpreter;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {
	private List<Token> tokens;
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
			match();
			if (currentToken.getName().equals("SM")) {
				currentTokenNumber--;
				return true;
			}

			while ( plus() || minus() || mult() || del() ) {
				match();
				if( !digit() && !var() ){
					throw new Exception("Statment Unit, Operation found; Statment Unit expected; currentToken: " + currentToken);
				}
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
	
	public List<PostfixToken> getPostfixToken( List<Token> exprTokens) throws Exception {
		List<PostfixToken> pfTokens = new ArrayList<PostfixToken>();
		Stack<PostfixToken> stack = new Stack<PostfixToken>();
		int lastPriority = 0;

		int tempTokenNumber = currentTokenNumber;
		Token tempToken = currentToken;
		int currentExprTokenNumber = 0; //??


		while (currentExprTokenNumber < exprTokens.size()) {
			//match();
			if ( digit() || var() ) {
				pfTokens.add( new PostfixToken(currentToken) );
			}

			if ( op() ) {
				//currentExprTokenNumber--;
				//if( !stack.empty() ) 
					while( !stack.empty() && (new PostfixToken(currentToken).getOpPriority() <= lastPriority )) {
						pfTokens.add( stack.pop() );
						if( stack.empty() ) {
							lastPriority = 0;
						} else {
							lastPriority = stack.peek().getOpPriority();
						}
					}
				//}

				stack.push( new PostfixToken(currentToken) );
				lastPriority = new PostfixToken(currentToken).getOpPriority();
			}

			if ( currentToken.getName().equals("SM") ) {
				while( !stack.empty() ) {
					pfTokens.add( stack.pop() );
				}
			}

			/*
			if ( brOpen() ) {
				
			}

			if ( brClose() ) {
				
			}
			*/
			//currentExprTokenNumber++;
		}

		System.out.println("->Postfix:");
		for(int i = 0; i < pfTokens.size(); i++) {
			System.out.print(pfTokens.get(i).getValue() + " ");
		}
		System.out.println("");

		currentToken = tempToken;
		currentTokenNumber = tempTokenNumber;
		return pfTokens;
	}

		private void say( String str ) {
		System.out.println("Parser: " + str);
	}
}