import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.jimple.internal.JGotoStmt;
import soot.toolkits.graph.UnitGraph;

public class ModifiedCFG extends UnitGraph {

	protected Map edgeToLabel;
	
	protected Unit methEntry;
	protected Unit methExit;
	
	Body m;
	
	public ModifiedCFG(Body body){
		
		super(body);
		m=body;
		int size = unitChain.size();
		
//		System.out.println(unitChain);
		//Map for Node(Unit) to its Successors 
		unitToSuccs = new HashMap(size * 2 + 1, 0.7f);
		unitToPreds = new HashMap(size * 2 + 1, 0.7f);
		
		/* Utility method for UnitGraph constructors.
		 * put values in unitToSuccs and unitToPreds*/
		buildUnexceptionalEdges(unitToSuccs, unitToPreds);
		
		/* Utility method used in the construction of UnitGraphs, 
		 * to be called only after the unitToPreds and unitToSuccs maps have been built.*/
		buildHeadsAndTails();
		
		Iterator uit = body.getUnits().iterator();
		
		while (uit.hasNext()) {
			Unit un = (Unit) uit.next();
			Util.v().log("succs_(no_entry_exit_node)",un+"--->"+getSuccsOf(un) );
			Util.v().log("preds_(no_entry_exit_node)",un+"--->"+getPredsOf(un) );
		}
		
		//adding entry and exit node in unitChain
		methEntry = new ExNopStmt("Entry");
		methExit  = new ExNopStmt("Exit");
		unitChain.addFirst(methEntry);
		unitChain.addLast(methExit);
		//System.out.println(unitChain);
		
		//
		unitToSuccs.put(methEntry, new ArrayList());
		unitToPreds.put(methEntry, new ArrayList());
		unitToSuccs.put(methExit, new ArrayList());
		unitToPreds.put(methExit, new ArrayList());

		/*putting edge from entry node to other nodes */
		Iterator headIt = getHeads().iterator();
		while (headIt.hasNext()) {
			addEdge(unitToSuccs, unitToPreds, methEntry, (Unit) headIt.next());
		}
		
		/*putting edge from exit node to other nodes */
		Iterator tailIt = getTails().iterator();
		while (tailIt.hasNext()) {
			addEdge(unitToSuccs, unitToPreds, (Unit) tailIt.next(), methExit);
		}
		
		buildHeadsAndTails();
		
		//dump this modified CFG
		soot.util.PhaseDumper.v().dumpGraph(this, body);
		
		Iterator uIt = body.getUnits().iterator();
		
		while (uIt.hasNext()) {
			Unit un = (Unit) uIt.next();
			Util.v().log("succs_(entry_exit_node)",un+"--->"+getSuccsOf(un) );
			Util.v().log("preds_(entry_exit_node)",un+"--->"+getPredsOf(un) );
		}
		
		//print_body(body);
	}

	public Unit getEntry() {
		return methEntry;
	}
	
	public Unit getExit() {
		return methExit;
	}
	
	public void add_dummy_edges() {
		Iterator i = body.getUnits().iterator();
		while (i.hasNext()) {
			Unit c_s = (Unit) i.next();
			if(c_s instanceof JGotoStmt){
				if(i.hasNext()){
					Unit n_s = (Unit) i.next();
					
					addEdge(unitToSuccs, unitToPreds, c_s, n_s);
					Util.v().log("goto",c_s+ "---->" + n_s);
				}
			}
		}
	}
	
	public void print_body(Body b) {
		System.out.println("\nPrinting Body of " + b.getMethod().getSignature());
		PatchingChain units = b.getUnits();
		Iterator it = units.iterator();
		int line_no = 1;
		while(it.hasNext()){
			Unit u = (Unit) it.next();
			
			System.out.println(line_no + " : " + u);
			
			line_no++;
		}
	}

	
}
