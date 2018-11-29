import java.util.ArrayList;
import java.util.List;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceFieldRef;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.scalar.SimpleLocalDefs;


public class ManageDefUse {
	SimpleLocalDefs locdefs;
	
	
	public ManageDefUse(SimpleLocalDefs locDefs) {
		this.locdefs = locDefs;
	}
	
	public List get_def_list(Value use, Unit unit) {
		List def_list = new ArrayList();
		
		//if use is a variable
		if(use instanceof Local){
			//System.out.println(use);
			def_list = locdefs.getDefsOfAt((Local)use, unit);
			//System.out.println(def_list);
			return def_list;
		}
		
		if(use instanceof JimpleLocal){
			def_list = locdefs.getDefsOfAt((JimpleLocal)use, unit);
		}
		return def_list;
	}
}
