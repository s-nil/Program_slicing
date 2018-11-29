import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.internal.JimpleLocalBox;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;


public class DDG {

	ManageDefUse du_man;
	
	ArrayList unit_list;
	
	public DDG(UnitGraph graph) {
		unit_list = new ArrayList();
		
		Body body = graph.getBody();
		
		//Analysis that provides an implementation of the LocalDefs interface.
		SimpleLocalDefs localDefs = new SimpleLocalDefs(graph);
		
		du_man = new ManageDefUse(localDefs);
		
		Iterator unitsItr = body.getUnits().iterator();
		process_units(unitsItr);
	}

	private void process_units(Iterator unitsItr) {
		try {
			while (unitsItr.hasNext()) {
				Stmt unit = (Stmt) unitsItr.next();
				unit_list.add(unit);
				
				if (unit.containsInvokeExpr()) {
					//throw new RuntimeException("Method contains calls to other functions.");
					try {
						handle_invoke_expr(unit);
					} catch (Exception e) {
						System.err.println("contains function call.");
					}
					
					//System.out.println(unit.getInvokeExpr());
				}
				if (unit instanceof AssignStmt) {
					process_assign_stmt(unit);
				}
			}
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void handle_invoke_expr(Stmt unit) {
		Iterator jlIt = unit.getInvokeExpr().getUseBoxes().iterator();
		
		while (jlIt.hasNext()) {
			ValueBox val = (ValueBox) jlIt.next();
			//System.out.println(val.getValue());
			if(val instanceof JimpleLocalBox){
				List defList = du_man.get_def_list(val.getValue(), unit);
				//System.out.println(defList);
				
				if (defList.size() == 0) {
					continue;
				}
				
				Iterator it = defList.iterator();
				while (it.hasNext()) {
					Unit def_unit = (Unit) it.next();
					add_dep(unit, def_unit, StoreGraph.DATA);
					Util.v().log("dataEdge", "DATA\t" + unit + "--->"+def_unit);
				}
			}
		}
		
		
		
		Iterator argIt = unit.getInvokeExpr().getArgs().iterator();
		while (argIt.hasNext()) {
			Value val = (Value) argIt.next();
			//System.out.println(val);
			List def_List = du_man.get_def_list(val, unit);
			
			if(def_List.size() == 0){
				continue;
			}
			
			Iterator it = def_List.iterator();
			while (it.hasNext()) {
				Unit def_unit = (Unit) it.next();
				add_dep(unit, def_unit, StoreGraph.DATA);
				Util.v().log("dataEdge", "DATA\t" + unit + "--->"+def_unit);
			}
		}
	}

	private void process_assign_stmt(Unit unit) {
		Iterator useboxIt = unit.getUseBoxes().iterator();
		Iterator defboxIt = unit.getDefBoxes().iterator();
		
		while (useboxIt.hasNext()) {
			Value use_val = ((ValueBox) useboxIt.next()).getValue();
			//System.out.println(use_val);
			List def_List = du_man.get_def_list(use_val, unit);
			
			if(def_List.size() == 0){
				continue;
			}
			
			Iterator it = def_List.iterator();
			
			while (it.hasNext()) {
				Unit def_unit = (Unit) it.next();
				
				//add dependency
				add_dep(unit,def_unit,StoreGraph.DATA);
				
				Util.v().log("dataEdge", "DATA\t" + unit + "---->" + def_unit);
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
