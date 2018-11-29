import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import soot.Body;
import soot.G;
import soot.Local;
import soot.PatchingChain;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.JimpleBody;


public class preProcess {
	private static preProcess inst = null;
	
    List formal_in_assign_list = new ArrayList();
	
	List formal_out_assign_list = new ArrayList();
	
	Unit nop_stmt_entry, nop_stmt_exit;
	
	private preProcess() {
		
	}
	
	public static preProcess v() {
		inst = new preProcess();		
		return inst;		
	}
	
	public void process(SootClass sc) {
		Iterator mItr = sc.methodIterator();
		while (mItr.hasNext()) {
			SootMethod sm = (SootMethod) mItr.next();
			
			if(!sm.isConcrete())
				return;
			
			methodPreprocessor(sm);
		}
	}
	
	private void methodPreprocessor(SootMethod sm) {
		Body b = sm.getActiveBody();
		
		if(!(b instanceof JimpleBody)){
			throw new RuntimeException("Error: Expecting a jimple Body");
		}
		
		if(sm.getName().equals("<init>"))
			return;
		//System.out.println(b);
		
		JimpleBody jb = (JimpleBody)b;
		
		for (int i = 0; i < sm.getParameterCount(); i++) {
			Local loc = jb.getParameterLocal(i);
			Unit form_in = new formalInAssignment(loc, loc);
			formal_in_assign_list.add(form_in);
			
		}
		
		Unit non_id = jb.getFirstNonIdentityStmt();
		
		
		PatchingChain units = jb.getUnits();
		
		Iterator<Unit> unitsIt = units.snapshotIterator();
		
		units.insertBefore(formal_in_assign_list, non_id);
		
		//System.out.println(units);
		
		/*while (unitsIt.hasNext()) {
			Stmt unit = (Stmt) unitsIt.next();
			//System.out.println(unit.toString());
			if (unit.containsInvokeExpr()) {
				//TODO
			}
			
			//if (unit instanceof JReturnStmt) {
			if (unit instanceof JReturnVoidStmt) {
				//TODO
				//System.out.println(unit.toString());
			}
		}*/
		
		sm.setActiveBody(jb);
		print_body(sm.getActiveBody());
	}

	public void print_body(Body b) {
		System.out.println(b);
		System.out.println("\nPrinting Body of " + b.getMethod().getSignature() + "\n");
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
