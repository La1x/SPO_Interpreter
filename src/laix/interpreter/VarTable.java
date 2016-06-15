package laix.interpreter;
import java.util.*;
import java.lang.Integer;

public class VarTable {
	private HashMap<String,Integer> table;
	private HashMap<String,HashMap<String,Integer>> structTable;
	private VarTable prevTable;

	private String structName;
	private boolean isStruct;

	public VarTable(VarTable p) {
		table = new HashMap<String,Integer>();
		structTable = new HashMap<String, HashMap<String, Integer>>();
		prevTable = p;

		structName = null;
		isStruct = false;
	}

	public void put(String s, Integer i) throws Exception {
		String found = null;
		if ( isStruct == false ) {
			for ( VarTable t = this; t != null; t = t.prevTable ) {
				for ( String name : t.table.keySet() ) {
					if ( s.equals(name) ) {
						found = name;
						break;
					}
				}
				if ( found != null ) {
					break;
				}
			}

			if ( found != null ) {
				table.put(found, i);
			} else {
				throw new Exception("\n[!]Syntax error: variable " + s + " must be declared.");
			}
		} else {
			HashMap<String,Integer> foundStruct = null;
			for ( VarTable t = this; t != null; t = t.prevTable ) {
				for ( String name : t.structTable.keySet() ) {
					if ( structName.equals(name) ) {
						foundStruct = t.structTable.get(name);
						break;
					}
				}
				if ( foundStruct != null ) {
					break;
				}
			}

			if ( foundStruct != null ) {
				foundStruct.put(s, i);
				structTable.put(structName, foundStruct);
			} else {
				throw new Exception("\n[!]Syntax error: struct " + structName + " must be declared.");
			}
		}		
	}

	public Integer get(String s) {
		if ( isStruct == false ) {
			for ( VarTable t = this; t != null; t = t.prevTable ) {
				Integer found = t.table.get(s);
				if ( found != null ) {
					return found;
				}
			}
		} else {
			for ( VarTable t = this; t != null; t = t.prevTable ) {
				HashMap<String,Integer> foundStruct = t.structTable.get(structName);
				if ( foundStruct != null ) {
					Integer found = foundStruct.get(s);
					if ( found != null ) {
						return found;
					}
				}
			}
		}
		return null;
	}

	public void varRegistration(String s, Integer i) throws Exception {
		if ( isStruct == false ) {
			for ( String name : table.keySet() ) {
				if ( s.equals(name) ) {
					throw new Exception("\n[!]Syntax error: declaration in body cycle or variable " + s + " already declared in this block.");
				}
			}
			table.put(s,i);
		} else {
			HashMap<String,Integer> foundStruct = structTable.get(structName);
			if ( foundStruct == null ) {
				throw new Exception("\n[!]Syntax error: struct " + structName + "must be declared.");
			}
			for ( String name : foundStruct.keySet() ) {
				if ( structName.equals(name) ) {
					throw new Exception("\n[!]Syntax error: variable " + s + " already declared in struct " + structName);
				}
			}
			foundStruct.put(s,i);
			structTable.put(structName,foundStruct);
		}
		
	}

	public void structRegistration(String s) throws Exception {
		HashMap<String,Integer> foundStruct = structTable.get(structName);
		if ( foundStruct != null ) {
			throw new Exception("\n[!]Syntax error: struct " + structName + "already declared.");
		}
		foundStruct = new HashMap<String,Integer> ();
		structTable.put(s, foundStruct);
	}

	/*public void deleteCurrent() throws Exception {
		if ( prevTable == null ) {
			throw new Exception("\n[!]Table error: cant delete global var table.");
		}
	 	table = prevTable.table;
	 	prevTable = prevTable.prevTable;
	}*/

	public void setStructOn( String s ) {
		structName = s;
		isStruct = true;
	}

	public void setStructOff() {
		structName = null;
		isStruct = false;
	}

	public void print() {
		System.out.println("\t\t\tVar Table:");
		for ( String name : table.keySet() ) {
            String key = name;
            String value = table.get(name).toString();  
            System.out.println("\t\t\t " + key + "\t" + value); 
		}

		for ( String sname : structTable.keySet() ) {
			String key = sname;
			HashMap<String,Integer> strvl = structTable.get(sname);
			System.out.println("\t\t\t > struct " + sname + ":");
			for ( String name2 : strvl.keySet() ) {
            	String key2 = name2;
            	String value2 = strvl.get(name2).toString();  
            	System.out.println("\t\t\t    " + key2 + "\t" + value2); 
			}
		}

		if (prevTable != null) {
			System.out.println("\t\t\t  ->");
			prevTable.print();
		}
	}
}