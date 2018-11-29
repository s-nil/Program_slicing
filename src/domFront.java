import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.toolkits.graph.DominanceFrontier;
import soot.toolkits.graph.DominatorNode;
import soot.toolkits.graph.DominatorTree;


public class domFront implements DominanceFrontier {

	private boolean dir;
	protected DominatorTree domTree;
	protected Map nodeToFrontier;
	
	public domFront(DominatorTree domTree, boolean dir) {
		this.dir = dir;
		this.domTree = domTree;
		nodeToFrontier = new HashMap();
		bottomUpDispatch(domTree.getHead());
	}
	
	@Override
	public List getDominanceFrontierOf(DominatorNode domNode) {
		ArrayList frontier = (ArrayList) nodeToFrontier.get(domNode);
		if(frontier == null)
			throw new RuntimeException("Frontier not defined for the node "+ domNode);
		
		return (List) frontier.clone();
	}
		
	public boolean isFrontierKnown(DominatorNode domNode) {
		return nodeToFrontier.containsKey(domNode);
	}
	
	protected void bottomUpDispatch(DominatorNode domNode) {
		if (isFrontierKnown(domNode)) {
			return;
		}
		
		//System.out.println(domNode);
		
		Iterator childrenIt = domTree.getChildrenOf(domNode).iterator();
		
		while (childrenIt.hasNext()) {
			DominatorNode child = (DominatorNode) childrenIt.next();
			
			if(!isFrontierKnown(child))
				bottomUpDispatch(child);
		}
		//System.out.println(domNode);
		processNode(domNode);
	}

	private void processNode(DominatorNode domNode) {
		//System.out.println("--------------start---------------");
		//System.out.println(domNode);
		List dominanceFrontier = new ArrayList();
		
		Iterator sIt = dir ?
				domTree.getSuccsOf(domNode).iterator() : 
					domTree.getPredsOf(domNode).iterator();
				
		while (sIt.hasNext()) {
			DominatorNode o1 = (DominatorNode) sIt.next();
			
			//System.out.println(o1);
			if(!domTree.isImmediateDominatorOf(domNode, o1)){
				dominanceFrontier.add(o1);
			}
		}
		
		Iterator cIt = domTree.getChildrenOf(domNode).iterator();
		
		while (cIt.hasNext()) {
			DominatorNode o2 = (DominatorNode) cIt.next();
			
			Iterator cFIt = getDominanceFrontierOf(o2).iterator();
			while (cFIt.hasNext()) {
				DominatorNode o3 = (DominatorNode) cFIt.next();
				
				if(!domTree.isImmediateDominatorOf(domNode, o3))
					dominanceFrontier.add(o3);
			}
		}
		nodeToFrontier.put(domNode, dominanceFrontier);
		
		//System.out.println("-----------end------------");
	}
}
