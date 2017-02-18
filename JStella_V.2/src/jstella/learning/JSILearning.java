package jstella.learning;

import java.awt.Color;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import jstella.cart.Cartridge;
import jstella.core.JSAudio;
import jstella.core.JSConsole;
import jstella.core.JSRiot;
import jstella.core.JSSystem;
import jstella.core.JSTIA;
import jstella.core.JSVideo;
import jstella.runner.Intercessor;
import jstella.runner.JStellaMain;
import static jstella.core.JSConstants.*;

public class JSILearning {

	Timer tmr;

	private JStellaMain JSM;
	private JSRiot JSR;
	private JSTIA JST;
	private Intercessor JSI;
	private JSVideo JSV;
	private JSAudio JSA;
	private JSSystem JSS;
	private JSConsole JSC;
	private Cartridge CRT;
	private final int FrameTime = 17;
	
	private Agent AGT;

	public JSILearning(JStellaMain J){
		JSM = J;
		JSI = JSM.getIntercessor();
		JSC = JSI.getConsole();
		JSR = JSC.getRiot();
		JST = JSC.getTIA();
		CRT = JSC.getCartridge();
		JSV = JSC.getVideo();
		JSA = JSC.getAudio();
		JSS = JSC.getSystem();

		JSC.setJSILearning(this);
		
		AGT = new Agent(this);
		
		
		//tmr = new Timer();
		//tmr.scheduleAtFixedRate(new RemindTask(this), 1000, 1000);

		//setSoundEnabled(false);
		//loadROM(new File("SRC\\ROM\\Pac-Man.bin"));
	}
	
	public void doLearning(){
		
		
		
		AGT.doAction();
	}

	/**
	 * ----getGameImgae----
	 * Returns a BufferedImage which is built as a composite
	 * of multiple images taken separated by a number of game frames.
	 * 
	 * @param int FramesToAverage: An integer that decides how many BufferedImages
	 *      to take to be combined (1 - 12 inclusive)
	 * @param int NumberOfFramesBetween: An integer that decides how many game frames to wait
	 *      between taking BufferedImgaes. There are 60 frames a second so this number is multiplied
	 *      by 17 milliseconds (FrameTime) to separate out frames. (0 - 12 inclusive)
	 * @return BufferedImage: This image is a composite built out of all the images taken.
	 *      The final image will retain pixel color at a location if it is identical in all images
	 *      and will change it to a new highlight color if it differs in any image when all are compared
	 */
	public BufferedImage getGameImage(int FramesToAverage, int NumberOfFramesBetween){
		try{
			if(FramesToAverage < 1 || FramesToAverage >12)
				return null;
			if(NumberOfFramesBetween < 0 || NumberOfFramesBetween >12)
				return null;

			//Robot Class is used to call delay
			Robot r = new Robot();
			ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

			//Take all images waiting for the specified number of frames in-between
			for(int x = 0; x < FramesToAverage; x ++){
				//images.add(copyData(JSI.getMyCanvas().getMyImage()));
				images.add(copyImageBuffer(JSV.getBackBuffer()));
				r.delay(NumberOfFramesBetween * FrameTime);
			}

			//Used for debugging testing   ----------
			int test = 0;
			for(BufferedImage B : images){
				File out = new File("Image" + test + ".png");
				ImageIO.write(B, "png", out);
				test++;
			}
			//End debugging testing   ----------

			BufferedImage out = new BufferedImage(images.get(0).getWidth(), images.get(0).getHeight(), BufferedImage.TYPE_INT_ARGB);
			//Loop through all pixel locations
			for(int x = 0; x < out.getWidth(); x++){
				for(int y = 0; y < out.getHeight(); y++){
					//Loop through all images comparing the pixel location
					boolean same = true;
					for(int i = 0; i < images.size()-1; i++){
						if(!(images.get(i).getRGB(x, y) == images.get(i + 1).getRGB(x, y))){
							same = false;
							break;
						}
					}
					//If the pixel color is the same in all images
					//else highlight the pixel red
					if(same)
						out.setRGB(x, y, images.get(0).getRGB(x, y));
					else
						out.setRGB(x, y, Color.red.getRGB());
				}
			}

			//Used for debugging testing   ----------
			File outputfile = new File("out.png");
			ImageIO.write(out, "png", outputfile);
			//End debugging testing   ----------
			return out;
		}
		catch(Exception e){
			System.out.println(e);
			return null;
		}
	}

	private BufferedImage copyImageBuffer(BufferedImage C){
		BufferedImage ret = new BufferedImage(C.getWidth(), C.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < C.getWidth(); x++){
			for(int y = 0; y < C.getHeight(); y++){
				ret.setRGB(x, y, C.getRGB(x, y));
			}
		}
		return ret;
	}

	public String getRegisterValue(int i) {
		String reg = Integer.toBinaryString(i);
		for (int x = reg.length(); x < 8; x++) {
			reg = "0" + reg;
		}
		return reg;
	}

	public int[] getMemory(){
		return JSR.getMyRAM();
	}

	public void setSoundEnabled(boolean soundEnabled){
		JSA.setSoundEnabled(soundEnabled);
	}

	public void setVideoEnabled(boolean videoEnabled){
		JSV.setVideoEnabled(videoEnabled);
	}

	public void loadROM(File aROMFile){
		try {
			JSM.loadROM(aROMFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



class RemindTask extends TimerTask {

	JSILearning JSIL;

	public RemindTask(JSILearning L) {
		JSIL = L;
	}

	public void run() {

		//JSIL.getMyRAM();
		//JSIL.getBackBuff();
		//BufferedImage ig = JSIL.getGameImage(6, 2);
		//System.out.println(ig);

	}
}









