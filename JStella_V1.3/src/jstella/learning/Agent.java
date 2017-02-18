package jstella.learning;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Random;

public class Agent {

	private JSILearning JSI;

	double gamma = 0.9,
			eps = 0.9,
			alpha = 0.9,
			lambda = 0.9;
	int episodesToRun = 10000,
			episodeNumber = 0,
			currentScore = 0,
			scoreDifference = 0,
			currentLives = 0,
			liveDifference = 0,
			numberOfPrototypes = 5000;
	int[] actions = new int[3];

	Random rnd = new Random();
	int counter = 0;
	boolean deadFlag = false;
	boolean prototypesMade = false;
	int[] Cstate, Pstate;
	String fileName = "memSpaceInvaders.txt";

	//HashTables	
	Hashtable<StateActionPair,  Double> prototypeAction = new Hashtable<StateActionPair, Double>();
	Hashtable<int[], Integer> prototypeVisit = new Hashtable<int[], Integer>();

	//FOR RECORDING MEMORY SPACE
	/**ArrayList<ArrayList<Integer>> memspaceR = new ArrayList<ArrayList<Integer>>();**/
	//FOR USING MEMORY SPACE
	int[][] memspace = new int[128][];

	public Agent(JSILearning J){	
		/**
		 * Set JSILearning Object
		 */
		JSI = J;

		/**
		 * Boolean Flags To Set
		 */
		JSI.setSoundEnabled(false); //Default Value : true
		JSI.setKeysEnabled(false); //Default Value : true
		//JSI.setVideoEnabled(false);  //Default Value : true

		/**
		 * Variables Related To Emulation
		 */
		JSI.setFrameDelay(1); //Default Value : 17 (60 frames a second)
		//JSI.setAgentCallDelay(1); //Default Value : 1 (Agent called every frame)

		/**
		 * Variables Related To Images
		 */
		//JSI.setImagesEnabled(false); //Default Value : false
		//JSI.setFramesToAverage(4); //This is the default value
		//JSI.setNumberOfFramesBetween(3); //This is the default value

		/**
		 * Select A ROM File To Play
		 */
		JSI.loadROM(new File("SRC\\ROM\\SpaceInvaders.bin"));


		/**FOR MAKING FILES
		for(int x = 0; x <128; x++){
			memspaceR.add(x,new ArrayList<Integer>());
		}
		 **/

	}

