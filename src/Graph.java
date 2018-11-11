import java.util.*;

import soot.util.HashMultiMap;
import soot.util.MultiMap;

public class Graph {
	
	MultiMap src_dest, dest_src;
	
	public Graph() {
		src_dest = new HashMultiMap();
		dest_src = new HashMultiMap();
	}
	
	public void add_edge(Object src, Object dest) {
		src_dest.put(src, dest);
		dest_src.put(dest, src);
	}
	
	public Set getParents(Object node) {
		return dest_src.get(node);
	}
	
	public Set getChildren(Object node) {
		return src_dest.get(node);
	}
	
	public Set getAllParents() {
		return src_dest.keySet();
	}
	
	public Set getAllChildren() {
		return dest_src.keySet();
	}
	
	public void print() {
		Iterator itr = src_dest.keySet().iterator();
		String output = new String();
		while (itr.hasNext()) {
			Object src = itr.next();
			System.out.println("{" + src + "->" + getChildren(src) + "}");
		}
	}
}
