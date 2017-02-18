package jstella.learning;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;

public class Agent {

	JSILearning JSI;
	Robot rbot;

	int i = 0;

	public Agent(JSILearning J){
		JSI = J;
		try {
			rbot = new Robot();
		} catch (AWTException e) {}
		JSI.setSoundEnabled(false);
		JSI.loadROM(new File("SRC\\ROM\\Pac-Man.bin"));
	}

	public void doAction() {
		//System.out.println("test" + i);
		//System.out.println(J.getMemory());
		//i++;
		int rnd = (int)(Math.random()*5);
		if(rnd ==0){
			rbot.keyPress(KeyEvent.VK_RIGHT);
			rbot.delay(16);
			rbot.keyRelease(KeyEvent.VK_RIGHT);
		}
		if(rnd ==1){
			rbot.keyPress(KeyEvent.VK_LEFT);
			rbot.delay(16);
			rbot.keyRelease(KeyEvent.VK_LEFT);	
		}
		if(rnd ==2){
			rbot.keyPress(KeyEvent.VK_UP);
			rbot.delay(16);
			rbot.keyRelease(KeyEvent.VK_UP);	
		}
		if(rnd ==3){
			rbot.keyPress(KeyEvent.VK_DOWN);
			rbot.delay(16);
			rbot.keyRelease(KeyEvent.VK_DOWN);	
		}
		if(rnd == 4){
			rbot.keyPress(KeyEvent.VK_SPACE);
			rbot.delay(16);
			rbot.keyRelease(KeyEvent.VK_SPACE);	
		}


	}

}