	public int[] getAction() {
		//YOUR CODE STARTS HERE

		//Create prototypes
		if(!prototypesMade){
			createPrototypes();
		}

		//  -_---__---------------------------------
		//  |-|-/-/---------------------------------
		//  |-|/-/--__-_-_-__---___-_-____---____-_-
		//  |----\-/-_`-|-'_-\-/-_-\-'__\-\-/-/-_`-|
		//  |-|\--\-(_|-|-|-|-|--__/-|---\-V-/-(_|-|
		//  \_|-\_/\__,_|_|-|_|\___|_|----\_/-\__,_|
		//  ----------------------------------------
		if(episodeNumber==-1){//if we are done follow only the best policy
			int[] state = JSI.getROMState();
			actions = getBestActionByState(state);//choose best action
			return actions;
		}
		else if(episodeNumber < episodesToRun && deadFlag){//Reset everything at the end of the episode
			episodeNumber++;
			eps = doNormalReduction(episodeNumber,eps);
			deadFlag = false;
			Pstate = null;
			Cstate = null;

			if(episodeNumber%5==0){
				System.out.println("UPDATING PROTOTYPES PLEASE WAIT");
				updatePrototypes();
				System.out.println("COMPLETED");
			}

			System.out.println("RESET:: MOVING TO EPISODE: " + episodeNumber + "\n");
			JSI.reset();
			JSI.loadROM(new File("SRC\\ROM\\SpaceInvaders.bin"));
			actions = getRandomAction();
			return actions;
		}
		else if(episodeNumber >= episodesToRun){//If we have finished reset the system so we can watch the final policy
			/**FOR MAKING FILES
			try{
				PrintWriter writer = new PrintWriter(new FileWriter("memstate.txt"));

				for(int x = 0; x < memspaceR.size(); x++){
					for(int y = 0; y < memspaceR.get(x).size(); y++){

						writer.write(memspaceR.get(x).get(y) + " ");
					}
					writer.write("\n");
				}			
				writer.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			 **/

			JSI.setFrameDelay(17);
			JSI.loadROM(new File("SRC\\ROM\\SpaceInvaders.bin"));
			JSI.reset();
			episodeNumber = -1;
		}
		else{

			/**FOR MAKING FILES
			int[] mem = JSI.getMemory();
			for(int x = 0; x < mem.length; x++)
				if(!memspaceR.get(x).contains(mem[x]))
					memspaceR.get(x).add(mem[x]);
			 **/

			//Step Reward
			double stepReward = 1;

			if(Cstate == null){
				Cstate = JSI.getROMState();
				double r = Math.random();
				if(r<=eps)
					actions = getRandomAction();//choose random action
				else
					actions = getBestActionByState(Cstate);//choose best action 
				return actions;
			}

			Pstate = Cstate.clone();
			Cstate = JSI.getROMState();

			//Find the Score Difference to see if we gained points
			scoreDifference = (JSI.getROMScore() - currentScore);
			currentScore = JSI.getROMScore();

			//Find the live difference to see if we lost a life
			liveDifference = JSI.getROMLives() - currentLives;
			currentLives = JSI.getROMLives();

			//OPTIONS--------------------------------
			//OPTIONS FOR ENDING RUN EARLY

			//counter++;
			//if(counter>20000){
			//	deadFlag =true;
			//	counter = 0;
			//}

			if(!JSI.getROMAliveStatus()){
				deadFlag = true;
			}

			//OPTIONS FOR GIVING POSITIVE OR NEGATIVE REWARD
			if(scoreDifference > 0 )
				stepReward = scoreDifference;

			if(liveDifference < 0 )
				stepReward = -250;

			//END OPTIONS----------------------------

			for(int[] ste : prototypeVisit.keySet()){
				//Update the prototypeVisit hashtable to keep track of how often a prototype has been seen
				if(isAdjacent(ste,Pstate)==1){

					System.out.println("FOUND ONE\n");
					prototypeVisit.replace(ste, prototypeVisit.get(ste)+1);

					StateActionPair sap = new StateActionPair(ste,actions);

					//Update the value of each prototype based on the sum of those adjacent
					double O = prototypeAction.get(sap);
					//double e = O + isAdjacent(ste,Pstate) * alpha * (stepReward + gamma * (getSumAdjacentStates(sap.getState())));
					double e = O +  alpha * (stepReward + gamma * (getSumAdjacentStates(Cstate)));				
					prototypeAction.replace(sap, e);


				}
			}
			//Set next action a', chosen E-greedily based on Q(s',a')
			double r = Math.random();
			if(r<=eps)
				actions = getRandomAction();//choose random action
			else
				actions = getBestActionByState(Cstate);//choose best action 
		}

		//YOUR CODE ENDS HERE
		return actions;
	}

	/**
	 * Gets the value of all adjacent states and computes the summation
	 * @param state the current state to compare to
	 * @return the value of the summation
	 */
	private double getSumAdjacentStates(int[] state){
		double sumO = 0.0;
		for(StateActionPair s : prototypeAction.keySet()){
			if(isAdjacent(s.getState(),state)==1){
				sumO += prototypeAction.get(s);
			}
		}
		return sumO;
	}


