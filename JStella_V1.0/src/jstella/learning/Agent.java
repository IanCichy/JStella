package jstella.learning;
import java.io.File;

public class Agent {

	private JSILearning JSI;
	boolean test = false;

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
		JSI.loadROM(new File("SRC\\ROM\\PacMan.bin"));
	}

	public int[] getAction() {
		int[] actions = new int[3];
		//YOUR CODE STARTS HERE
		
		
		//YOUR CODE ENDS
		return actions;
	}

}

