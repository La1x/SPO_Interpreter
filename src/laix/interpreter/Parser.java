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

	private boolean isWhileComplete = false;
		
	public void setTokens(List<Token> tokens){
		this.tokens = tokens;
		currentTokenNumber = -1;
	}

	public boolean match(){
		currentTokenNumber++;
		currentToken = tokens.get(currentTokenNumber);
		// System.out.println("[" + currentTokenNumber + "] " + currentToken);

		return false;
	}
	
	public void lang() throws Exception {
		say("Start of parsing.");
		boolean exist = false;

		topVarTable = null;
		savedVarTable = topVarTable;
		topVarTable = new VarTable(topVarTable);

		while ( currentTokenNumber < (tokens.size()-1) && expr() ) {
			/*if ( isWhileComplete ) {
				isWhileComplete = false;
			}*/
			exist = true;
		}

		if (!exist) {
			throw new Exception("expr expected");
		}
		topVarTable.print();
		say("Done!");
	}
	
	public boolean expr() throws Exception {
		if( declare() || assign() || struct_expr() || while_expr() || function_kw() ) {
			return true;
		} else {
			return false;
			// throw new Exception("declare or assign expected, but " + currentToken + "found.");
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
					topVarTable.varRegistration( tokens.get( currentTokenNumber-1 ).getValue(), 0 );
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

	// --- WHILE ---
	public boolean while_expr() throws Exception {
		say("Calling while:");
		
		savedVarTable = topVarTable;
		topVarTable = new VarTable(topVarTable);

		boolean isOk = while_loop();

		topVarTable = savedVarTable;
		// topVarTable.deleteCurrent();
		return isOk;
	}

	public boolean while_loop() throws Exception {
		int startWhileTokenNumber = currentTokenNumber;
		match();
		if ( while_decl() ) {
			match();
			if ( cbrOpen() ) {
				if ( isWhileComplete == false ) {
					if ( while_body() ) {
						match();
						if ( cbrClose() ) {
							currentTokenNumber = startWhileTokenNumber;
							while_loop();
							return true;
						} else { // !cbrClose
							throw new Exception("\n[!]Syntax error: curly close bracket expected.");
						}
					} else { // !while_body
						throw new Exception("\n[!]Syntax error: while body expected.");
					}
				} else { // isWhileComplete == true
					while ( !cbrClose() ) {
						match();
					}
					// topVarTable = savedVarTable;
					return true;
				}				
			} else { // !cbrOpen
				throw new Exception("\n[!]Syntax error: curly open bracket expected.");
			}
		} else { // !while_decl
			say("While not found.");
			currentTokenNumber--;
			return false;
		}
		// return true;
	}

	public boolean while_decl() throws Exception {
		if ( while_kw() ) {
			match();
			if ( brOpen() ) {
				match();
				if ( while_limit() ) {
					match();
					if ( brClose() ) {
						return true;
					} else {
						throw new Exception("\n[!]Syntax error: close bracket expected in while declaration.");
					}
				} else {
					throw new Exception("\n[!]Syntax error: limit in while head expected in while declaration.");
				}
			} else {
				throw new Exception("\n[!]Syntax error: open bracket expected in while declaration.");
			}
		} else { // !while_name
			return false;
		}
	}

	public boolean while_body() throws Exception {
		while ( !cbrClose() ) {
			expr();
		}
		return true;
	}

	public boolean while_limit() throws Exception {
		stmtAccum = new ArrayList<Token> ();
		if ( var() ) {
			stmtAccum.add( currentToken );
			match();
			if ( op() ) {
				stmtAccum.add( currentToken );
				match();
				if ( stmtUnit() ) {
					stmtAccum.add( currentToken );

					say("stmtAccum: ");
					print(stmtAccum);
					PostfixMaker pfm = new PostfixMaker();
					pfm.make(stmtAccum);
					say("Maker.out: ");
					pfm.print();
					PolizProcessor poliz =  new PolizProcessor( pfm.get(), topVarTable );
					Integer result = poliz.go();
					say("poliz result = " + result);

					if ( result == 1 ) {
						isWhileComplete = false;
					} else if ( result == 0 ) {
						isWhileComplete = true;
					} else {
						throw new Exception("\n[!]Syntax error: boolean statement expected in while head.");
					}
					return true;
				} else {
					throw new Exception("\n[!]Syntax error: var or digit expected in while head.");
				}
			} else {
				throw new Exception("\n[!]Syntax error: operation expected in while head.");
			}
		} else {
			return false;
		}
	}

	public boolean while_kw() {
		return ( currentToken.getName().equals("WHILE_KW") );
	}
	// --- WHILE (END) ---

	// --- FUNCTION_KW ---
	public boolean function_kw()  throws Exception {
		say("Calling function_kw:");
		stmtAccum = new ArrayList<Token> ();
		match();
		if ( function() ) {
			stmtAccum.add( currentToken );
			match();
			if ( brOpen() ) {
				stmtAccum.add( currentToken );
				match();
				if ( stmtUnit() ) {
					stmtAccum.add( currentToken );
					match();
					if ( brClose() ) {
						stmtAccum.add( currentToken );
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
							return true;
						} else { //!sm
							throw new Exception("VAR, ASSIGN_OP, statment found; SM expected; currentToken: [" + currentTokenNumber + "] " + currentToken);
						}						
					} else {
						throw new Exception("\n[!]Syntax error: close bracket or separator expected in function: " + currentToken);	
					}
				} else {
					throw new Exception("\n[!]Syntax error: operand expected in function: " + currentToken);	
				}
			} else {
				throw new Exception("\n[!]Syntax error: open bracket expected in function: " + currentToken);
			}
		} else { // !function
			say("Function_kw not found.");
			currentTokenNumber--;
			return false;
		}		
		// return true;
	}
	// --- FUNKTION_KW END ---
	
	// --- STATEMENT ---
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
			throw new Exception("\n[!]Syntax error: statment expected. currentToken" + currentToken);
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
				throw new Exception("\n[!]Syntax error: operand expected. currentToken: " + currentToken);
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

		if ( functionStmt() ) {
			return true;
		}

		return false;
	}
	
	public boolean stmtUnit() throws Exception{
		if ( digit() || struct_stmt() || var() ){
			return true;
		} else {
			return false;
		}		
	}

	public boolean functionStmt() throws Exception {
		if ( function() ) {
			stmtAccum.add( currentToken );
			match();
			if ( brOpen() ) {
				stmtAccum.add( currentToken );
				match();
				if ( stmtUnit() ) {
					stmtAccum.add( currentToken );
					match();
					if ( brClose() ) {
						stmtAccum.add( currentToken );
						return true;
					} else if ( separator() ) {
						stmtAccum.add( currentToken );
						match();
						if ( stmtUnit() ) {
							stmtAccum.add( currentToken );
							match();
							if ( brClose() ) {
								stmtAccum.add( currentToken );
								return true;
							} else {
								throw new Exception("\n[!]Syntax error: close bracket expected or function can have only two operands: " + currentToken);	
							}
						} else {
							throw new Exception("\n[!]Syntax error: operand expected in function: " + currentToken);	
						}
					} else {
						throw new Exception("\n[!]Syntax error: close bracket or separator expected in function: " + currentToken);	
					}
				} else {
					throw new Exception("\n[!]Syntax error: operand expected in function: " + currentToken);	
				}
			} else {
				throw new Exception("\n[!]Syntax error: open bracket expected in function: " + currentToken);
			}
		} else {
			return false;
		}
		
	}
	// --- STATMENT END ---

	// --- STRUCT ---
	public boolean struct_expr() throws Exception {
		say("Calling struct_expr:");
		match();
		if ( struct_decl() ) {
			match();
			if ( cbrOpen() ) {
				topVarTable.structRegistration( tokens.get( currentTokenNumber-1 ).getValue() );
				topVarTable.setStructOn( tokens.get( currentTokenNumber-1 ).getValue() );
				if ( struct_body() ) {
					match();
					if ( cbrClose() ) {
						topVarTable.setStructOff();
						return true;
					} else { // !cbrClose
						throw new Exception("\n[!]Syntax error: curly close bracket expected.");
					}
				} else { // !struct_body
					throw new Exception("\n[!]Syntax error: struct body expected.");
				}
			} else { // !cbrOPen
				throw new Exception("\n[!]Syntax error: curly open bracket expected.");
			}
		} else { // !!struct_decl
			say("Struct not found.");
			currentTokenNumber--;
			return false;
		}
	}

	public boolean struct_decl() throws Exception {
		if ( struct_kw() ) {
			match();
			if ( var() ) {
				return true;
			} else {
				throw new Exception("\n[!]Syntax error: struct name expected. currentToken:" + currentToken);
			}
		} else {
			return false;
		}
	}

	public boolean struct_body() throws Exception {
		while( !cbrClose() ) {
			if( declare() || assign() ) {
				continue;
			}

			if ( cbrClose() ) {
				return true;
			} else {
				throw new Exception("\n[!]Syntax error: only declare or assign can be in struct body. currentToken:" + currentToken);
			}
		}
		//currentTokenNumber--;
		return true;
	}

	public boolean struct_kw() {
		return currentToken.getName().equals("STRUCT_KW");
	}

	public boolean struct_stmt() throws Exception {
		// int savedNumber = currentTokenNumber;
		if ( var() ) {
			match();
			if ( dot() ) {
				stmtAccum.add( tokens.get( currentTokenNumber-1 ) );
				stmtAccum.add( currentToken );
				match();
				if ( var() ) {
					return true;
				} else {
				throw new Exception("\n[!]Syntax error: var expected after struct name. currentToken:" + currentToken);
				}
			} else {
				currentTokenNumber -= 2;
				match();
				return false;
			}
		} else {
			return false;
		}
	}

	// --- STRUCT END---
	
	public boolean sm()  {
		return currentToken.getName().equals("SM");
	}

	public boolean separator() {
		return currentToken.getName().equals("SEP");
	}

	public boolean varKw(){
		return currentToken.getName().equals("VAR_KW");
	}

	public boolean function(){
		return currentToken.getName().equals("FUNCTION");
	}

	public boolean assignOp() {
		return currentToken.getName().equals("ASSIGN_OP");
	}
	
	public boolean plus() {
		return ( currentToken.getName().equals("PLUS_OP") );
	}

	public boolean dot() {
		return currentToken.getName().equals("DOT_OP");
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
				 currentToken.getName().equals("GRT_OP") ||
				 currentToken.getName().equals("LST_OP") );
	}

	public boolean brOpen() {
		return currentToken.getName().equals("BRK_O");
	}

	public boolean brClose() {
		return currentToken.getName().equals("BRK_C");
	}

	public boolean cbrOpen() {
		return currentToken.getName().equals("CBRK_O");
	}

	public boolean cbrClose() {
		return currentToken.getName().equals("CBRK_C");
	}

	private void say( String str ) {
		// System.out.println("Parser: " + str);
	}

	public void print( List<Token> ts ) {
		/*for(Token t : ts ) {
            System.out.print(t.getValue() + " ");
        }
		System.out.println("");*/
	}
}