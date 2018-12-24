import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set; 
/**
 * Node represents single node in Network.
 * @author Aviv
 *
 */
public class Node {

	String name;
	ArrayList<String> Values;
	ArrayList<Node> NodeParents;
	Hashtable<String, Float> CPT = new Hashtable<String, Float>();
	
	
	Node(String name, ArrayList<String> Value, ArrayList<Node> Parents, ArrayList<String> CPT) {
		this.name = name;
		this.Values = Value;
		this.NodeParents = Parents;
	}
	
	Node(String name) {
		this.name = name;
		this.Values = new ArrayList<>();
		this.NodeParents = new ArrayList<>();
	}
	
	String getName( ) {
		return this.name;
	}
	
	public void SetParent(Node p) {
		this.NodeParents.add(p);
	}
	
	public void AddCPT(String p) {
		if(this.NodeParents.size() < 1) { // No parents 
			String[] temp = p.split(",");
			float remainder = 1;
			for(int j = 0; j<temp.length; j=j+2) {
				if(temp[j].startsWith("=")) {
				   CPT.put(temp[j].replace("=", ""), Float.parseFloat(temp[j+1]));
				}	
				remainder -= Float.parseFloat(temp[j+1]);
			}
			
			for(int i = 0; i<this.Values.size(); i++) {
				if(!CPT.containsKey(this.Values.get(i))) {
					CPT.put(this.Values.get(i), remainder);
				}
			}
		}
		else { // Parents > 0
			int parent_number = this.NodeParents.size();
			String[] temp = p.split(",");
			String t = "";
			for(int i = 0; i<parent_number; i++) {
				t += this.NodeParents.get(i).getName()+"="+temp[i]+",";
			}
			
			float remainder = 1;
			for(int i = parent_number; i< temp.length; i=i+2) {
				if(temp[i].startsWith("=")) {
					   CPT.put(t+this.getName()+temp[i], Float.parseFloat(temp[i+1]));
					}	
					remainder -= Float.parseFloat(temp[i+1]);
			}
			for(int i = 0; i<this.Values.size(); i++) {
				String mykey = t+this.getName()+"="+this.Values.get(i);
				if(!CPT.containsKey(mykey)) {
					CPT.put(mykey, remainder);
				}
			}
			
		}
		
	}
	public Hashtable<String,Float> FixHash(Node fac, String name) {
		Hashtable<String,Float> new_hash = new Hashtable<String,Float>();
		Set<String> keys = fac.CPT.keySet();
		for(String key : keys) {
			new_hash.put(name+"="+key, fac.CPT.get(key));
		}
		return new_hash;
		
	}
	public void AddValue(String p) {
		this.Values.add(p);
	}
	
	public ArrayList<String> getValues() {
		return this.Values;
	}
	String ShowNode() {
		String ss=  "Node Name: "+this.name+", Values: ";
		for(int i =0; i<this.Values.size(); i++) {
			ss += this.Values.get(i)+", ";
		}
		ss += "Node Parents: ";
		for(int i =0; i<this.NodeParents.size(); i++) {
			ss += this.NodeParents.get(i).getName()+",";
		}
		ss += " CPTs: ";
		ss += ShowHash();
		return ss;
	}
	String ShowHash() {
		return CPT.toString();
	}
	public static Comparator<Node> Sort = new Comparator<Node>() {

		public int compare(Node s1, Node s2) {
		  int hash_size1 = s1.CPT.size();
		  int hash_size2 = s2.CPT.size();

		   return hash_size1-hash_size2;

	    }
};
}
