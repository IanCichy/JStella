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
	private JSVideo JSV;
	private JSAudio JSA;

	//Variables from JSLearning
	private Agent agent;
	private GamePacMan game;

	//Variables for timing
	private Timer tmr;

	//Variables for images
	int imageCounter;
	private int framesToAverage;
	private int numberOfFramesBetween;
	private ArrayList<BufferedImage> gameImages;
	private BufferedImage image;
	private boolean imagesEnabled;

	public JSILearning(JStellaMain J){
		JSM = J;
		JSI = JSM.getIntercessor();
		IPM = JSI.getInputMaster();
		JSC = JSI.getConsole();
		JSL = JSC.getController(Jack.LEFT);
		JSR = JSC.getRiot();
		JSV = JSC.getVideo();
		JSA = JSC.getAudio();

		//Set JSI in the console
		JSC.setJSILearning(this);

		//Create the Timer
		tmr = new Timer();

		//Initialize image variables
		imageCounter = 1;
		framesToAverage = 4;
		numberOfFramesBetween = 2;
		gameImages = new ArrayList<BufferedImage>();
		image = null;
		imagesEnabled = false;

		//Add Agent and Game
		game = new GamePacMan(this);
		agent = new Agent(this);

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
		if(imagesEnabled){
			if(imageCounter % numberOfFramesBetween == 0){
				getImagePart();
				imageCounter = 1;
				if(gameImages.size() == framesToAverage)
					combineGameImage();
			}
			imageCounter++;
		}

		//Main Call to Agent to get Actions
		int[] actions = agent.getAction();
		//Send those Actions to the game
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
	public BufferedImage getGameImage(){
		if(imagesEnabled)
			return image;
		else
			return null;
	}


	public void getImagePart(){
		try{
			while(gameImages.size() >= framesToAverage){
				gameImages.remove(0);
			}
			gameImages.add(copyImageBuffer(JSV.getBackBuffer()));
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	private void combineGameImage(){
		try{
			//Used for debugging testing   ----------
			/*
			int test = 0;
			for(BufferedImage B : gameImages){
				File out = new File("Image" + test + ".png");
				ImageIO.write(B, "png", out);
				test++;
			}
			 */
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
			/*
			File outputfile = new File("out.png");
			ImageIO.write(out, "png", outputfile);
			 */
			//End debugging testing   ----------
			image = out;
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
	 * ----setNumberOfFramesBetween----
	 * Sets the delay in frames between two image captures
	 * @param int framesBetween: Number of frames (must be in the range 1 to 10 inclusive)
	 */
	public void setNumberOfFramesBetween(int framesBetween){
		if(framesBetween >= 1 && framesBetween <= 10)
			numberOfFramesBetween = framesBetween;
		else
			throw new IllegalArgumentException();
	}

	/**
	 * ----getNumberOfFramesBetween----
	 * Gets the delay in frames between two image captures
	 * @return int: returns an integer as a number of frames
	 */
	public int getNumberOfFramesBetween(){
		return numberOfFramesBetween;
	}

	/**
	 * ----setFramesToAverage----
	 * Sets the total number of frames to captures
	 * @param int numFrames: Number of frames (must be in the range 1 to 8 inclusive)
	 */
	public void setFramesToAverage(int numFrames){
		if(numFrames >= 1 && numFrames <= 8)
			framesToAverage = numFrames;
		else
			throw new IllegalArgumentException();
	}

	/**
	 * ----getFramesToAverage----
	 * Gets the number of total frames
	 * @return int: returns an integer as a number of frames to average
	 */
	public int getFramesToAverage(){
		return framesToAverage;
	}

	/**
	 * ----setSoundEnabled----
	 * Sets the sound (on/off) with (true/false)
	 * @param boolean aSoundEnabled: set flag to value
	 */
	public void setSoundEnabled(boolean aSoundEnabled){
		JSA.setSoundEnabled(aSoundEnabled);
	}

	/**
	 * ----setVideoEnabled----
	 * Sets the video (on/off) with (true/false)
	 * @param boolean aVideoEnabled: set flag to value
	 */
	public void setVideoEnabled(boolean aVideoEnabled){
		JSV.setVideoEnabled(aVideoEnabled);
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
	 * ----setImagesEnabled----
	 * Sets the constant image building (on/off) with (true/false)
	 * @param boolean aImagesEnabled: set flag to value
	 */
	public void setImagesEnabled(boolean aImagesEnabled) {
		imagesEnabled = aImagesEnabled;
	}

	/**
	 * ----setFrameDelay----
	 * Sets the Frame Delay (time in between each frame) (in milliseconds)
	 * @param int ntscDelay: sets the delay to the given time
	 *      by stopping the emulation timer, setting a new delay,
	 *      resetting the system, and finally starting the timer again
	 */
	public void setFrameDelay(int ntscDelay){
		JSI.stopTimer();
		JSI.setDelayNTSC(ntscDelay);
		JSI.emulateResetPress();
		JSI.startTimer();
	}

	/**
	 * ----getFrameDelay----
	 * Gets the current Frame Delay (time in between each frame)
	 * @return int: returns an integer that represents an amount of milliseconds
	 */
	public int getFrameDelay(){
		return JSI.getDelayNTSC();
	}

	/**
	 * ----setLearningSpeed----
	 * Sets the learning speed
	 * @param int myLearningSpeed: sets the learning speed of the Agent as how many calls
	 *      it will receive. (0 = no calls, 1 = 1 call every frame, n = 1 call every n frames)
	 */
	public void setAgentCallDelay(int myDelay){
		JSC.setMyAgentCallDelay(myDelay);
	}

	/**
	 * ----getLearningSpeed----
	 * Gets the current learning speed
	 * @return int: returns an integer that represents an amount of calls the Agent will receive
	 *      compared to the number of frames.  (0 = no calls, 1 = 1 call every frame, n = 1 call every n frames) 
	 */
	public int getAgentCallDelay(){
		return JSC.getMyAgentCallDelay();
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

	/**
	 * ----reset----
	 * Will reset the ROM cartridge to the initial state
	 */
	public void reset(){
		JSI.emulateResetPress();
	}




	//-----------------------------------------------------------------------------------------------------------------------------------------
	//------------------------------------------------------  _____     ____    __  __  -------------------------------------------------------
	//------------------------------------------------------ |  __ \   / __ \  |  \/  | -------------------------------------------------------
	//------------------------------------------------------ | |__) | | |  | | | \  / | -------------------------------------------------------
	//------------------------------------------------------ |  _  /  | |  | | | |\/| | -------------------------------------------------------
	//------------------------------------------------------ | | \ \  | |__| | | |  | | -------------------------------------------------------
	//------------------------------------------------------ |_|  \_\  \____/  |_|  |_| -------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------------------


	public int getScore(){
		return game.getScore();
	}

	public int getLives(){
		return game.getLives();
	}
	
	public int getLevel(){
		return game.getLevel();
	}

	public int[] getValidActions(){
		return game.getValidActions();
	}

	public String getName(){
		return game.getName();
	}

}









