package jstella.learning;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import jstella.core.JSAudio;
import jstella.core.JSConsole;
import jstella.core.JSConstants.Jack;
import jstella.core.JSController;
import jstella.core.JSRiot;
import jstella.core.JSSystem;
import jstella.core.JSTIA;
import jstella.core.JSVideo;
import jstella.runner.InputMaster;
import jstella.runner.Intercessor;
import jstella.runner.JStellaMain;
import static jstella.core.JSConstants.*;

public class JSILearning {

	//Variables from JStella
	private JStellaMain JSM;
	private Intercessor JSI;
	private InputMaster IPM;
	private JSConsole JSC;
	private JSController JSL;
	private JSRiot JSR;
	//private JSTIA JST;
	private JSVideo JSV;
	private JSAudio JSA;
	//private JSSystem JSS;

	//Variables from JSLearning
	private Agent agent;
	private Game game;

	//Variables for timing
	private Timer tmr;

	public JSILearning(JStellaMain J){
		JSM = J;
		JSI = JSM.getIntercessor();
		IPM = JSI.getInputMaster();
		JSC = JSI.getConsole();
		JSL = JSC.getController(Jack.LEFT);
		JSR = JSC.getRiot();
		//JST = JSC.getTIA();
		JSV = JSC.getVideo();
		JSA = JSC.getAudio();
		//JSS = JSC.getSystem();

		JSC.setJSILearning(this);

		tmr = new Timer();

		game = new Game(this);
		agent = new Agent(this, game);

	}

	/**
	 * ----doLearning----
	 * Main loop that is called every time the doFrame() method is run in the JSConsole Class
	 * 
	 * Takes care of asking the specified agent for an instruction at the specified rate in milliseconds
	 * Also sends any input received directly to the InputMaster class by setting the associated pin to a high state
	 * 
	 */
	public void doLearning(){
		int[] actions = agent.getAction();
		for(int x : actions)
			sendInput(x);
	}

	/**
	 * ----sendInput----
	 * Processes any given input command and sends it to the InputMaster class directly
	 * 
	 * @param int aVKCode: A key code that corresponds to a "key press" given from the Agent
	 *     and based on that code will set the corresponding input pin to the high state.
	 *     The Digital pins are {1,2,3,4,6} (Refer to JSConstants and further Atari Documentation).
	 *     If the pin is already high no change will happen, else the pin will be set high and 
	 *     an event will be triggered to stop the key press one NTSC Delay later 
	 *     (NTSC Delay corresponds to a single frame length)
	 */
	public void sendInput(int aVKCode){

		//Digital Pin One : UP
		if((JSL.read(DigitalPin.One)) && KeyEvent.VK_UP == aVKCode){
			IPM.processInputKeyEvent(aVKCode, true);
			tmr.schedule(new TimerTask() {
				public void run() {
					IPM.processInputKeyEvent(aVKCode, false);
				}
			}, JSI.getDelayNTSC());
		}
		//Digital Pin Two : DOWN
		if((JSL.read(DigitalPin.Two)) && KeyEvent.VK_DOWN == aVKCode){
			IPM.processInputKeyEvent(aVKCode, true);
			tmr.schedule(new TimerTask() {
				public void run() {
					IPM.processInputKeyEvent(aVKCode, false);
				}
			}, JSI.getDelayNTSC());
		}
		//Digital Pin Three : LEFT
		if((JSL.read(DigitalPin.Three)) && KeyEvent.VK_LEFT == aVKCode){
			IPM.processInputKeyEvent(aVKCode, true);
			tmr.schedule(new TimerTask() {
				public void run() {
					IPM.processInputKeyEvent(aVKCode, false);
				}
			}, JSI.getDelayNTSC());
		}
		//Digital Pin Four : RIGHT
		if((JSL.read(DigitalPin.Four)) && KeyEvent.VK_RIGHT == aVKCode){
			IPM.processInputKeyEvent(aVKCode, true);
			tmr.schedule(new TimerTask() {
				public void run() {
					IPM.processInputKeyEvent(aVKCode, false);
				}
			}, JSI.getDelayNTSC());
		}
		//Digital Pin Six : SPACE
		if((JSL.read(DigitalPin.Six)) && KeyEvent.VK_SPACE == aVKCode){
			IPM.processInputKeyEvent(aVKCode, true);
			tmr.schedule(new TimerTask() {
				public void run() {
					IPM.processInputKeyEvent(aVKCode, false);
				}
			}, JSI.getDelayNTSC());
		}
	}


