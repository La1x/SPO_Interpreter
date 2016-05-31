package laix.interpreter;

//import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {
	private List<Token> tokens;
	private Token currentToken;
	private int currentTokenNumber = 0;
	//private 
		
	public void setTokens(List<Token> tokens){
		this.tokens = tokens;
	}

	public boolean match(){
		currentToken = tokens.get(currentTokenNumber);
		currentTokenNumber++;
		return false;
	}
	
	public void lang() throws Exception {
		boolean exist = false;
		while ( currentTokenNumber < tokens.size() && expr() ) {
			exist = true;
		}

		if (!exist) {
			throw new Exception("expr expected");
		}
	}
	
	public boolean expr() throws Exception {
		if( declare() || assign() ) {
			return true;
		} else {
			throw new Exception("declare or assign expected, but " + currentToken + "found.");
		}		
	}
	
	
	public boolean declare() throws Exception{
		//=log();
		if( varKw() ) {
			//=log();
			if ( var() ) {
				//=log();
				if ( sm() ) {
					//=log();
					return true;
				}else { //!sm()
					throw new Exception("VAR_KW, VAR found; SM expected; currentToken: " + currentToken);
				}
			}else { //!var()
				
				throw new Exception("VAR_KW found; VAR expected; currentToken: " + currentToken);
			}
		}else { //!varKw()
			//=log();
			currentTokenNumber--;
			return false;
		}
	}
	
	
	public boolean assign() throws Exception{
		if ( var() ) {
			//=log();
			if ( assignOp() ) {
				//=log();
				if ( stmt() ) {
					//=log();
					if ( sm() ) {
						//=log();
						return true;
					}else { //!sm
						throw new Exception("VAR, ASSIGN_OP, statment found; SM expected; currentToken: " + currentToken);
					}
				}else { //!stmt
					throw new Exception("VAR, ASSIGN_OP found; statment expected; currentToken: " + currentToken);
				}
			}else { //!assignOp
				throw new Exception("VAR found; ASSIGN_OP expected; currentToken: " + currentToken);
			}
		}else { //!var
			//=log();
			currentTokenNumber--;
			return false;
		}
	}
	
	
	public boolean stmt() throws Exception{
		if ( stmtUnit() ){
			while ( plus()||minus()||mult()||del() ){
				if(!stmtUnit()){
					throw new Exception("Statment Unit, Operation found; Statment Unit expected; currentToken: " + currentToken);
				}
			} 
			return true;
		}else {
			return false;
		}		
	}

	public boolean stmtUnit() throws Exception{
		if ( digit() || var() ){
			return true;
		}else {
			return false;
		}		
	}
	
	public boolean sm()  {
		match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}
		return currentToken.getName().equals("SM");
	}
	public boolean varKw(){
		match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}
		return currentToken.getName().equals("VAR_KW");
	}
	public boolean assignOp() {
		match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}
		return currentToken.getName().equals("ASSIGN_OP");
	}
	
	public boolean plus() {
		//=System.out.println("plus called.");
		match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}

		if ( currentToken.getName().equals("PLUS_OP") ) {
			return true;
		}else {
			currentTokenNumber--;
			return false;
		}
	}
	
	public boolean minus() {
		//=System.out.println("minus called.");
		match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}

		if ( currentToken.getName().equals("MINUS_OP") ) {
			return true;
		}else {
			currentTokenNumber--;
			return false;
		}
	}

	public boolean mult() {
		//=System.out.println("mult called.");
		match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}

		if ( currentToken.getName().equals("MULT_OP") ) {
			return true;
		}else {
			currentTokenNumber--;
			return false;
		}
	}

	public boolean del() {
		//=System.out.println("del called.");
		match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}

		if ( currentToken.getName().equals("DEL_OP") ) {
			return true;
		}else {
			currentTokenNumber--;
			return false;
		}
	}
	
		/*match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}
		return currentToken.getName().equals("SM");
	}*/
/*
	public boolean varKw(){
		match();
		if ( currentToken.getName().equals("WS"*/

	public boolean digit() {
		match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}

		if ( currentToken.getName().equals("DIGIT") ) {
		 	return true;
		}else {
			currentTokenNumber--;
			return false;
		}
	}

	public boolean var() {
		match();
		if ( currentToken.getName().equals("WS") ) {
			match();
		}
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



		while (currentExprTokenNumber < exprTokens.size()) {
			//match();
			if ( stmtUnit() ) {
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

	/*
	private void log() {
		System.out.println("\nCurrenToken: " + currentToken);
	}
	*/
}