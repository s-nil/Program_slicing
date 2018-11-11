import java.util.Iterator;

import soot.G;
import soot.SootClass;
import soot.SootMethod;


public class classPreprocessor {
	private static classPreprocessor inst = null;
	
	private classPreprocessor() {
		
	}
	
	public static classPreprocessor v() {
		inst = new classPreprocessor();		
		return inst;		
	}
	
	public void preprocess(Iterator<SootClass> classesItr) {
		while (classesItr.hasNext()) {
			Iterator<SootMethod> mItr = ((SootClass) classesItr.next()).methodIterator();
			
			while (mItr.hasNext()) {
				SootMethod m = (SootMethod) mItr.next();
				if(!m.isConcrete())
					continue;
				
				new methodPreprocessor(m);
			}
		}
	}
}