	/**
	 * ----getGameImgae----
	 * Returns a BufferedImage which is built as a composite
	 * of multiple images taken separated by a number of game frames.
	 * 
	 * @param int FramesToAverage: An integer that decides how many BufferedImages
	 *      to take to be combined 
	 * @param int NumberOfFramesBetween: An integer that decides how many game frames to wait
	 *      between taking BufferedImgaes. There are 60 frames a second so this number is multiplied
	 *      by the DelayNTSC (FrameTime) to separate out frames. 
	 * @return BufferedImage: This image is a composite built out of all the images taken.
	 *      The final image will retain pixel color at a location if it is identical in all images
	 *      and will change it to a new highlight color if it differs in any image when all are compared
	 */
	public void getGameImage(int FramesToAverage, int NumberOfFramesBetween){
		try{
			/*
			if(FramesToAverage < 1 || FramesToAverage >6)
				return null;
			if(NumberOfFramesBetween < 0 || NumberOfFramesBetween >10)
				return null;
			 */

			ArrayList<BufferedImage> gameImages = new ArrayList<BufferedImage>();

			//Take all images waiting for the specified number of frames in-between
			for(int x = 0; x < FramesToAverage; x ++){
				tmr.schedule(new TimerTask() {
					public void run() {
						//BufferedImages have no built in Clone method so it must be custom
						gameImages.add(copyImageBuffer(JSV.getBackBuffer()));
					}
				}, ((NumberOfFramesBetween * JSI.getDelayNTSC()) * x));
			}

			tmr.schedule(new TimerTask() {
				public void run() {
					combineGameImage(gameImages);
				}
			}, ((NumberOfFramesBetween * JSI.getDelayNTSC()) * FramesToAverage));
		}
		catch(Exception e){
			System.out.println(e);
		}
	}


	private void combineGameImage(ArrayList<BufferedImage> gameImages){
		try{
			//Used for debugging testing   ----------
			int test = 0;
			for(BufferedImage B : gameImages){
				File out = new File("Image" + test + ".png");
				ImageIO.write(B, "png", out);
				test++;
			}
			//End debugging testing   ----------

			BufferedImage out = new BufferedImage(gameImages.get(0).getWidth(), gameImages.get(0).getHeight(), BufferedImage.TYPE_INT_ARGB);
			//Loop through all pixel locations
			for(int x = 0; x < out.getWidth(); x++){
				for(int y = 0; y < out.getHeight(); y++){
					//Loop through all images comparing the pixel location
					boolean same = true;
					for(int i = 0; i < gameImages.size()-1; i++){
						if(!(gameImages.get(i).getRGB(x, y) == gameImages.get(i + 1).getRGB(x, y))){
							same = false;
							break;
						}
					}
					//If the pixel color is the same in all images
					//else highlight the pixel red
					if(same)
						out.setRGB(x, y, gameImages.get(0).getRGB(x, y));
					else
						out.setRGB(x, y, Color.red.getRGB());
				}
			}

			//Used for debugging testing   ----------
			File outputfile = new File("out.png");
			ImageIO.write(out, "png", outputfile);
			//End debugging testing   ----------
		}
		catch(Exception e){}
	}

	/**
	 * ----copyImageBuffer----
	 * Returns a BufferedImage which is a clone of the BufferedImage
	 * sent to it as the BufferedImage type has no built in clone functionality
	 * @param BufferedImage toCopy: A BufferedImage to be cloned
	 * @return BufferedImage: This image is a composite built out of all the images taken.
	 *      The final image will retain pixel color at a location if it is identical in all images
	 *      and will change it to a new highlight color if it differs in any image when all are compared
	 */
	private BufferedImage copyImageBuffer(BufferedImage toClone){
		/*
		BufferedImage ret = new BufferedImage(toClone.getWidth(), toClone.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < toClone.getWidth(); x++){
			for(int y = 0; y < toClone.getHeight(); y++){
				ret.setRGB(x, y, toClone.getRGB(x, y));
			}
		}
		return ret;
		*/
		ColorModel cm = toClone.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = toClone.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		
	}


