import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.toolkits.graph.UnitGraph;


public class MDG {

	public MDG(SootMethod sm) {
		//System.out.println("\nMDG");
		
		JimpleBody jb = (JimpleBody)sm.getActiveBody();
		
		// CFG with single entry and single exit
		UnitGraph graph = new ModifiedCFG(jb);
		
		//Data dependence graph
		DDG ddg = new DDG(graph);
		//ddg.print();
		
		
		((ModifiedCFG)graph).add_dummy_edges();
		
		//Control dependence graph
		CDG cdg = new CDG(graph);
		
		//cdg.print();
		
	}
}
