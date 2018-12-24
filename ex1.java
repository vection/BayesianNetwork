
import java.io.*; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set; 


/**
 * This program is desired to compute conditional probabilty queries in Bayesian Network.
 * Currently it has 3 different methods of computing each query - 
 * 1. Normal computing - without improvements.
 * 2. Variable Elimination - ABC ordering.
 * 3. Variable Elimination - Min Weights ordering.
 * 
 * @author Aviv
 *
 */
public class ex1 {

	static ArrayList<Factor> myfactors = new ArrayList<>();
	static ArrayList<String> queries_to_compute = new ArrayList<>();
    static String Asking ="";
	
	
	
	
	/**
	 * Starting funtion
	 * @param input2
	 * @throws IOException
	 */
	public static void StartAlgo1(File Input) throws IOException {
        File Output = new File("output.txt");
		BufferedReader br = new BufferedReader(new FileReader(Input)); 
		PrintStream out = new PrintStream(Output);
		VariableElimination p2 = new VariableElimination();
		VariableElimination p3 = new VariableElimination();
		ArrayList<Node> Network = new ArrayList<>();

        boolean queries = false;
		String st; 
		while ((st = br.readLine()) != null) {
			if(st.equals("Network")) {
				Network = CreateNetwork(br.readLine());
			}
        else if(st.equals("Queries") || queries == true) {
        	   if(st.equals("Queries")) 
        		   st = br.readLine();
				queries = true;
				String query = st;
				try {
					int index = query.lastIndexOf(",");
					query = st.substring(0, index);
					String algo = st.substring(index+1);
					if(algo.contains("1")) {
						out.println(GetResults(query,Network));
					}
					else if(algo.contains("2")) {
						p2 = new VariableElimination(query, Network,0);					   
						out.println(p2.final_answer);
					}
					else if(algo.contains("3")) {
						p3 = new VariableElimination(query, Network,1);					   
						out.println(p3.final_answer);
					}
				}
				catch(Exception s) {}

			}
			else {
				for(int index = 0; index < Network.size(); index++) {

					if(st.equals("Var "+Network.get(index).getName())) { // found new Var to add
						String values = br.readLine();
						AddValues(Network.get(index), values); // Adding values

						String parents = br.readLine(); // Parents string
						AddParents(Network.get(index), parents, Network); // function of adding parents

						br.readLine();

						String CPTs = br.readLine(); // adding CPT's
						while(!CPTs.isEmpty()) {
							Network.get(index).AddCPT(CPTs);
							CPTs = br.readLine();
						}
					}	
				}
			}
		} 
		//ShowNetwork(Network);
		System.out.println("\n");
		//System.out.println(GetCPT(Network,"P(B=true)"));
		//	getFullQuery(Network, "P(B=true|J=true,M=true)");
		//System.out.println(GetResults("P(B=true|J=true,M=true)", Network));
		//System.out.println(GetResults("P(L=true|I=true,D=true)", Network));
		//System.out.println(GetResults("P(L=true|I=true,D=true)", Network));
		//System.out.println(VariableElimination("P(B=true|J=true,M=true)", Network));
		//System.out.println(VariableElimination("P(L=true|I=true,D=true)", Network));
		//System.out.println(VariableElimination("P(A=true|C=run)", Network));
		
		
		//VariableElimination p = new VariableElimination("P(C=run|B=set,A=true)", Network);
		
		//VariableElimination p3 = new VariableElimination("P(A=true|C=run)", Network);
		//VariableElimination p = new VariableElimination("P(J=true|B=true)", Network);
		
		//VariableElimination p2 = new VariableElimination("P(B=true|J=true,M=true)", Network);
		/*VariableElimination p9 = new VariableElimination("P(A=true|C=true)",Network, 0);
		System.out.println(p9.final_answer);
		
		/*VariableElimination p10 = new VariableElimination("P(M=false|A=female,D=d)",Network, 1);
		System.out.println(p10.final_answer);
		VariableElimination p5 = new VariableElimination("P(M=false|A=female)",Network, 0);
		System.out.println(p5.final_answer);
		/*VariableElimination p4 = new VariableElimination("P(D=true|A=true,B=false)",Network, 1);
		System.out.println(p4.final_answer);
		//VariableElimination p9 = new VariableElimination("P(X=true|B=false)",Network);
		//System.out.println(p9.query +"= "+p9.final_answer);
		/*VariableElimination p0 = new VariableElimination("P(L=true|D=true)",Network);
		System.out.println(p0.query +"= "+p0.final_answer);
		VariableElimination p3 = new VariableElimination("P(G=medium|S=false)",Network);
		System.out.println(p3.query +"= "+p3.final_answer);
        
		

		System.out.println(GetResults("P(L=true|I=true,D=true)", Network));
		
		System.out.println(GetResults("P(G=medium|S=false)", Network));
		System.out.println(GetResults("P(L=true|D=true)", Network));
		
*/
	}

