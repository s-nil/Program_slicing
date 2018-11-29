import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ArrayPackedSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.BoundedFlowSet;
import soot.toolkits.scalar.CollectionFlowUniverse;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.FlowUniverse;


public class BackDomAnalysis extends BackwardFlowAnalysis {
	
	FlowSet emptySet;
	Map nodeToGenerateSet;
	DirectedGraph graph;
	
	public BackDomAnalysis(DirectedGraph graph) {
		super(graph);
		
		this.graph = graph;
		List nodes = new ArrayList();
		
		for(Iterator nIt = graph.iterator(); nIt.hasNext();){
			nodes.add(nIt.next());
		}
		
		FlowUniverse nodeUniverse = new CollectionFlowUniverse(nodes);
		
		emptySet = new ArrayPackedSet(nodeUniverse);
		
		nodeToGenerateSet = new HashMap(graph.size()*2+1,0.7f);
		
		for (Iterator nIt = graph.iterator(); nIt.hasNext();) {
			Object o = nIt.next();
			
			FlowSet genSet = (FlowSet) emptySet.clone();
			genSet.add(o, genSet);
			nodeToGenerateSet.put(o, genSet);
			
		}
		doAnalysis();
	}

	@Override
	protected void flowThrough(Object in, Object b, Object out) {
		//domAnalysis.flowThrough(in, b, out);
		FlowSet inV = (FlowSet)in, outV = (FlowSet)out;
		
		inV.union((FlowSet)nodeToGenerateSet.get(b), outV);
	}

	@Override
	protected void copy(Object source, Object dest) {
		//domAnalysis.copy(source, dest);
		FlowSet sourceSet = (FlowSet) source, destSet = (FlowSet) dest;
		
		sourceSet.copy(destSet);
	}

	@Override
	protected Object entryInitialFlow() {
		//return domAnalysis.entryInitialFlow();
		List entries = graph.getTails();
		
		if (entries.size() != 1) {
			throw new RuntimeException("Assertion failed: Only one tail node expected.");
		}
		BoundedFlowSet initSet = (BoundedFlowSet) emptySet.clone();
		initSet.add(entries.get(0));
		return initSet;
	}

	@Override
	protected void merge(Object in1, Object in2, Object out) {
		//domAnalysis.merge(in1, in2, out);
		FlowSet inSet1 = (FlowSet) in1, inSet2 = (FlowSet)in2;
		FlowSet outSet = (FlowSet)out;
		
		inSet1.intersection(inSet2, outSet);
	}

	@Override
	protected Object newInitialFlow() {
		//return domAnalysis.newInitialFlow();
		BoundedFlowSet initSet = (BoundedFlowSet) emptySet.clone();
		initSet.complement();
		return initSet;
	}

}
