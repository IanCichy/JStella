package jstella.learning;

import java.awt.event.KeyEvent;

public class GameTesting implements JSIGame {

	JSILearning JSI;

	public GameTesting(JSILearning J){
		JSI = J;
	}

	public int getScore() {
		try{
			String highBits =  JSI.getMemoryAsHex()[19];
			String middleBits = JSI.getMemoryAsHex()[20];
			String lowBits = JSI.getMemoryAsHex()[21];

			if(highBits.length() != 2)
				highBits = "0" + highBits;
			if(middleBits.length() != 2)
				middleBits = "0" + middleBits;
			if(lowBits.length() != 2)
				lowBits = "0" + lowBits;

			String value = highBits + "" + middleBits + "" + lowBits;
			return Integer.parseInt(value);
		}
		catch(NumberFormatException e){
			return 0;
		}
	}

	public int getLives() {
		return 0;
	}

	public int getLevel() {
		return 0;
	}

	public int[] getState(){
		int[] ste = new int[2];
		ste[0] = JSI.getMemory()[0];
		ste[1] = JSI.getMemory()[5];
		return ste;
	}
	
	public int[][] getValidActions(){
		return new int[][] {
			{KeyEvent.VK_UP},
			{KeyEvent.VK_DOWN},
			{KeyEvent.VK_LEFT},
			{KeyEvent.VK_RIGHT}
		};
	}

	public boolean getAliveStatus() {
		return true;
	}
	
}
