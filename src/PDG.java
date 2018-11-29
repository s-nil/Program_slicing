import java.util.Iterator;

import soot.Body;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.JimpleBody;


public class PDG {

	public PDG(SootClass sc) {
		Iterator mIt = sc.methodIterator();
		while (mIt.hasNext()) {
			SootMethod o = (SootMethod) mIt.next();
			
			if (o.getName().equals(Util.v().sc_methodName)) {
				build_PDG(o);
				slice(o,Util.v().sc_line);
			}
		}
		
	}

	private void build_PDG(SootMethod sM) {
		//Returns true if this method is not phantom, abstract or native
		if(!sM.isConcrete())
			return;
			
		System.out.println("\n\nMethod: " + sM.getName());
		//print_body(sM.getActiveBody());
		MDG mdg = new MDG(sM);
	}
	
	private void slice(SootMethod m, int scLine) {
		JimpleBody jb = (JimpleBody) m.getActiveBody();
		Iterator unitsIt = jb.getUnits().iterator();
		int line_count = 0;
		while (unitsIt.hasNext()) {
			Unit unit = (Unit) unitsIt.next();
			if (line_count == scLine) {
				StoreGraph.v().slice(unit);
			}
			line_count++;
		}
	}

	
	void print_body(Body b) {
		System.out.print("\nPrinting Body of " + b.getMethod().getSignature()
				+ "\n");
		PatchingChain units = b.getUnits();
		Iterator it = units.iterator();
		int line_no = 1;
		while (it.hasNext()) {
			Unit u = (Unit) it.next();

			System.out.println(line_no + " :" + "  " + u);

			line_no++;
		}
	}

}
