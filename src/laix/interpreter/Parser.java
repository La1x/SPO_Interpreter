package laix.interpreter;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {
	private List<Token> tokens;
	private List<Token> stmtAccum;
	private Token currentToken;
	private int currentTokenNumber;
	private VarTable topVarTable;
	private VarTable savedVarTable;
		
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

		topVarTable = null;
		savedVarTable = topVarTable;
		topVarTable = new VarTable(topVarTable);

		while ( currentTokenNumber < (tokens.size()-1) && expr() ) {
			exist = true;
		}

		if (!exist) {
			throw new Exception("expr expected");
		}
		topVarTable.print();
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
					topVarTable.put( tokens.get( currentTokenNumber-1 ).getValue(), 0 );
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
		String varName;
		match();
		if ( var() ) {
			varName = currentToken.getValue();
			match();
			if ( assignOp() ) {
				match();
				stmtAccum = new ArrayList<Token> ();
				if ( stmt() ) {
					match();
					if ( sm() ) {
						say("stmtAccum: ");
						print(stmtAccum);
						PostfixMaker pfm = new PostfixMaker();
						pfm.make(stmtAccum);
						say("Maker.out: ");
						pfm.print();
						PolizProcessor poliz =  new PolizProcessor( pfm.get(), topVarTable );
						Integer result = poliz.go();
						topVarTable.put( varName, result );
						say("poliz result = " + result);
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
	
	public boolean stmt() throws Exception {
		if ( operand() ) {
			match();

			if ( sm() ) {
				currentTokenNumber--;
				return true;
			}

			if ( op() ) {
				stmtAccum.add( currentToken );
				match();
				stmt();
			}

			if ( brOpen() || brClose() ) {
				throw new Exception("\n[!]Syntax error: wrong bracket in statement.");
			}
		} else {
			throw new Exception("1 " + currentToken);
		}
		return true;			
	}

	public boolean operand() throws Exception {
		if ( brOpen() ) {
			stmtAccum.add( currentToken );
			match();
			if ( operand() ) {
				return true;
			} else {
				throw new Exception("Operand expected. currentToken: " + currentToken);
			}
		}

		if ( stmtUnit() ) {
			stmtAccum.add( currentToken );
			match();
			while ( brClose() ) {
				stmtAccum.add( currentToken );
				match();
			}
			currentTokenNumber--;
			return true;			
		}

		return false;
	}
	
	public boolean stmtUnit() throws Exception{
		if ( digit() || var() ){
			return true;
		}else {
			return false;
		}		
	}
	
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

	public boolean brOpen() {
		return currentToken.getName().equals("BRK_O");
	}

	public boolean brClose() {
		return currentToken.getName().equals("BRK_C");
	}

	private void say( String str ) {
		System.out.println("Parser: " + str);
	}

	public void print( List<Token> ts ) {
		for(Token t : ts ) {
            System.out.print(t.getValue() + " ");
        }
		System.out.println("");
	}
}