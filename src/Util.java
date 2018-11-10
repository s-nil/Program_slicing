import java.io.BufferedReader;
import java.io.FileReader;


public class Util {
	
	private static Util o = null;
	public String sc_className, sc_methodName;
	public int sc_line;
	
	private Util() {
		
	}
	
	public static Util v() {
		if (o == null) {
			o = new Util();
		}
		return o;		
	}
	
	public void read_sc() {
		String filename = "sc.txt";
		BufferedReader out;
		
		try {
			out = new BufferedReader(new FileReader(filename));
			if(out == null){
				System.out.println("sc.txt could not be opened.\n");
			}
			sc_className = out.readLine();
			sc_methodName = out.readLine();
			sc_line =  Integer.parseInt(out.readLine());
			System.out.println("slicing criteria: " + sc_className + ": " + sc_methodName + ": " + sc_line);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
