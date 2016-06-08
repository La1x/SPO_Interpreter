package laix.interpreter;

public class PostfixToken extends Token {
	private int opPriority;
	
	public PostfixToken(String name, String value){
		super (name,value);

		if (this.getName().equals("PLUS_OP") || this.getName().equals("MINUS_OP")) {
			this.opPriority = 2;
		}

		if (this.getName().equals("MULT_OP") || this.getName().equals("DEL_OP")) {
			this.opPriority = 1;
		} 

		if (this.getName().equals("BRK_O") || this.getName().equals("BRK_C")) {
			this.opPriority = -1;
		} else {
			opPriority = 4;
		}
	}

	public PostfixToken(Token token){
		super (token.getName(), token.getValue());

		if (this.getName().equals("PLUS_OP") || this.getName().equals("MINUS_OP")) {
			this.opPriority = 2;
		}

		if (this.getName().equals("MULT_OP") || this.getName().equals("DEL_OP")) {
			this.opPriority = 1;
		} 

		if (this.getName().equals("BRK_O") || this.getName().equals("BRK_C")) {
			this.opPriority = -1;
		} else {
			opPriority = 4;
		}
	}

	public int getOpPriority() {
		return opPriority;
	}

	public void setOpPriority(int i) {
		opPriority = i;
	}
}
