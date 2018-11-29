import soot.jimple.internal.JNopStmt;


public class ExNopStmt extends JNopStmt{
	String nameStr;
	
	public ExNopStmt(String str) {
		this.nameStr = str;
	}
	public String toString() {
		return "NOP."+this.nameStr;
	}
}
