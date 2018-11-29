import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import soot.Unit;

import com.sun.org.apache.bcel.internal.generic.NEW;


public class StoreGraph {
	private static StoreGraph inst = null;
	
	//Hashtable method_entry = new Hashtable();
	Set units_in_slice = new HashSet();
	
	Graph data_dep_edges = new Graph();
	Graph con_dep_edges = new Graph();
	
	public static int DATA = 1, CONTROL = 2;
	
	private StoreGraph() {
		// TODO Auto-generated constructor stub
	}
	
	protected static StoreGraph v() {
		if(inst == null){
			inst = new StoreGraph();
		}
		return inst;
	}
	
	public void add_edge(Object src, Object dest, int type) {
		if (type == DATA) {
			data_dep_edges.add_edge(src, dest);
		}else if (type == CONTROL) {
			con_dep_edges.add_edge(src, dest);
		}
	}
	
	public void print(int type) {
		if (type == DATA) {
			data_dep_edges.print();
		}else if (type == CONTROL) {
			con_dep_edges.print();
		}
	}

	public void slice(Unit unit) {
		slice1(unit);
		Set units_fp = new HashSet(units_in_slice);
		
		Iterator it = units_fp.iterator();
		while (it.hasNext()) {
			Unit u = (Unit) it.next();
			slice2(u);
		}
	}
	
	public void slice2(Unit u) {
		units_in_slice.add(u);
		
		Set parSet = new HashSet();
		
		parSet.addAll(con_dep_edges.getParents(u));
		parSet.addAll(data_dep_edges.getParents(u));
		
		Iterator it = parSet.iterator();
		
		while (it.hasNext()) {
			Unit unit = (Unit) it.next();
			if(!units_in_slice.contains(unit)){
				slice2(unit);
			}
		}
	}

	public void slice1(Unit unit){
		units_in_slice.add(unit);
		
		Set pSet = new HashSet();
		
		pSet.addAll(con_dep_edges.getParents(unit));
		pSet.addAll(data_dep_edges.getParents(unit));
		
		Iterator it = pSet.iterator();
		
		while (it.hasNext()) {
			Unit u = (Unit) it.next();
			if(!units_in_slice.contains(u)){
				slice1(u);
			}
		}
	}
	
}