	/**
	 * 
	 */
	private void createPrototypes(){
		int currentPrototype = 0;
		try{
			//Read in data about the state space from the given file
			BufferedReader reader = new BufferedReader( new FileReader( fileName ));
			String line = reader.readLine();
			while(line != null)
			{
				//Create jagged array to represent values
				String[] record = line.split("\\s");
				memspace[currentPrototype] = new int[record.length];
				for(int i = 0; i < record.length; i++)
					memspace[currentPrototype][i] = Integer.parseInt(record[i]);
				currentPrototype++;	
				line = reader.readLine();
			}
		}
		catch(Exception e){
			System.out.println(e);
		}

		//Create prototypes based on the state space created from the file
		int[][] validActions = JSI.getROMValidActions();
		while(prototypeVisit.size() < numberOfPrototypes){
			int[] prototype = createPrototype();
			if(!prototypeVisit.containsKey(prototype)){
				//Add this prototype to the visited hashtable 
				prototypeVisit.put(prototype, 0);
				for(int[] a : validActions)
					//Add this prototype to the prototype action hashtable for each action
					prototypeAction.put(new StateActionPair(prototype, a), 0.0);
			}
		}
		prototypesMade =true;
	}

	/**
	 * 
	 */
	private void updatePrototypes(){
		Hashtable<int[], Integer> newVisit = new Hashtable<int[], Integer>();
		Hashtable<StateActionPair,  Double> newProtoAct = new Hashtable<StateActionPair, Double>();

		//determine the probability for keeping a prototype p
		double probabilityToKeep; 
		double rndProb;
		for(int[] proto : prototypeVisit.keySet()){
			probabilityToKeep = Math.pow(Math.E, -(prototypeVisit.get(proto)));
			rndProb = rnd.nextDouble();
			//System.out.println("PRob:" + probabilityToKeep + "\nRand: " + r);
			if(rndProb > probabilityToKeep){//Keep the prototype
				newVisit.put(proto, 0);
			}

		}
		//Replace the visited table with the new one of prototypes we kept
		prototypeVisit = null;
		prototypeVisit = newVisit;

		for(StateActionPair sap : prototypeAction.keySet()){
			if(prototypeVisit.containsKey(sap.getState())){
				newProtoAct.put(sap, prototypeAction.get(sap));
			}
		}
		//Replace the state,action table with only those prototypes that survived
		prototypeAction =  null;
		prototypeAction = newProtoAct;


		System.out.println("SIZE OF VISIT:" + prototypeVisit.size());
		System.out.println("SIZE OF PA:" + prototypeAction.size());

		//Generate new prototypes randomly or by splitting
		if(prototypeVisit.size() < (numberOfPrototypes/100)){//RANDOM
			//Add new prototypes until we are full
			int[][] validActions = JSI.getROMValidActions();
			while(prototypeVisit.size() < numberOfPrototypes){
				int[] prototype = createPrototype();
				if(!prototypeVisit.containsKey(prototype)){
					//Add this prototype to the visited hashtable 
					prototypeVisit.put(prototype, 0);
					for(int[] a : validActions)
						//Add this prototype to the prototype action hashtable for each action
						prototypeAction.put(new StateActionPair(prototype, a), 0.0);
				}
			}
		}
		else{//SPLITTING
			//Add new prototypes until we are full
			int[][] validActions = JSI.getROMValidActions();
			while(prototypeVisit.size() < numberOfPrototypes){
				int[] prototype = splitPrototype((int[])(prototypeVisit.keySet().toArray())[rnd.nextInt(prototypeVisit.size())]);
				if(!prototypeVisit.containsKey(prototype)){
					//Add this prototype to the visited hashtable 
					prototypeVisit.put(prototype, 0);
					for(int[] a : validActions)
						//Add this prototype to the prototype action hashtable for each action
						prototypeAction.put(new StateActionPair(prototype, a), 0.0);
				}
			}
		}
		System.out.println("SIZE OF VISIT:" + prototypeVisit.size());
		System.out.println("SIZE OF PA:" + prototypeAction.size());
	}


