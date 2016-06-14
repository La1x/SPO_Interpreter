package laix.interpreter;
import java.util.*;
import java.lang.Integer;

public class VarTable {
	private HashMap<String,Integer> table;
	private VarTable prevTable;

	public VarTable(VarTable p) {
		table = new HashMap<String,Integer>();
		prevTable = p;
	}

	public void put(String s, Integer i) throws Exception {
		String found = null;
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
	}

	public Integer get(String s) {
		for ( VarTable t = this; t != null; t = t.prevTable ) {
			Integer found = t.table.get(s);
			if ( found != null ) {
				return found;
			}
		}
		return null;
	}

	public void varRegistration(String s, Integer i) throws Exception {
		for ( String name : table.keySet() ) {
			if ( s.equals(name) ) {
				throw new Exception("\n[!]Syntax error: variable " + s + " already declared in this block.");
			}
		}
		table.put(s,i);
	}

	public void deleteCurrent() throws Exception {
		if ( prevTable == null ) {
			throw new Exception("\n[!]Table error: cant delete global var table.");
		}
	 	table = prevTable.table;
	 	prevTable = prevTable.prevTable;
	}

	public void print() {
		System.out.println("\t\t\tVar Tables:");
		for ( String name : table.keySet() ) {
            String key = name;
            String value = table.get(name).toString();  
            System.out.println("\t\t\t " + key + "\t" + value); 
		}

		if (prevTable != null) {
			System.out.println("\t\t\t  ->");
			prevTable.print();
		}
	}
}