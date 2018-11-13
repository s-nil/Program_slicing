import java.util.HashMap;
import java.util.Hashtable;

import com.sun.org.apache.bcel.internal.generic.NEW;


public class StoreGraph {
	private static StoreGraph inst = null;
	
	Hashtable method_entry = new Hashtable();
	
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
}