	/**
	 * Create prototypes at random from the given state space input
	 * @return int[] a valid set of values that represent a state
	 */
	private int[] createPrototype(){
		int[] p = new int[memspace.length];
		for(int x = 0; x < memspace.length; x++){
			p[x] = memspace[x][rnd.nextInt( memspace[x].length)];
		}
		return p;
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	private int[] splitPrototype(int[] prevState){
		int[] newState = prevState.clone();
		int UPorDown = rnd.nextInt(2);
		int variablePosition = rnd.nextInt(newState.length);
		int memSpacePosition = indexOf(memspace[variablePosition], newState[variablePosition]);
		int numberOfValues = memspace[variablePosition].length;

		if(numberOfValues == 2){
			if(memSpacePosition == 0)
				newState[variablePosition] = memspace[variablePosition][1];
			else
				newState[variablePosition] = memspace[variablePosition][0];

		}
		else if(numberOfValues > 2){
			if(memSpacePosition == 0){
				if(UPorDown == 0)
					newState[variablePosition] = memspace[variablePosition][1];
				else
					newState[variablePosition] = memspace[variablePosition][numberOfValues-1];
			}
			else if(memSpacePosition == numberOfValues-1){
				if(UPorDown == 0)
					newState[variablePosition] = memspace[variablePosition][0];
				else
					newState[variablePosition] = memspace[variablePosition][numberOfValues-2];
			}
			else{
				if(UPorDown == 0)
					newState[variablePosition] = memspace[variablePosition][memSpacePosition+1];
				else
					newState[variablePosition] = memspace[variablePosition][memSpacePosition-1];
			}
		}
		return newState;
	}

	/**
	 * Checks to see if the given state and prototype are adjacent to each other
	 * @param s a state
	 * @param p a state to compare to
	 * @return 1 if the states are adjacent, else 0
	 */
	private int isAdjacent(int[] s, int[] p){
		int count = 0;
		for(int x = 0; x < s.length; x++){
			if(s[x]!=p[x]){
				//Get the position 
				int position =  indexOf(memspace[x], s[x]);
				int numberOfValues = memspace[x].length;
				if(numberOfValues == 2){
					count++;
				}
				else if(numberOfValues > 2){
					int a = -1;
					int b = -1;
					if(position == 0){
						a = memspace[x][position +1];
						b = memspace[x][numberOfValues -1];
					}
					else if(position == numberOfValues-1){
						a = memspace[x][0];
						b = memspace[x][position -1];
					}
					else{
						a = memspace[x][position +1];
						b = memspace[x][position -1];
					}

					//Check to see if adjacent
					if(s[x] == a || s[x] == b){
						count++;
					}
					else{
						return 0;
					}
				}
			}
		}
		if(count<=1)
			return 1;
		else
			return 0;
	}

	public int indexOf(int[] a, int key) {
		int low = 0;
		int high = a.length - 1;
		while (low <= high) {
			int mid = low + (high - low) / 2;
			if      (key < a[mid]) high = mid - 1;
			else if (key > a[mid]) low = mid + 1;
			else return mid;
		}
		return -1;
	}

	/*
	 * Reduces the randomness value as episodes take place
	 */
	private double doNormalReduction(int x, Double D){
		if(x>=(episodesToRun/2))
			return 0;
		else if(x%10 == 0 && x >=10)
			return (0.9/Math.floor(x/10.0));
		else
			return D;
	}

	/**
	 * Returns the best action for the given state s of the ROM
	 *      Will give priority to any (s,action) pair that still has
	 *      a 0 value as it has not been tried yet
	 */
	private int[] getBestActionByState(int[] s) {
		int[] bestActions = null;
		double bestScore = Double.MIN_VALUE;
		double curScore = 0;

		int[][] possibleActions = JSI.getROMValidActions();
		for(int[] a : possibleActions){
			if(prototypeAction.containsKey(new StateActionPair(s,a))){
				curScore = prototypeAction.get(new StateActionPair(s,a));
				if(curScore == 0){
					bestActions = a;
					break;
				}
				else if(curScore > bestScore){
					bestScore = curScore;
					bestActions = a;
				}
			}
		}
		if(bestActions == null){
			return getRandomAction();
		}
		return bestActions;
	}

	/**
	 * Returns a random action from the possible actions for the ROM
	 */
	private int[] getRandomAction() {
		int[][] Actions = JSI.getROMValidActions();
		int rnd = (int) (Math.random()*Actions.length);
		return Actions[rnd];
	}
}