    /**
     * 
     * @param Networkinfo - file path of basyen network
     * @return ArrayList of Nodes represents each node.
     */
	public static ArrayList<Node> CreateNetwork(String Networkinfo) { // Creating the Network.
		ArrayList<Node> MyNetwork = new ArrayList<>();
		String[] mydata = Networkinfo.split(" ");

		mydata = mydata[1].split(",");

		for(int i =0; i<mydata.length; i++) {
			Node temp = new Node(mydata[i]);
			MyNetwork.add(temp);
		}
		return MyNetwork;		
	}
/**
 * Adding parents function.
 * @param myNode 
 * @param parents
 * @param myNetwork
 */
	public static void AddParents(Node myNode, String parents, ArrayList<Node> myNetwork) { // Adding Parents to node.
		String[] temp = parents.split(" ");
		temp = temp[1].split(",");
		if(temp[0].equals("none")) {}	
		else {
			for(int i = 0; i<temp.length; i++) {
				int currentindex = FindIndexByName(myNetwork,temp[i]);
				myNode.NodeParents.add(myNetwork.get(currentindex));
			}
		}
	}
/**
 * Adding values function.
 * @param myNode
 * @param values
 */
	public static void AddValues(Node myNode, String values) { // Adding Values to node.
		String[] temp = values.split(" ");
		for(int i = 1; i<temp.length; i++) {
			myNode.Values.add(temp[i].replace(",", ""));  
		}
	}
/**
 * @param myNetwork
 * @param c - name of node
 * @return index in list.
 */
	public static Integer FindIndexByName(ArrayList<Node> myNetwork, String c) { // Finding Index in Network by name.
		for(int i = 0; i<myNetwork.size(); i++) {
			if(myNetwork.get(i).getName().equals(c)) 
				return i;
		}
		return 0;
	}
	/**
	 * @param Network
	 * @param pshel
	 * @return CPT
	 */
	public static float GetCPT(ArrayList<Node> Network, String pshel) { // By giving simple query checking for his CPT.
		String[] temp = pshel.split("\\(");
		String mypshel = temp[1].replace(")", "");
		temp = mypshel.split("=");
		int index = FindIndexByName(Network, temp[0]);
		float rate = Network.get(index).CPT.get(temp[1]);
		return rate;
	}
/**
 * @param query
 * @param Network
 * @return Opposite values.
 */
	public static ArrayList<String> GetOpposite(String query, ArrayList<Node> Network) { // Input: Query. Output: Opposite query.

		ArrayList<String> myOpp = new ArrayList<>();

		String[] temp = query.split("\\|");
		String[] temp2 = temp[0].split("=");

		temp2[0] = temp2[0].replace("P", "");
		temp2[0] = temp2[0].replace("(", "");
		if(FindIndexByName(Network,temp2[0]) == null) { return null; }
		Node factor = Network.get(FindIndexByName(Network,temp2[0]));

		for(int i =0; i < factor.getValues().size(); i++) {
			if(!temp2[1].equals(factor.getValues().get(i))) { 
				temp2[1] = factor.getValues().get(i);
				myOpp.add(temp2[0]+"="+temp2[1]+"|"+temp[1]);
			}
		}
		return myOpp;

	}
	/**
	 * Function that give final answer to Algorithem 1.
	 * @param query
	 * @param Network
	 * @return String as result.
	 */
	public static String GetResults(String query, ArrayList<Node> Network) { // Input: Query,Network Output: String as final solution requested .
		int mulcounter = 0;
		int pluscounter = 0;


		String[] myQuery = query.split("\\|");
		ArrayList<String> myFullQuerys = getFullQuery(Network, query); // Upper rate
		myQuery[1] = "P("+myQuery[1]; // Down rate
		Pair t = Calculate(myFullQuerys, Network);
		float myResult = t.getRate(); // Calculating first expression.
		mulcounter += t.getmulcount();
		pluscounter += t.getsumcount();

		myFullQuerys = GetOpposite(query, Network); // Opposite query for normalization

		ArrayList<String> temp = new ArrayList<>();
		float myResult2 = 0;
		for(int i = 0; i<myFullQuerys.size(); i++) {
			temp = getFullQuery(Network, myFullQuerys.get(i));
			Pair sq = Calculate(temp, Network);
			myResult2 += sq.getRate();
			mulcounter += sq.getmulcount();
			pluscounter += sq.getsumcount();
		}
		pluscounter--; // Can compute the last query with 

		float myResult4 = myResult/(myResult2+myResult);
		String finalresult = VariableElimination.fixRate(myResult4)+","+pluscounter+","+mulcounter;
		return finalresult;

	}
  /**
   *  Function that calculate the querys that I have to compute.
   * @param Network
   * @param query
   * @return
   */
	public static ArrayList<String> getFullQuery(ArrayList<Node> Network, String query) { // Function that calculate the querys that I have to compute.

		query = query.replace("|", ",");
		String myVars = getVars(Network);
		for(int i = 0; i<Network.size(); i++) {
			int index = query.indexOf(Network.get(i).getName());
			if(index != -1) {
				myVars = myVars.replace(Network.get(i).getName(), "");
			}
		}
		if(myVars.isEmpty()) { 
			ArrayList<String> onequery = new ArrayList<>();
			onequery.add(query);
			return onequery;
		}
		ArrayList<Node> vv = new ArrayList<>();
		for(int i = 0; i<myVars.length(); i++) {
			int index = FindIndexByName(Network, Character.toString(myVars.charAt(i)));
			ArrayList<String> myNode = Network.get(index).getValues();
			vv.add(Network.get(index));

		}

		ArrayList<String> psps = new ArrayList<>();
		if(vv.size() > 2) {
			psps = getFullCombinations(vv.get(0),vv.get(1));

			for(int i = 2; i<vv.size(); i++) {
				psps = getFullCombinations(vv.get(i),psps);
			}

		}
		else if(vv.size() == 2) {
			psps = getFullCombinations(vv.get(0), vv.get(1));
		}
		else {

			for(int j = 0; j<vv.get(0).getValues().size(); j++) {
				psps.add(vv.get(0).getName()+"="+vv.get(0).Values.get(j));

			}

		}
		ArrayList<String> myQuerys = new ArrayList<>();
		query = query.replace(")", "");
		query = query.replace("|", ",");
		for(int i = 0; i<psps.size(); i++) { 
			myQuerys.add(query+","+psps.get(i)+")");  
		}
		return myQuerys;
	}
/**
 * Find key function.
 * @param Network
 * @param key
 * @return float as rate.
 */
	public static float FindKey(ArrayList<Node> Network, String key) {
		for(int i = 0; i<Network.size(); i++) {
			if(Network.get(i).CPT.containsKey(key)) {
				return Network.get(i).CPT.get(key);
			}
		}
		return -1;
	}
/**
 * Function that calculates single query.
 * @param query
 * @param Network
 * @return Pair
 */
	public static Pair CalculateSingleQuery(String query, ArrayList<Node> Network) { // Calculating Single Query.
		query = query.replace("P", "");
		query = query.replace("(", "");
		query = query.replace(")", "");

		int mulcounter = 0;
		String[] factors = query.split(",");
		Hashtable<String, String> CPT = new Hashtable<String, String>();
		for(int i =0; i<factors.length; i++) { 
			String[] temp = factors[i].split("=");
			CPT.put(temp[0], temp[1]);
		}
		float totalrate = 1;
		for(int i =0; i<factors.length; i++) {
			String temp = "P("+factors[i]+")";
			try {
				float rate = GetCPT(Network,temp);
				totalrate *= rate;
				mulcounter++;
			}
			catch (Exception p ) {
				float myrate;
				String[] myVar = factors[i].split("=");
				int index = FindIndexByName(Network, myVar[0]);
				Node factor = Network.get(index);

				String myKey = "";
				ArrayList<Node> factorParents = factor.NodeParents;

				if(factorParents.size() == 1) {
					String label = CPT.get(factorParents.get(0).getName());
					myrate = factor.CPT.get(factorParents.get(0).getName()+"="+label+","+factor.getName()+"="+myVar[1]);
					totalrate *= myrate;
					mulcounter++;
				}
				else {

					for(int u = 0; u<factorParents.size(); u++) {
						myKey += factorParents.get(u).getName()+"="+CPT.get(factorParents.get(u).getName())+",";
					}
					myKey += factor.getName()+"="+myVar[myVar.length-1];
					try {

						myrate = factor.CPT.get(myKey);
						totalrate *= myrate;
						mulcounter++;
					}
					catch(Exception p2) {}
				}
			}
		}
		mulcounter--; // Because we over counting for each factor.
		Pair results = new Pair(totalrate, mulcounter);
		return results;

	}
/**
 * Calculate list of queries.
 * @param query
 * @param Network
 * @return Pair.
 */
	public static Pair Calculate(ArrayList<String> query, ArrayList<Node> Network) { // Calculating the query rate.
		float rate = 0;
		int pluscounter = 0;
		int mulcounter = 0;
		for(int i = 0; i<query.size(); i++) {
			Pair temp = CalculateSingleQuery(query.get(i), Network);
			rate += temp.getRate();
			mulcounter += temp.getmulcount();
			pluscounter++;
		}

		Pair solution = new Pair(rate, mulcounter, pluscounter);
		return solution;
	}

