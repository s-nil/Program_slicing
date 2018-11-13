import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;
import soot.jimple.internal.JAssignStmt;


public class formalInAssignment extends JAssignStmt{

	public formalInAssignment(Value variable, Value rvalue) {
		super(variable, rvalue);
	}

	public formalInAssignment(ValueBox variableBox, ValueBox rvalueBox) {
		super(variableBox, rvalueBox);
	}
	
	public Object clone() {
		return new formalInAssignment(Jimple.cloneIfNecessary(this.getLeftOp()), Jimple.cloneIfNecessary(this.getRightOp()));
	}
	
	public String toString() {
		return "FI:"+super.toString();
		
	}
}
