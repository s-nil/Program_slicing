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
		
		//System.out.println(size);
		
		unitToSuccs = new HashMap(size * 2 + 1, 0.7f);
		unitToPreds = new HashMap(size * 2 + 1, 0.7f);
		
		//Utility method for UnitGraph constructors.
		buildUnexceptionalEdges(unitToSuccs, unitToPreds);
		
		/* Utility method used in the construction of UnitGraphs, 
		 * to be called only after the unitToPreds and unitToSuccs maps have been built.*/
		buildHeadsAndTails();
		
		methEntry = new ExNopStmt();
		methExit  = new ExNopStmt();
		unitChain.addFirst(methEntry);
		unitChain.addLast(methExit);
		
		unitToSuccs.put(methEntry, new ArrayList());
		unitToPreds.put(methEntry, new ArrayList());
		unitToSuccs.put(methExit, new ArrayList());
		unitToPreds.put(methExit, new ArrayList());
	
		Iterator headIt = getHeads().iterator();
		while (headIt.hasNext()) {
			addEdge(unitToSuccs, unitToPreds, methEntry, (Unit) headIt.next());
		}
		
		Iterator tailIt = getTails().iterator();
		while (tailIt.hasNext()) {
			addEdge(unitToSuccs, unitToPreds, (Unit) tailIt.next(), methExit);
		}
		
		buildHeadsAndTails();
		
		/*Iterator itr = unitChain.iterator();
		while (itr.hasNext()) {
			Unit o = (Unit) itr.next();
			System.out.println(o);
			
		}*/
		
		soot.util.PhaseDumper.v().dumpGraph(this, body);
		//print(getHeads());
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
					Util.v().log("goto", c_s + " : " + n_s);
				}
			}
		}
	}

	/*public boolean abc(){
		Scanner in = new Scanner(System.in);
		String s = in.nextLine();
		System.out.println(s);
		
		return false;
	}
	
	public void print(List<Unit> lu){
		Iterator hit = lu.iterator();
		while (hit.hasNext()) {
			Unit o = (Unit) hit.next();
			System.out.print(o);
			System.out.println(" : " + getSuccsOf(o).size());
			print(getSuccsOf(o));
			abc();
		}
	}*/
}
