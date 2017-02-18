package jstella.learning;
import java.io.File;

public class Agent {

	private JSILearning JSI;
	private Game GME;

	public Agent(JSILearning J, Game G){
		JSI = J;
		GME = G;
		JSI.setSoundEnabled(false);
		//	JSI.setKeysEnabled(false);
		//	JSI.setDelayNTSC(1);
		JSI.setLearningSpeed(30);
		JSI.loadROM(new File("SRC\\ROM\\PacMan.bin"));
	}

	public int[] getAction() {

		int[] actions = new int[3];
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
		//System.out.println(GME.getLives());
		
		JSI.getGameImage(4,2);

		//System.out.println(GME.getScore());

		//for(String t : x){
		//	System.out.print(t + ", ");
		//}
		//System.out.println();

		return actions;
	}

}

