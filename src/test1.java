import java_cup.internal_error;


public class test1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int a=10;
		int x,y;
		x = a + 10;
		x = x+1;
		y = x;
		if(x>10){
			x = y +1;
		}
		else {
			a = x;
		}
		a= new test1().name(a);
		System.out.println(a);
		
	}
	
	public int name(int a) {
		return a;
	}
	
}
