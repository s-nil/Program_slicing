import java.util.Iterator;
import java.util.List;

import soot.G;
import soot.Unit;
import soot.toolkits.graph.DominanceFrontier;
import soot.toolkits.graph.DominatorNode;
import soot.toolkits.graph.DominatorTree;
import soot.toolkits.graph.DominatorsFinder;
import soot.toolkits.graph.UnitGraph;


public class CDG {
	
	public CDG(UnitGraph u) {
		
		DominatorsFinder domFinder = new DomFinder(u);
		DominatorTree domTree = new DominatorTree(domFinder);
		
		//printDT(domTree.getHead());
		
		DominanceFrontier domFrontier = new domFront(domTree,false);
		
		//printDF(domTree.getHead(), domFrontier);
		
		Iterator domIt = domTree.iterator();
		while (domIt.hasNext()) {
			DominatorNode dnode = (DominatorNode) domIt.next();
			Unit unit = (Unit) dnode.getGode();
			List domF = domFrontier.getDominanceFrontierOf(dnode);
			
			if (domF == null || domF.size() == 0) {
				
				ModifiedCFG mcfg = (ModifiedCFG) u;
				Unit m_e = (Unit) mcfg.getEntry();
				
				StoreGraph.v().add_edge(m_e, unit, StoreGraph.CONTROL);
				Util.v().log("controlEdge", "CONTROL\t" + m_e + "---->" + unit);
				
				continue;
			}
			
			Iterator parIt = domF.iterator(); //parent iterator
			while (parIt.hasNext()) {
				DominatorNode par = (DominatorNode) parIt.next();
				Unit parUnit = (Unit) par.getGode();
				
				List succs = u.getSuccsOf(parUnit);
				if (succs == null || succs.size() == 0) {
					System.out.println("No succs for "+parUnit+"!!!");
					continue;
				}
				
				Iterator succIt = succs.iterator();
				while (succIt.hasNext()) {
					Unit succ = (Unit) succIt.next();
					if(succ == unit || domTree.isImmediateDominatorOf(dnode, domTree.getDode(succ))){
						StoreGraph.v().add_edge(parUnit, unit, StoreGraph.CONTROL);
						Util.v().log("controledge", "CTRL\t"+parUnit+"\t"+unit);
					}
				}
			}
		}
	}
	
	private void add_dep(Unit parent,List children) {
		Iterator i = children.iterator();
		
		while (i.hasNext()) {
			Unit child = (Unit) i.next();
			
			if (child != null) {
				StoreGraph.v().add_edge(parent,child, StoreGraph.CONTROL);
			}
		}
	}
	
	public void print() {
		System.out.println("\nCDG\n");
		StoreGraph.v().print(StoreGraph.CONTROL);
	}
	
	public void printDT(DominatorNode dn){
		Util.v().log("Dominator_Tree", dn+"---->"+dn.getChildren());
		Iterator dti = dn.getChildren().iterator();
		
		while (dti.hasNext()) {
			DominatorNode o = (DominatorNode) dti.next();
			printDT(o);		
		}
	}
	
	public void printDF(DominatorNode dn,DominanceFrontier df){
		Util.v().log("Dominator_Node", dn+"----->"+df.getDominanceFrontierOf(dn));
		Iterator dti = dn.getChildren().iterator();
		
		while (dti.hasNext()) {
			DominatorNode o = (DominatorNode) dti.next();
			printDF(o,df);		
		}
	}
}