	/*
	public String getRegisterValue(int i) {
		String reg = Integer.toBinaryString(i);
		for (int x = reg.length(); x < 8; x++) {
			reg = "0" + reg;
		}
		return reg;
	}
	 */

	/**
	 * ----getMemory----
	 * Gets the RAM used by the ATARI
	 * @return int[]: returns an integer array of length 128 which is the 
	 *      complete RAM space the emulator uses
	 */
	public int[] getMemory(){
		return JSR.getMyRAM().clone();
	}

	/**
	 * ----getMemoryHex----
	 * Gets the RAM used by the ATARI
	 * @return String[]: returns an String array of length 128 which is the 
	 *      complete RAM space the emulator uses where all values have been
	 *      converted to Hex values which is how data is handled in the system
	 */
	public String[] getMemoryHex(){
		int[] mem = JSR.getMyRAM().clone(); 
		String[] mems = new String[mem.length];

		for(int x = 0; x < mem.length; x++){
			mems[x] = Integer.toHexString(mem[x]);
		}
		return mems;
	}

	/**
	 * ----setSoundEnabled----
	 * Sets the sound (on/off) with (true/false)
	 * @param boolean soundEnabled: set flag to value
	 */
	public void setSoundEnabled(boolean soundEnabled){
		JSA.setSoundEnabled(soundEnabled);
	}

	/**
	 * ----setVideoEnabled----
	 * Sets the video (on/off) with (true/false)
	 * @param boolean videoEnabled: set flag to value
	 */
	public void setVideoEnabled(boolean videoEnabled){
		JSV.setVideoEnabled(videoEnabled);
	}

	/**
	 * ----setKeysEnabled----
	 * Sets the keyboard listener (on/off) with (true/false)
	 * @param boolean aKeysEnabled: set flag to value
	 */
	public void setKeysEnabled(boolean aKeysEnabled){
		IPM.setKeysEnabled(aKeysEnabled);
	}

	/**
	 * ----setDelayNTSC----
	 * Sets the NTSC Delay (in milliseconds)
	 * @param int ntscDelay: sets the delay to the given time
	 *      by stopping the emulation timer, setting a new delay,
	 *      resetting the system, and finally starting the timer again
	 */
	public void setDelayNTSC(int ntscDelay){
		JSI.stopTimer();
		JSI.setDelayNTSC(ntscDelay);
		JSI.emulateResetPress();
		JSI.startTimer();
	}

	/**
	 * ----getDelayNTSC----
	 * Gets the current NTSC delay
	 * @return int: returns an integer that represents an amount of milliseconds
	 */
	public int getDelayNTSC(){
		return JSI.getDelayNTSC();
	}

	/**
	 * ----setLearningSpeed----
	 * Sets the learning speed
	 * @param int myLearningSpeed: sets the learning speed of the Agent as how many calls
	 *      it will receive. (0 = no calls, 1 = 1 call every frame, n = 1 call every n frames)
	 */
	public void setLearningSpeed(int myLearningSpeed){
		JSC.setMyLearningSpeed(myLearningSpeed);
	}

	/**
	 * ----getLearningSpeed----
	 * Gets the current learning speed
	 * @return int: returns an integer that represents an amount of calls the Agent will receive
	 *      compared to the number of frames.  (0 = no calls, 1 = 1 call every frame, n = 1 call every n frames) 
	 */
	public int getLearningSpeed(){
		return JSC.getMyLearningSpeed();
	}

	/**
	 * ----loadROM----
	 * Sets the current ROM to be used by the system
	 * @param File aROMFile: set the current ROM
	 */
	public void loadROM(File aROMFile){
		try {
			JSM.loadROM(aROMFile);
		} catch (IOException e) {
			System.out.println("ROM Read Error or ROM Does Not Exist");
			e.printStackTrace();
		}
	}













}









