import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Set;



/**
 * Variable Elimination ver.1
 * @author Aviv
 *
 */
public class VariableElimination {
	
	
	
	static ArrayList<Factor> myInitial = new ArrayList<>();
	static String query;
	static Hashtable<String,String> myQueryValues ;
    static ArrayList<Node> Network;
	String final_answer;
	static int mulcounter;
	static int sumcounter;
	
	
	VariableElimination() {}
	/**
	 * 
	 * @param query - Query wanted to compute.
	 * @param Network - ArrayList of Nodes which represents Node in the network.
	 * @param method - 0 means simple elimination, 1 means elimination by key size.
	 */
	VariableElimination(String query, ArrayList<Node> Network, int method) {
		/**
		 * Intalizing params
		 */
		mulcounter = 0;
		sumcounter = 0;

		this.query = query;
		boolean answer= false;
		String E_order = "";
		ArrayList<String> factors = new ArrayList<>();
		this.Network = Network;
		this.myQueryValues = ex1.getKnownValues(query);
		String asking = getAsking(query);
		factors = ex1.Split(query, Network, myQueryValues);
		
		
		if(method == 0 ) {
			E_order = ex1.getEliminationOrder(Network, myQueryValues, asking);
		}
		else {
			E_order = getHeuristicOrder();
		}
		
		/**
		 * Transfering to factors.
		 */
		for(int i = 0; i<factors.size(); i++) {
			Factor temp = transferToFactor(factors.get(i));
			if(temp != null)
			    myInitial.add(temp);
		}
		/**
		 * Running on Elimination order and start Join&Eliminate.
		 */
		for(int i =0; i<E_order.length(); i++) {
			ArrayList<Factor> InitialClone = (ArrayList<Factor>) myInitial.clone();
			Factor after_join;
			String p = Character.toString(E_order.charAt(i));
			ArrayList<Factor> myJoin = new ArrayList<>();
			for(int j =0; j<InitialClone.size(); j++) {
				if(InitialClone.get(j).name.contains(p)) {
					myJoin.add(InitialClone.get(j));
					myInitial.remove(InitialClone.get(j));
				}
			}
			myJoin.sort(Factor.Sort);
			myInitial.sort(Factor.Sort);
			if(myJoin.size() == 0) {
				continue;
			}
			after_join = Join(myJoin,p,Network); // Join phase.
			Factor after_eliminate = Eliminate(after_join, p); // Eliminate phase.
			if(Removebackround(after_eliminate.name).equals(getAskingName(asking)) && checkIfVaild(after_eliminate) && i != E_order.length()-1) {
				final_answer = getAnswer(after_eliminate, query);
				answer = true;
			}
			if(checkIfVaild(after_eliminate)  || i == E_order.length()-1)
			    myInitial.add(after_eliminate);

		}
		/**
		 * Computing phase.
		 */
		for(int i = 0; i<myInitial.size(); i++) {
			if(!checkIfVaild(myInitial.get(i))) {
				mulcounter+= myInitial.get(i).result.mulcount;
				sumcounter += myInitial.get(i).result.sumcount;
				myInitial.remove(myInitial.get(i));
			}
		}
		myInitial.sort(Factor.Sort);
		
		if(!answer) {
			if(myInitial.size() == 2) {
				if(!Removebackround(myInitial.get(1).name).contains(Removebackround(myInitial.get(0).name))) {
					Factor temp = JoinStrangers(myInitial.get(1),myInitial.get(0), asking);
					final_answer = getAnswer(temp, query);
				}
				else {
				    Factor last_join = Join(myInitial, getAskingName(getAsking(query)) , Network);
				    final_answer = getAnswer(last_join, query);
				}
			}
			
			else if(myInitial.size() == 1) {
				final_answer = getAnswer(myInitial.get(0), query);
			}
			
			else {
				 for(int u = 0; u<myInitial.size(); u++) {
					 if(!Removebackround(myInitial.get(u).name).contains(getAskingName(getAsking(query)))) {
						 myInitial.remove(myInitial.get(u));
					 }
				 }
				 
					Factor last_join = Join(myInitial, getAskingName(getAsking(query)) , Network);
					final_answer = getAnswerAfterJoin(last_join, query);
			}
		}
		myInitial.clear();
		sumcounter = 0;
		mulcounter = 0;


	}

	
/**
 * Get order by hash size.
 * @return
 */
	public static String getHeuristicOrder() {
		ArrayList<Node> Clone = (ArrayList<Node>) Network.clone();
		String order = "";
		Clone.sort(Sort);
		for(int i = 0; i<Clone.size(); i++) {
			if(myQueryValues.get(Clone.get(i).getName()) == null) {
				order += Clone.get(i).getName();
			}
		}
		return order;
	}
	public static Comparator<Node> Sort = new Comparator<Node>() {

		public int compare(Node s1, Node s2) {
		  int neighbors1 = s1.CPT.size();
		  int nehibors2 = s2.CPT.size();

		   return neighbors1-nehibors2;

	    }
	};
	/**
	 * Checking if factor is valid - CPT's = 1 is not.
	 * @param factor
	 * @return TRUE/FALSE
	 */
	public static boolean checkIfVaild(Factor factor) {
		Set<String> keys = factor.getHash().keySet();
		int size = factor.getHash().size();
		float one = 1;
		for(String key : keys) {
			if(factor.getHash().get(key).equals(one))
                size--;
		}
		if(size==0)
		    return false;
		else
			return true;
		
	}
	/**
	 * Getting asked name.
	 * @param asking
	 * @return Name of factor
	 */
	public static String getAskingName(String asking) {
		String[] tep = asking.split("=");
		return tep[0];
	}
	/**
	 * *Not used in this version
	 * Function checking if factor is holding the answer already.
	 * @param factor
	 * @return TRUE/FALSE
	 */
	public static boolean checkIfAnswerExists(Factor factor) {
		String factor_name = factor.name;
		int size= myQueryValues.size();
		int counter = 0;
		factor_name = Removebackround(factor_name);
		if(factor_name.contains(",")) {
			String[] temp = factor_name.split(",");
			for(int i = 0; i < temp.length; i++) {
			  if(myQueryValues.get(temp[i]) != null) 
				 counter++;
			}
		}
		if(size <= 1) 
			return false;
		if(counter == size) 
			return true;
		return false;
		
	}
	/**
	 * Dealing with some cases after elimination.
	 * @param last
	 * @param query
	 * @return String represents answer.
	 */
	public static String getAnswerAfterJoin(Factor last, String query) {
		String factor_name = Removebackround(last.name);
		//int mulcounter = 0;
		int index = ex1.FindIndexByName(Network, factor_name);
		Factor org = new Factor(Network.get(index), getAskingName(getAsking(query)));
		
		Hashtable<String,Float> hash = new Hashtable<String,Float>();
		
		Set<String> keys = org.getHash().keySet();
		Set<String> lastkeys = last.getHash().keySet();
		
		for(String key : keys) {
			for(String lkey : lastkeys) {
				if(lkey.contains(key)) {
					if(!hash.containsKey(lkey)) {
						hash.put(lkey, last.getHash().get(lkey) * org.getHash().get(key));
						mulcounter++;
					}
				}
			}
		}
		Pair p = last.result;
		//p.mulcount += mulcounter;
		Factor new_one = new Factor(last.name, hash, p);
		return getAnswer(new_one, query);
		
	}
	/**
	 * Dealing with last phase, get to answer.
	 * @param last - last factor
	 * @param query 
	 * @return String represnts result.
	 */
	public static String getAnswer(Factor last, String query) { 
		String factor_name = Removebackround(last.name);
		Set<String> keys = last.getHash().keySet();
		String asking = getAsking(query);
		asking += "="+myQueryValues.get(asking);
		float sum = 0;
		float rate = 0;
		
		for(String key : keys) {
		     if(key.contains(asking)) {
		    	 rate = last.getHash().get(key);
		     }
		     sum += last.getHash().get(key);
		}
		rate = rate/sum;
		return fixRate(rate)+","+sumcounter+","+mulcounter;
	}
	/**
	 * Fixing rate to be by instructions.
	 * @param rate
	 * @return rate
	 */
	public static String fixRate(float rate) {
		Double rate1 = (double)rate;
		rate1 = (double)Math.round(rate1*100000)/100000;
		DecimalFormat df = new DecimalFormat("#0.00000");
		return df.format(rate1);
		
	}
	/**
	 * Helping function to remove backrounds.
	 * @param mypshel
	 * @return name of factor
	 */
	public static String Removebackround(String mypshel) {
		mypshel = mypshel.replace("f","");
		mypshel = mypshel.replace("P","");
		mypshel = mypshel.replace("(", "");
		mypshel = mypshel.replace(")", "");
		return mypshel;
	}
	/**
	 * Function that returns asking factor name.
	 * @param query
	 * @return
	 */
	public static String getAsking(String query) {
		query = Removebackround(query);
		String[] queries = query.split("\\|");
		String[] queries2 = queries[0].split("=");
		return queries2[0];
	}
	/**
	 * *Not used in this version
	 * Function that checks if factor is relevant to our query.
	 * @param factor
	 * @return TRUE/FALSE
	 */
	public static boolean checkIfRelevant(Node factor) {
		String factor_name = ex1.Removebackround(factor.getName());
		Set<String> queries_values = myQueryValues.keySet();
		for(String key : queries_values) {
			if(!factor.name.equals(key)) {
				for(int i = 0; i<factor.NodeParents.size(); i++) {
					if(key.equals(ex1.Removebackround(factor.NodeParents.get(i).name)))
						return true;
				}
			}
			else 
				return true;
		}
		return false;
	}
	/**
	 * @param query
	 * @return ArrayList<Node>
	 */
	public ArrayList<Node> getNodes(String query) {
		ArrayList<Node> nodes = new ArrayList<>();
		
		query = Removebackround(query);
		query = query.replace("|", ",");
		String[] temp = query.split(",");
		
		for(int i = 0; i<temp.length; i++) {
			int index = ex1.FindIndexByName(Network, temp[i]);
			nodes.add(Network.get(index));
		}
		return nodes;
		
	}
	/**
	 * Transfering to factor function
	 * @param factor 
	 * @return Factor object.
	 */
	public Factor transferToFactor(String factor) { 
		Hashtable<String,Float> hash = new Hashtable<String,Float>();
		String my_factor = Removebackround(factor);
		if(my_factor.length() == 1) {
			int index = ex1.FindIndexByName(Network, my_factor);
			return new Factor(Network.get(index), "f("+my_factor+")");
		}	
		ArrayList<Node> myNodes = getNodes(factor);
		Node child=  myNodes.get(0);
		/*if(!checkIfRelevant(child)) { //////////////
			int index = ex1.FindIndexByName(Network, ex1.Removebackround(child.name));
			Network.remove(index);
		    return null;
		}*/
		Hashtable<String,Float> factor_CPT = child.CPT;
		for(int i = 0; i<myNodes.size(); i++) {

			Node fact = myNodes.get(i);
				if(myQueryValues.get(fact.getName()) != null && !getAsking(query).equals(fact.getName())) {
					String known = fact.getName()+"="+myQueryValues.get(fact.getName());
					Set<String> keys = factor_CPT.keySet();
					for(String k : keys) {
						if(k.contains(known)) {
							String new_key = "";
							if(k.indexOf(known) == 0) 
								new_key = k.replace(known+",", "");			   
							else 
								new_key = k.replace(","+known, "");

							hash.put(k, factor_CPT.get(k));
						}
					}
				}
				if(!hash.isEmpty()) {
					factor_CPT = (Hashtable<String, Float>) hash.clone();
					hash.clear();
				}

		}
		Node factor_node = child;
		
		String new_name_factor = "f("+my_factor.replace("|", ",")+")";
		return  new Factor(new_name_factor, factor_CPT , new Pair(0,0));
		
	}
	/**
	 * Eliminate function.
	 * @param factor 
	 * @param to_eliminate - name of what to eliminate.
	 * @return Factor after eliminate.
	 */
	public static Factor Eliminate(Factor factor, String to_eliminate) {
		int pluscounter = 0;
		Set<String> keys = factor.getHash().keySet();
		Hashtable<String,Float> new_hash = new Hashtable<String,Float>();
		String factor_name = Removebackround(factor.name);
		
		ArrayList<Node> myNodes = new ArrayList<>();
		String[] temp = factor_name.split(",");
		
		for(int i = 0;i<temp.length; i++) {
			if(!temp[i].contains(to_eliminate)) {
				int index = ex1.FindIndexByName(Network, temp[i]);
				myNodes.add(Network.get(index));
			}
		}
		ArrayList<String> new_keys = new ArrayList<>();
		if(myNodes.size() == 1) {
			Node fact = myNodes.get(0);
			for(int k = 0; k<fact.getValues().size(); k++) {
				new_keys.add(fact.name+"="+fact.getValues().get(k));
			}
 		}
		else if(myNodes.size() ==2) {
			new_keys =  ex1.getFullCombinations(myNodes.get(0),myNodes.get(1));
		}
		else if (myNodes.size() > 2){
			new_keys =  ex1.getFullCombinations(myNodes.get(0),myNodes.get(1));
			
			for(int i= 2; i<myNodes.size(); i++) {
				new_keys = ex1.getFullCombinations(myNodes.get(i),new_keys);
			}
		}
		else {
			return factor;
		}
		

		String new_factor_name = "";
		for(String nkey : new_keys) {
			new_factor_name = getFactorName(nkey);
			for(String okey : keys) {
				if(containsTwoStrings(okey,nkey)) {
					Float rate = factor.getHash().get(okey);
					if(!new_hash.containsKey(nkey)) {
					    new_hash.put(nkey, rate);
					}
					else {
						new_hash.put(nkey, new_hash.get(nkey) + rate);
						sumcounter++;
					}
				}
			}
		}

		return new Factor(new_factor_name, new_hash, new Pair(0,factor.result.mulcount, factor.result.sumcount+pluscounter));
		
		
	}
	/**
	 * Removing unneeded queries, currently not used.
	 * @param keys
	 * @return
	 */
	public static ArrayList<String> removeNotNeeded(ArrayList<String> keys) {
		if(keys.isEmpty()) 
			return keys;
		String key = keys.get(0);
		String condition = "";
		ArrayList<String> myNeeded = new ArrayList<String>();
		if(key.contains(",")) {
			String[] tmp = key.split(",");
			for(int i = 0; i<tmp.length; i++) {
				String[] tmp2 = tmp[i].split("=");
				if(myQueryValues.get(tmp2[0]) != null && !getAskingName(getAsking(query)).equals(tmp2[0])) {
					condition += tmp2[0]+"="+myQueryValues.get(tmp2[0])+",";
				}
			}
			condition = condition.substring(0, condition.length()-1);
		}
		if(condition.isEmpty()) {
			return keys;
		}

		for(int i = 0; i<keys.size(); i++) {
			if(containsTwoStrings(keys.get(i),condition)) {
				myNeeded.add(keys.get(i));
			}
		}
		return myNeeded;
	}

