import java.util.Comparator;
import java.util.Hashtable;
import java.util.Set;
/**
 * Represents Factor in Bayesian Network.
 * @author Aviv
 *
 */
public class Factor {
	String name;
	Hashtable<String, Float> factor = new Hashtable<String, Float>();
	Pair result;
	
	Factor() {
		this.name = "";
		this.factor = new Hashtable<String,Float>();
		this.result = new Pair(1,2);
	}
	
	Factor(String name, Hashtable<String, Float> hash, Pair p) {
		this.name = name;
		this.factor = hash;
		this.result  = p;
	}
	Factor(Node factor, String name) {
		this.name = name;
		Hashtable<String,Float> hash = new Hashtable<String,Float>();
		Set<String> keys = factor.CPT.keySet();
		for(String key : keys) {
			if(!key.contains(factor.name)) {
				hash.put(factor.getName()+"="+key, factor.CPT.get(key));
			}
		}
		if(!hash.isEmpty()) {
		 this.factor = hash;
		}
		else 
			this.factor = factor.CPT;
		this.result = new Pair(0,0);
	}
	Factor(Hashtable<String, Float> hash1, Hashtable<String, Float> hash2, String name) {
		
	}
	
	Factor(String name , Hashtable<String,Float> hash) {
		this.name = name;
		this.factor = hash;
	}
	
	public Hashtable<String,Float> getHash() {
		return this.factor;
	}
	
	 public static Comparator<Factor> Sort = new Comparator<Factor>() {

			public int compare(Factor s1, Factor s2) {
			  int hash_size1 = s1.getHash().size();
			  int hash_size2 = s2.getHash().size();

			   return hash_size1-hash_size2;

		    }
	};

}
