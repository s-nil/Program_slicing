import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;


public class DDG {

	ManageDefUse du_man;
	
	ArrayList unit_list;
	
	public DDG(UnitGraph graph) {
		unit_list = new ArrayList();
		
		Body body = graph.getBody();
		
		SimpleLocalDefs localDefs = new SimpleLocalDefs(graph);
		
		du_man = new ManageDefUse(localDefs);
		
		Iterator unitsItr = body.getUnits().iterator();
		process_units(unitsItr);
	}

	private void process_units(Iterator unitsItr) {
		while (unitsItr.hasNext()) {
			Stmt unit = (Stmt) unitsItr.next();
			
			unit_list.add(unit);
			
			if (unit.containsInvokeExpr()) {
				
			}
			if (unit instanceof AssignStmt) {
				process_assign_stmt(unit);
			}
		}
	}

	private void process_assign_stmt(Unit unit) {
		Iterator useboxIt = unit.getUseBoxes().iterator();
		Iterator defboxIt = unit.getDefBoxes().iterator();
		
		while (useboxIt.hasNext()) {
			Value use_val = ((ValueBox) useboxIt.next()).getValue();
			
			List def_List = du_man.get_def_list(use_val, unit);
			
			Iterator it = def_List.iterator();
			
			while (it.hasNext()) {
				Unit def_unit = (Unit) it.next();
				
				//add dependency
				add_dep(unit,def_unit,StoreGraph.DATA);
				
				Util.v().log("dataedge", "DATA\t" + def_unit + "\t" + unit + " ");
			}
		}
	}

	private void add_dep(Unit unit, Unit defUnit, int type) {
		StoreGraph.v().add_edge(defUnit, unit, type);
	}
	
	public void print() {
		StoreGraph.v().print(StoreGraph.DATA);
	}
}
