/**
 * Pair for holding sum counts and mul counts.
 * *Not currently used in Elimination.
 * @author Aviv
 *
 */
public class Pair {
	
	
	float rate;
	int mulcount;
	int sumcount;
	
	Pair(int a) {
		this.mulcount = a;
		this.rate = 0;
		this.sumcount = 0;
	}
	
	Pair(float a, int b) {
		this.rate = a;
		this.mulcount = b;
		this.sumcount = 0;
	}
	Pair(float a, int b, int c) {
		this.rate = a;
		this.mulcount = b;
		this.sumcount = c;
	}
	
	float getRate() {
		return rate;
	}
	
	int getsumcount() {
		return sumcount;
	}
	
	int getmulcount() {
		return mulcount;
	}
	
	
	

}