	// Variable Elimination
	/**
	 * Function that provides elimination order for Algorithem 2.
	 * @param Network
	 * @param myValues
	 * @param asking
	 * @return elimination order.
	 */
	public static String getEliminationOrder(ArrayList<Node> Network, Hashtable<String, String> myValues, String asking) {
		Set<String> mykeys = myValues.keySet();
		String allvars ="";
		for(int i = 0; i< Network.size(); i++) {
			allvars += Network.get(i).getName();
		}
		for(String key : mykeys) {
			if(allvars.contains(key)) {
				allvars = allvars.replace(key, "");
			}
		}
		/*for(String key : mykeys) {
			if(!key.equals(asking)) {
				return sortString(allvars)+key;
			}
		}*/
		return sortString(allvars);
	}
	/**
	 * Function that splits query to factors. 
	 * @param query
	 * @param Network
	 * @param myValues
	 * @return List of strings represents factors names.
	 */
	public static ArrayList<String> Split(String query, ArrayList<Node> Network, Hashtable<String,String> myValues) {
		ArrayList<Node> myNodes = new ArrayList<>();
		ArrayList<String> myQueries = new ArrayList<>();
		for(int i =0; i<Network.size(); i++) {
			Node factor = Network.get(i);
			if(myValues.get(factor.getName()) == null ) {
				myNodes.add(factor);
			}

		}
		query = Removebackround(query);
		query = query.replace("|", ",");
		String[] splitquery = query.split(",");

		for(int i =0; i<splitquery.length; i++) {
			String[] sq = splitquery[i].split("=");
			int index = FindIndexByName(Network,sq[0]);
			Node factor = Network.get(index);
			if(factor.NodeParents.size() < 1 && myValues.get(factor.getName()) == null) {
				myQueries.add("P("+factor.getName()+")");
			}
			else {
				String q = "P("+factor.getName()+"|";
				for(int j = 0; j<factor.NodeParents.size(); j++) {
					q += factor.NodeParents.get(j).getName()+",";
				}
				q = q.substring(0, q.length() - 1);
				q+= ")";
				myQueries.add(q);
			}
		}

		for(int i =0; i<myNodes.size(); i++) {
			Node factor = myNodes.get(i);
			if(factor.NodeParents.size() < 1  && myValues.get(factor.getName()) == null) {
				myQueries.add("P("+factor.getName()+")");
			}
			else {
				int need = 0;
				String q = "P("+factor.getName()+"|";
				for(int j = 0; j<factor.NodeParents.size(); j++) {
					q += factor.NodeParents.get(j).getName()+",";
					if(myValues.get(factor.NodeParents.get(j).name) == null) 
						need++;
				}
				q = q.substring(0, q.length() - 1);
				q+= ")";
				//if(need != factor.NodeParents.size()) 
					myQueries.add(q);
			}
		}
		return myQueries;

	}
	/**
	 * Transfer query variables to hashtable.
	 * @param query
	 * @return hashtable -> each node name to value. (evidence)
	 */
	public static Hashtable<String, String> getKnownValues(String query) {
		Hashtable<String,String> myKnown = new Hashtable<String,String>();
		query = query.replace("|", ",");
		query = Removebackround(query);
		String[] myValues = query.split(",");
		for(int i = 0; i<myValues.length; i++) {
			String[] myValue = myValues[i].split("=");
			myKnown.put(myValue[0], myValue[1]);

		}
		return myKnown;
	}
  /**
   * Remove back round function.
   * @param mypshel
   * @return Node name.
   */
	public static String Removebackround(String mypshel) {
		mypshel = mypshel.replace("P","");
		mypshel = mypshel.replace("(", "");
		mypshel = mypshel.replace(")", "");
		return mypshel;
	}
 /**
  * Factor name.
  * @param key
  * @return String as name.
  */
    public static String getFactorName(String key) {
    	key = Removebackround(key);
    	String newkey="f(";
    	String[] mykey = key.split(",");
    	for(int i =0; i<mykey.length; i++) {
    		String[] mykey2 = mykey[i].split("=");
    		newkey += mykey2[0]+",";
    	}
    	newkey = newkey.substring(0, newkey.length() - 1);
    	newkey += ")";
    	return newkey;
    }

/**
 * Sorting function.
 * @param inputString
 * @return sorted string.
 */
	public static String sortString(String inputString) 
	{ 
		char tempArray[] = inputString.toCharArray(); 
		Arrays.sort(tempArray); 
		return new String(tempArray); 
	} 

/**
 * Getting variables from query.
 * @param query
 * @param Network
 * @return list of nodes.
 */
	public static ArrayList<Node> getVars(String query, ArrayList<Node> Network) {
		query = query.replace("|", ",");
		query = query.replace("P", "");
		query = query.replace("(","");
		query = query.replace(")", "");
		String[] temp = query.split(",");
		ArrayList<Node> miniNetwork = new ArrayList<>();
		for(int i =0; i<temp.length; i++) {
			int index = FindIndexByName(Network, Character.toString(temp[i].charAt(0)));

			miniNetwork.add(Network.get(index));


		}
		return miniNetwork;
	}
/**
 * Display network.
 * @param myNetwork
 */
	public static void ShowNetwork(ArrayList<Node> myNetwork) { // Function for displaying the Network.
		for(int i = 0; i<myNetwork.size(); i++) {
			System.out.println(myNetwork.get(i).ShowNode());
		}
	}

