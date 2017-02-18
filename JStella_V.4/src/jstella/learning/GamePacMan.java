package jstella.learning;
import java.awt.event.KeyEvent;

public class GamePacMan implements JSIGame {

	JSILearning JSI;

	public GamePacMan(JSILearning J){
		JSI = J;
	}

	public int getScore() {
		try{
			String highBits =  JSI.getMemoryHex()[80];
			String middleBits = JSI.getMemoryHex()[78];
			String lowBits = JSI.getMemoryHex()[76];

			if(!middleBits.equals("0")){
				for (int x = lowBits.length(); x < 2; x++) 
					lowBits = "0" + lowBits;
			}

			String value = highBits + "" + middleBits + "" + lowBits;
			return Integer.parseInt(value);
		}
		catch(NumberFormatException e){
			return 0;
		}
	}

	public int getLives() {
		//Index into Memory array at which the lives variable is held
		return JSI.getMemory()[24];
	}

	public int getLevel() {
		JSI.getMemory();
		return 0;
	}

	public String getName() {
		return "PacMan";
	}

	public int[] getValidActions() {
		return new int[] {KeyEvent.VK_UP,  KeyEvent.VK_DOWN,  KeyEvent.VK_LEFT,  KeyEvent.VK_RIGHT};
	}
}
