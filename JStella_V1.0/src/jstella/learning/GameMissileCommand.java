package jstella.learning;


public class GameMissileCommand implements JSIGame {

	JSILearning JSI;

	public GameMissileCommand(JSILearning J){
		JSI = J;
	}

	public int getScore() {
		try{
			String highBits =  JSI.getMemoryHex()[115];
			String middleBits = JSI.getMemoryHex()[113];
			String lowBits = JSI.getMemoryHex()[111];

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
		int x = 0;
		if(JSI.getMemory()[41] == 56)
			x++;
		if(JSI.getMemory()[43] == 56)
			x++;
		if(JSI.getMemory()[45] == 56)
			x++;
		if(JSI.getMemory()[47] == 56)
			x++;
		if(JSI.getMemory()[49] == 56)
			x++;
		if(JSI.getMemory()[51] == 56)
			x++;
		return x;
	}

	public int getLevel() {
		return JSI.getMemory()[103];
	}
}