	public static String getVars(ArrayList<Node> Network) { 
		String p = "";
		for(int i = 0; i<Network.size(); i++) {
			p += Network.get(i).getName();
		}
		return p;
	}
/**
 * Function that giving all combinations between two nodes.
 * @param arr1
 * @param arr2
 * @return List of strings - combinations.
 */
	public static ArrayList<String> getFullCombinations(Node arr1, Node arr2) // Function that helps in combinations between two nodes.
	{ 
		ArrayList<String> tt = new ArrayList<>();
		for (int i = 0; i < arr1.getValues().size(); i++) 
			for (int j = 0; j < arr2.getValues().size(); j++) 
				tt.add(arr1.getName()+"="+arr1.getValues().get(i)+","+arr2.getName()+"="+arr2.getValues().get(j));
		return tt;
	} 
	/**
	 * Function that giving all combinations between two nodes.
	 * @param arr1
	 * @param arr2
	 * @return List of strings - combinations.
	 */
	public static ArrayList<String> getFullCombinations(Node arr1, ArrayList<String> arr2) // Same function as above just handle another case.
	{ 
		ArrayList<String> tt = new ArrayList<>();
		for (int i = 0; i < arr2.size(); i++) 
			for (int j = 0; j < arr1.getValues().size(); j++) 
				tt.add(arr2.get(i)+","+arr1.getName()+"="+arr1.getValues().get(j));
		return tt;
	}
	public static void main(String[] args) throws IOException {
		File Input = new File("input.txt"); 
		StartAlgo1(Input);
	}
	

}