	/**
	 * Function that compares between two strings.
	 * @param one
	 * @param two
	 * @param to_eliminate
	 * @return TRUE/FALSE
	 */
	public static boolean compareTwoStrings(String one, String two, String to_eliminate) {
		String[] temp1 = one.split(",");
		String[] temp2 = two.split(",");
		String new_string = "";
		for(int i = 0; i <temp2.length; i++) {
			if(temp2[i].contains(to_eliminate)) {
				if(two.indexOf(to_eliminate) == 0) 
				   two = two.replace(temp2[i]+",", "");
				else 
					two = two.replace(","+temp2[i], "");
			}
		}
		if(one.equals(two)) 
			return true;
		else 
			return false;
	}
	/**
	 * Function that check if one is contained two according to my code.
	 * @param one
	 * @param two
	 * @return TRUE/FALSE
	 */
	public static boolean containsTwoStrings(String one, String two) {
		int counter = 0;
		if(one.contains(",") && two.contains(",")) {
		   String[] temp1 = one.split(",");
		   String[] temp2 = two.split(",");
		   for(int i =0; i<temp2.length; i++) {
			   for(int j = 0; j<temp1.length; j++) {
				   if(temp2[i].equals(temp1[j])) {
					   counter++;
				   }
			   }
		   }
		   if(counter == temp2.length) 
			   return true;
		}
		else if(one.contains(",") && !two.contains(",")) {
			String[] temp1 = one.split(",");
			 for(int j = 0; j<temp1.length; j++) {
				   if(temp1[j].equals(two)) {
					   counter++;
				   }
			   }
			 if(counter == 1) 
				 return true;
		}
		else if(!one.contains(",") && two.contains(",")) {
			String[] temp1 = two.split(",");
			 for(int j = 0; j<temp1.length; j++) {
				   if(temp1[j].equals(one)) {
					   counter++;
				   }
			   }
			 if(counter == 1) 
				 return true;
		}
		else if(!one.contains(",") && !two.contains(",")) {
			if(two.equals(one))
				return true;
		}
		return false;
	}
	/**
	 * Join function.
	 * @param Variables - factors to join
	 * @param factor_to_join - name of factor to be joined.
	 * @param myNetwork - Network.
	 * @return Factor after Join
	 */
	public static Factor Join(ArrayList<Factor> Variables, String factor_to_join, ArrayList<Node> myNetwork) {
		Factor new_factor;
		if(Variables.size() < 2) {
			return Variables.get(0);
		}
		Variables.sort(Factor.Sort);
		if(Variables.size() == 2) {
			new_factor = Join(Variables.get(0),Variables.get(1), factor_to_join);
			return new_factor;
		}
		else {
			new_factor = Join(Variables.get(0),Variables.get(1), factor_to_join);
			for(int i = 2; i<Variables.size(); i++) {

				new_factor = Join(Variables.get(i), new_factor,factor_to_join);
			}
		}
		return new_factor;
	}
	/**
	 * Helper function.
	 * @param keys1
	 * @param keys2
	 * @return TRUE/FALSE
	 */
	public static boolean checkIfHashEquals(Set<String> keys1, Set<String> keys2) {
		for(String key : keys1) {
			for(String key2 : keys2) {
				if(key.equals(key2)) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Function that recives 2 factors and join them.
	 * @param one
	 * @param two
	 * @param join_factor
	 * @return Joined Factor.
	 */
	public static Factor Join(Factor one, Factor two, String join_factor) { // FIX TOMMOROW
		Set<String> one_keys = one.getHash().keySet();
		Set<String> two_keys = two.getHash().keySet();
		if(checkIfHashEquals(one_keys, two_keys)) 
			return two;
		//int mulcounter = one.result.mulcount+two.result.mulcount;
		//int sumcounter = one.result.sumcount + two.result.sumcount;
		Hashtable<String,Float> new_hash = new Hashtable<String,Float>();

		join_factor = getCommon(Removebackround(one.name),Removebackround(two.name));
		if(join_factor.length() <= 1) {
			Node factor = Network.get(ex1.FindIndexByName(Network, join_factor));
			for(int i = 0; i<factor.getValues().size(); i++) {
				String condition = factor.name+"="+factor.getValues().get(i);
                
				for(String okeys : one_keys) {
					for(String tkeys : two_keys) {
						if(tkeys.contains(condition) && okeys.contains(condition)) {
							String new_key = tkeys.replace(condition, okeys);
							if(new_hash.containsKey(new_key)) {
							  new_hash.put(new_key, new_hash.get(new_key) + (one.getHash().get(okeys) * two.getHash().get(tkeys)));
							  mulcounter++;
							}
							else {
								new_hash.put(new_key, (one.getHash().get(okeys) * two.getHash().get(tkeys)));
								mulcounter++;
							}
						}
					}

				}


			}

		}
		else {
			ArrayList<Node> conditions = new ArrayList<>();
			for(int i =0; i<join_factor.length(); i++) {
				conditions.add(Network.get(ex1.FindIndexByName(Network, Character.toString(join_factor.charAt(i)))));
			}
			ArrayList<String> combinations = new ArrayList<>();
			if(conditions.size() > 2) {
				combinations = ex1.getFullCombinations(conditions.get(0),conditions.get(1));

				for(int i = 2; i<conditions.size(); i++) {
					combinations = ex1.getFullCombinations(conditions.get(i),combinations);
				}

			}
			else if(conditions.size() == 2) {
				combinations = ex1.getFullCombinations(conditions.get(0),conditions.get(1));
			}
			for(int j = 0; j<combinations.size(); j++) {
				String condition = combinations.get(j);
				for(String okeys : one_keys) {
					for(String tkeys : two_keys) { //////////////////////
					//	if(tkeys.contains(condition) && okeys.contains(condition)) {
						if(containsTwoStrings(tkeys,condition) && containsTwoStrings(okeys,condition)) {
							String remainder = "";
							if(okeys.indexOf(condition) == 0) {
								remainder = okeys.replace(condition+",", "");
							}
							else {
							   remainder = okeys.replace(condition, "");
							}
							String new_key = tkeys.replace(condition, remainder);
							if(new_hash.containsKey(new_key)) {
							   new_hash.put(new_key, new_hash.get(new_key) + (one.getHash().get(okeys) * two.getHash().get(tkeys)));
							   mulcounter++;
							}
							else {
								new_hash.put(new_key, (one.getHash().get(okeys) * two.getHash().get(tkeys)));
							}
						}
					}

				}
			}

		}
		Set<String> key_for_name = new_hash.keySet();
		String new_name ="";
		for(String key : key_for_name) {
			new_name = getFactorName(key);
			break;
		}
		
        return new Factor(new_name, new_hash, new Pair(0,mulcounter,sumcounter));



	}
	/**
	 * Join for strangers factors.
	 * @param one
	 * @param two
	 * @param asking
	 * @return
	 */
	public static Factor JoinStrangers(Factor one, Factor two, String asking) {
		Set<String> one_keys = one.getHash().keySet();
		Set<String> two_keys = two.getHash().keySet();
		//int mulcounter = 0;
		String new_name ="";
		Hashtable<String,Float> hash = new Hashtable<String,Float>();
		String one_name = Removebackround(one.name);
		String two_name = Removebackround(two.name);
		String known = "";
		
		if(myQueryValues.get(one_name) != null && !getAskingName(asking).equals(one_name)) {
			known += one_name+"="+myQueryValues.get(one_name)+",";
		}
		if(myQueryValues.get(two_name) != null && !getAskingName(asking).equals(two_name)) {
			known += two_name+"="+myQueryValues.get(two_name)+",";
		}
		known = known.substring(0, known.length()-1);
		
		
		for(String okey : one_keys) {
			for(String tkey : two_keys) {
				if(known.isEmpty()) {
				   String new_key = okey+","+tkey;
				   hash.put(new_key, one.getHash().get(okey)*two.getHash().get(tkey));
				   mulcounter++;
				   new_name = getFactorName(new_key);
				}
				else {
					if(okey.contains(known) || tkey.contains(known)) {
						String new_key = okey+","+tkey;
						   hash.put(new_key, one.getHash().get(okey)*two.getHash().get(tkey));
						   mulcounter++;
						   new_name = getFactorName(new_key);
					}
				}
			}
		}
		Pair res = new Pair(0, one.result.mulcount+two.result.mulcount+mulcounter, one.result.sumcount+two.result.sumcount);
		return new Factor(new_name,hash,res);
	}
	/**
	 * 
	 * @param key
	 * @param factor
	 * @return
	 */
	public static String getFactorValue(String key, String factor) {
		String[] temp = key.split(",");
		
		for(int i =0; i<temp.length; i++) {
			if(temp[i].contains(factor)) {
				return temp[i];
			}
		}
		return null;
	}
	/**
	 * Checking for common between two strings.
	 * @param s1
	 * @param s2
	 * @return Common string
	 */
	public static String getCommon(String s1, String s2) 
    { 
		String result = "";
       
		for(int i =0; i<Network.size(); i++) {
			String factor_name = Network.get(i).getName();
			if(s1.contains(factor_name) && s2.contains(factor_name)) {
				result += factor_name;
			}
		}
        return result;
    }
	/**
	 * @param key
	 * @return name of factor.
	 */
	public static String getFactorName(String key) {
		String name = "";
		if(key.contains(",")) {
			String[] temp = key.split(",");
			
			for(int i = 0; i<temp.length; i++) {
				String[] temp2 = temp[i].split("=");
				name += temp2[0]+",";
			}
		}
		else {
			String[] temp = key.split("=");
			name += temp[0];
			name = "f("+name+")";
			return name;
		}
		name = name.substring(0, name.length() -1);
		name = "f("+name+")";
		return name;
	}
}
