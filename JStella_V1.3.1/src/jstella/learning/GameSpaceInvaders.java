package jstella.learning;

import java.awt.event.KeyEvent;

public class GameSpaceInvaders implements JSIGame {

	JSILearning JSI;

	public GameSpaceInvaders(JSILearning J){
		JSI = J;
	}

	public int getScore() {
		try{
			String highBits =  JSI.getMemoryAsHex()[102];
			String lowBits = JSI.getMemoryAsHex()[104];

			String value = highBits + "" + lowBits;
			return Integer.parseInt(value);
		}
		catch(NumberFormatException e){
			return 0;
		}
	}

	public int getLives() {
		return JSI.getMemory()[73];
	}

	public int getLevel() {//WIP
		JSI.getMemory();
		return 0;
	}

	public int[][] getValidActions() {
		return new int[][] { 
			{KeyEvent.VK_LEFT},
			{KeyEvent.VK_RIGHT},
			{KeyEvent.VK_SPACE},
			{KeyEvent.VK_LEFT, KeyEvent.VK_SPACE},
			{KeyEvent.VK_RIGHT, KeyEvent.VK_SPACE}
		};
	}

	public int[] getState() {
		return JSI.getMemory();
	}

	public boolean getAliveStatus() {
		return JSI.getMemory()[71] == 0;
	}
}
