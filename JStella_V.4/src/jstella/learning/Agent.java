package jstella.learning;
import java.awt.image.BufferedImage;
import java.io.File;

public class Agent {

	private JSILearning JSI;

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
		BufferedImage gm = JSI.getGameImage();
		/*
		int rnd = (int)(Math.random()*9);
		if(rnd == 0)
			actions[0] = KeyEvent.VK_RIGHT;
		else if(rnd==1)
			actions[0] = KeyEvent.VK_LEFT;
		else if(rnd==2)
			actions[0] = KeyEvent.VK_UP;
		else if(rnd==3)
			actions[0] = KeyEvent.VK_DOWN;
		else if(rnd==4)
			actions[0] = KeyEvent.VK_SPACE;
		else if(rnd==5){
			actions[0] = KeyEvent.VK_LEFT;
			actions[1] = KeyEvent.VK_UP;
		}
		else if(rnd==6){
			actions[0] = KeyEvent.VK_LEFT;
			actions[1] = KeyEvent.VK_DOWN;
		}
		else if(rnd==7){
			actions[0] = KeyEvent.VK_RIGHT;
			actions[1] = KeyEvent.VK_UP;
		}
		else if(rnd==8){
			actions[0] = KeyEvent.VK_RIGHT;
			actions[1] = KeyEvent.VK_DOWN;
		}

		 */
		//int[] dfg = JSI.getMemory();
		//String[] x = JSI.getMemoryHex();
		//System.out.println("Lives:: " + GME.getLives());
		//JSI.getGameImage(4,2);
		//System.out.println("Score:: " + GME.getScore());
		
		//if(JSI.getScore() > 20){

		//}

		//for(String t : x){
		//	System.out.print(t + ", ");
		//}
		//System.out.println();

		return actions;
	}

}

