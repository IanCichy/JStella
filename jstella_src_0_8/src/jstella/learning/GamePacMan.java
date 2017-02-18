package jstella.learning;
import java.awt.event.KeyEvent;

public class GamePacMan implements JSIGame {

	JSILearning JSI;

	public GamePacMan(JSILearning J){
		JSI = J;
	}

	public int getScore() {
		try{
			String highBits =  JSI.getMemoryAsHex()[80];
			String middleBits = JSI.getMemoryAsHex()[78];
			String lowBits = JSI.getMemoryAsHex()[76];

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
		return JSI.getMemory()[24];
	}

	public int getLevel() {//WIP
		JSI.getMemory();
		return 0;
	}
	
	public boolean getAliveStatus(){
		return JSI.getMemory()[2]==0;
	}

	public int[][] getValidActions() {
		return new int[][] {
			//{0},
			{KeyEvent.VK_UP},
			{KeyEvent.VK_DOWN},
			{KeyEvent.VK_LEFT},
			{KeyEvent.VK_RIGHT}
			//{KeyEvent.VK_UP, KeyEvent.VK_LEFT},
			//{KeyEvent.VK_UP, KeyEvent.VK_RIGHT},
			//{KeyEvent.VK_DOWN, KeyEvent.VK_LEFT},
			//{KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT}
		};
	}

	@Override
	public int[] getState() {
		return JSI.getMemory();
	}
}
