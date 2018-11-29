import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.DominatorsFinder;
import soot.toolkits.scalar.FlowAnalysis;
import soot.toolkits.scalar.FlowSet;


public class DomFinder implements DominatorsFinder{

	protected DirectedGraph graph;
	protected Map nodeToDominators; // 
	
	public DomFinder(DirectedGraph graph) {
		this.graph = graph;
		
		
		FlowAnalysis analysis = (FlowAnalysis) new BackDomAnalysis(graph);
		
		
		nodeToDominators = new HashMap(graph.size() * 2 + 1,0.7f);
		
		for (Iterator nodeIt = graph.iterator(); nodeIt.hasNext();) {
			Object node = nodeIt.next();

			FlowSet set = (FlowSet) analysis.getFlowBefore(node);
			
			nodeToDominators.put(node, set);
			
		}
/*
		for (Iterator it = nodeToDominators.keySet().iterator(); it.hasNext();) {
			Object o = it.next();
			System.out.println(o);
			System.out.println("[-"+nodeToDominators.get(o)+"-]");
			System.out.println("--------------------------------------------");
		}
*/
	}
	
	@Override
	public List getDominators(Object node) {
		return ((FlowSet) nodeToDominators.get(node)).toList();
	}

	@Override
	public DirectedGraph getGraph() {
		return graph;
	}

	@Override
	public Object getImmediateDominator(Object node) {
		List l = getGraph().getTails();
		
		if(l.contains(node))
			return null;
		
		List domList = getDominators(node);
		domList.remove(node);
		
		Iterator domItr = domList.iterator();
		Object immediateDom = null;
		
		while ((immediateDom == null) && (domItr.hasNext())) {
			Object dom = domItr.next();
			
			if(isDominatedByAll(dom, domList))
				immediateDom = dom;
		}
		
		if(immediateDom == null)
			throw new RuntimeException("Assertion failed");
		
		return immediateDom;
	}

	@Override
	public boolean isDominatedBy(Object node, Object dominator) {
		return getDominators(node).contains(dominator);
	}

	@Override
	public boolean isDominatedByAll(Object node, Collection dominators) {
		return getDominators(node).containsAll(dominators);
	}

}
