import java.util.Iterator;

import soot.Body;
import soot.PatchingChain;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;


public class PDG {

	public PDG(Iterator<SootClass> classItr) {
		while (classItr.hasNext()) {
			SootClass sc = (SootClass) classItr.next();
			build_PDG(sc);
		}
	}

	private void build_PDG(SootClass sc) {
		Iterator<SootMethod> mItr = sc.methodIterator();
		while (mItr.hasNext()) {
			SootMethod sM = (SootMethod) mItr.next();
			
			if(!sM.isConcrete())
				continue;
			
			System.out.println("\n\nMethod: " + sM.getName());
			//print_body(sM.getActiveBody());
			MDG mdg = new MDG(sM);
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
