package jstella.learning;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class Agent {

	private JSILearning JSI;

	double gamma = 0.9,
			eps = 0.9,
			alpha = 0.9,
			lambda = 0.9;
	int episodesToRun = 20,
			episodeNumber = 0,
			currentScore = 0,
			scoreDifference = 0,
			currentLives = 0,
			liveDifference = 0,
			numberOfPrototypes = 1024;

	int counter = 0;
	boolean deadFlag = false;
	boolean prototypesMade = false;
	int[] Cstate, Pstate;

	//HashTables	
	Hashtable<StateActionPair, Double> stateAction = new Hashtable<StateActionPair, Double>();
	Hashtable<StateActionPair,  Double> prototypeAction = new Hashtable<StateActionPair, Double>();
	Hashtable<int[], Integer> prototypeVisit = new Hashtable<int[], Integer>();


	//FOR TESTING VARIABLE SPACE
	int[][] memspace = new int[128][256];

	public Agent(JSILearning J){	
		/**
		 * Set JSILearning Object
		 */
		JSI = J;

		/**
		 * Boolean Flags To Set
		 */
		JSI.setSoundEnabled(false); //Default Value : true
		//JSI.setKeysEnabled(false); //Default Value : true
		//JSI.setVideoEnabled(false);  //Default Value : true

		/**
		 * Variables Related To Emulation
		 */
		//JSI.setFrameDelay(1); //Default Value : 17 (60 frames a second)
		JSI.setAgentCallDelay(30); //Default Value : 1 (Agent called every frame)

		/**
		 * Variables Related To Images
		 */
		//JSI.setImagesEnabled(false); //Default Value : false
		//JSI.setFramesToAverage(4); //This is the default value
		//JSI.setNumberOfFramesBetween(3); //This is the default value

		/**
		 * Select A ROM File To Play
		 */
		JSI.loadROM(new File("SRC\\ROM\\PacMan.bin"));


	}

	public int[] getAction() {
		int[] actions = new int[3];
		//YOUR CODE STARTS HERE


		int[] mem = JSI.getMemory();
		for(int x = 0; x < mem.length; x++){
			memspace[x][mem[x]] = (memspace[x][mem[x]])+1;
		}
		
		try{
			PrintWriter writer = new PrintWriter(new FileWriter("memstate.txt"));
			for(int x = 0; x < memspace.length; x++){
				writer.write("Memory Record " + x + ": ");
				for(int y = 0; y < memspace[x].length; y++){
					writer.write(memspace[x][y] + ", ");
				}
				writer.write("\n");
			}			
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
		}


		/*
		if(!prototypesMade){
			int[][] Acts = JSI.getROMValidActions();
			for(int m = 0; m < numberOfPrototypes; m++){
				int[] prototype = createPrototype();
				prototypeVisit.put(prototype, 0);
				for(int[] a : Acts)
					prototypeAction.put(new StateActionPair(prototype, a), 0.0);
			}
			prototypesMade =true;
		}

		if(episodeNumber==-1){//if we are done follow only the best policy
			int[] state = JSI.getROMState();
			actions = getBestActionByState(state);//choose best action
			return actions;
		}
		else if(episodeNumber != -1 && episodeNumber < episodesToRun && deadFlag){//Reset everything at the end of the episode
			episodeNumber++;
			eps = doNormalReduction(episodeNumber,eps);
			deadFlag = false;
			Pstate = null;
			Cstate = null;

			updatePrototypes();

			System.out.println("RESET:: MOVING TO EPISODE: " + episodeNumber + "\n");
			JSI.reset();
			JSI.loadROM(new File("SRC\\ROM\\Edge.bin"));
			actions = getRandomActionByState();
			return actions;
		}
		else if(episodeNumber >= episodesToRun){//If we have finished reset the system so we can watch the final policy
			JSI.setFrameDelay(17);
			JSI.loadROM(new File("SRC\\ROM\\Edge.bin"));
			JSI.reset();
			episodeNumber = -1;
			try{
				PrintWriter writer = new PrintWriter(new FileWriter("StateActionTable.txt"));

				writer.write("All KEYS::\n");
				Set<StateActionPair> keys = stateAction.keySet();
				for(StateActionPair key: keys)
					writer.write("Value of "+key+" is: "+stateAction.get(key)+"\n");				
				writer.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		else{

			//Step Reward
			double stepReward = -1;
			if(Cstate != null){
				Pstate = Cstate.clone();
			}
			else{
				Cstate = JSI.getROMState();
				return new int[]{38};//112 is F1, 32 is Space
			}
			Cstate = JSI.getROMState();

			//Find the Score Difference to see if we gained points
			scoreDifference = (JSI.getROMScore() - currentScore);
			currentScore = JSI.getROMScore();

			//Find the live difference to see if we lost a life
			liveDifference = JSI.getROMLives() - currentLives;
			currentLives = JSI.getROMLives();

			//OPTIONS--------------------------------
			//OPTIONS FOR ENDING RUN EARLY
			counter++;
			if(counter>20000){
				deadFlag =true;
				counter = 0;
			}

			if(!JSI.getROMAliveStatus()){
				deadFlag = true;
				counter = 0;
			}

			//OPTIONS FOR GIVING POSITIVE OR NEGATIVE REWARD
			if(scoreDifference > 0 )
				stepReward = scoreDifference*10;

			if(scoreDifference < 0 )
				stepReward = -100;
			//END OPTIONS----------------------------

			//TEMP
			StateActionPair sap = new StateActionPair(Pstate,actions);
			//If this StateActionPair is new, add it with unknown reward
			if(!stateAction.containsKey(sap)){
				stateAction.put(sap, 0.0);
			}
			//Get the current reward value of this StateActionPair
			double currR = stateAction.get(sap);

			//find Max Q(s',a')
			actions = getBestActionByState(Pstate);
			double maxR = 0.0;
			if(stateAction.containsKey(new StateActionPair(Pstate,actions)))
				maxR = stateAction.get(new StateActionPair(Pstate,actions));
			double e = currR + alpha * (stepReward + gamma * (maxR - currR));				
			stateAction.replace(sap, e);

			//Set next action a', chosen E-greedily based on Q(s',a')
			double r = Math.random();
			if(r<=eps)
				actions = getRandomActionByState();//choose random action
			else
				actions = getBestActionByState(Cstate);//choose best action
			//TEMP 

			StateActionPair sap = new StateActionPair(Pstate,actions);
			if(!stateAction.containsKey(sap)){
				stateAction.put(sap, 0.0);
			}
			double currR = stateAction.get(sap);
			actions = getBestActionByState(Pstate);
			double maxR = 0.0;
			if(stateAction.containsKey(new StateActionPair(Pstate,actions)))
				maxR = stateAction.get(new StateActionPair(Pstate,actions));

			double sume  = 0.0;
			for(StateActionPair s : prototypeAction.keySet()){

				if(isAdjacent(s.getState(),Cstate)==1)
					if( prototypeVisit.containsKey(s.getState()))
						prototypeVisit.replace(s.getState(), prototypeVisit.get(s.getState())+1);
					else
						prototypeVisit.put(s.getState(), 1);


				double O = prototypeAction.get(s);
				double e = O + isAdjacent(s.getState(),Cstate) * alpha * (stepReward + gamma * (maxR - currR));				
				prototypeAction.replace(s, e);

				//Replace sap with the sumation
				sume = prototypeAction.get(s)*isAdjacent(sap.getState(),Cstate);
			}
			stateAction.replace(sap, sume);

			//Set next action a', chosen E-greedily based on Q(s',a')
			double r = Math.random();
			if(r<=eps)
				actions = getRandomActionByState();//choose random action
			else
				actions = getBestActionByState(Cstate);//choose best action

		}
		 */


		//YOUR CODE ENDS HERE
		return actions;
	}

	private void updatePrototypes(){
		if(episodeNumber%5==0){
			for(StateActionPair s : prototypeAction.keySet()){
				if(prototypeAction.get(s) == 0){
					prototypeVisit.remove(s.getState());

				}
				else
				{
					System.out.println(prototypeAction.get(s));
				}

			}
		}


		int[][] Acts = JSI.getROMValidActions();

		while(prototypeAction.keySet().size()< numberOfPrototypes){
			int[] prototype = createPrototype();
			prototypeVisit.put(prototype, 0);
			for(int[] a : Acts)
				prototypeAction.put(new StateActionPair(prototype, a), 0.0);

		}
	}


	/*
	 * 
	 */
	private int[] createPrototype(){
		int[] stateSeed = JSI.getROMState();
		int[] p = new int[JSI.getROMState().length];
		for(int x = 0; x < p.length; x++)
			p[x] = (int) (Math.random()*256);
		return p;
	}

	/*
	 * 
	 */
	private int[] splitPrototype(int[] p){
		int[] s = p.clone();
		int x = ((int)Math.random()*129);
		if(((int)(Math.random()*2))==0)
			s[x] = s[x]+1;
		else
			s[x] = s[x]-1;
		return s;
	}

	/*
	 * Checks to see if the given state and prototype are adjacent to eachother
	 */
	private int isAdjacent(int[] s, int[] p){
		int count = 0;
		for(int x = 0; x < s.length; x++){
			if(s[x]==p[x]){

			}
			else{
				if(s[x] == p[x]+1 || s[x] == p[x]-1){
					count++;
				}
				else{
					return 0;
				}
			}
		}
		if(count<=1)
			return 1;
		else
			return 0;
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

	/*
	 * Returns the best action for the given state of the ROM
	 */
	private int[] getBestActionByState(int[] S) {
		int[] bestAct = null;
		double bestScore = -1;
		double curScore = 0;

		int[][] Actions = JSI.getROMValidActions();
		for(int[] x : Actions){
			if(stateAction.containsKey(new StateActionPair(S,x))){
				curScore = stateAction.get(new StateActionPair(S,x));
				if(curScore > bestScore){
					bestScore = curScore;
					bestAct = x;
				}
			}
		}
		if(bestAct == null){
			return getRandomActionByState();
		}
		return bestAct;
	}

	/*
	 * Returns a random action from the possible actoins for the ROM
	 */
	private int[] getRandomActionByState() {
		int[][] Actions = JSI.getROMValidActions();
		int rnd = (int) (Math.random()*Actions.length);
		return Actions[rnd];
	}

}

