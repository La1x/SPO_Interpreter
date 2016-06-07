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

	public void put(String s, Integer i) {
		table.put(s, i);
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