import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;


import soot.Body;
import soot.PatchingChain;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.JasminClass;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.options.Options;
import soot.util.Chain;
import soot.util.JasminOutputStream;


public class reconstructCFG {

	public reconstructCFG(SootClass sc) {
		Iterator mIt = sc.methodIterator();
		while (mIt.hasNext()) {
			SootMethod sm = (SootMethod) mIt.next();
			if (!sm.isConcrete()) {
				continue;
			}
			
			if(sm.getName().equals(Util.v().sc_methodName)){
				removeStmts(sm);
			}
		}
		//createClass(sc);
		
	}

	public void createClass(SootClass sc) {
		try {
			//Provides utility methods to retrieve an input stream for a class name, given a classfile, or jimple or baf output files.
			String filename = SourceLocator.v().getFileNameFor(sc, Options.output_format_class);
			OutputStream sout = new JasminOutputStream(new FileOutputStream(filename));
			PrintWriter wout = new PrintWriter(new OutputStreamWriter(sout));
			
			JasminClass jClass = new soot.jimple.JasminClass(sc);
			jClass.print(wout);
			wout.flush();
			sout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeStmts(SootMethod sm) {
		JimpleBody jb = (JimpleBody)sm.getActiveBody();
		//System.out.println(jb.getUnits());
		Chain unitsChain = new PatchingChain(jb.getUnits());
		
		Iterator iter = unitsChain.iterator();
		
		Iterator uIter = jb.getUnits().iterator();
		while (uIter.hasNext()) {
			Unit un = (Unit) uIter.next();
			if(((un instanceof JAssignStmt) || 
					(un instanceof JInvokeStmt)) && 
					!StoreGraph.v().units_in_slice.contains(un)){
				uIter.remove();
			}
		}
		
		//System.out.println(jb.getUnits());
		print_body(jb);
	}
	
	public void print_body(Body b) {
		System.out.println("\nPrinting Body of " + b.getMethod().getSignature() + " after slicing.");
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
