/*
 * JStellaApplet.java
 *
 * Created on July 28, 2007, 3:16 AM
 */



package jstella.runner;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import jstella.core.JSAudio;

import jstella.core.*;
import static jstella.core.JSConstants.*;


import jstella.cart.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
import java.net.*;
import java.io.*;


//TODO : external config settings passed as parameters
//TODO : (BIG ONE) JavaScript interface support


// Example of html using this :
// <applet archive="JStella.jar" code="jstella.runner.JStellaApplet.class"  height="400" width="480">
// <param name=ROM value="rom="svnstorymtn.bin">
//</applet>

/**
 *
 * @author J.L. Allen
 */
public class JStellaApplet extends javax.swing.JApplet implements Intercessor.IfcIntercessorClient{
    
    //It's good to change the version # every time you publish/test the applet so that you can know if
    //you are looking at the new applet or an old one that was cached.
    private final static String JSTELLA_APPLET_VERSION="0.424";
      
    private final static boolean USE_UTIL_TIMER=true;        
    
    public final static String PARAMETER_ROM="ROM";
    public final static String PARAMETER_CARTRIDGE_TYPE="CARTRIDGETYPE";
    public final static String PARAMETER_DISPLAY_HEIGHT="DISPLAYHEIGHT";
    public final static String PARAMETER_PADDLE_MODE="PADDLEMODE";
    public final static String PARAMETER_PADDLE_MOUSE_AXIS="PADDLEMOUSEAXIS";
    public final static String PARAMETER_OPTIONS_PANEL_ENABLED="OPTIONSPANELENABLED";
    
    private java.util.Map<String, String> myConfiguration=new java.util.HashMap<String, String>();
    private Intercessor myIntercessor=null; //new Intercessor(this);
    
    private JStellaOptionsApplet myOptionsApplet=null;
    
    
    private StringBuffer myDebugOutput=new StringBuffer();
    
    
    /** Initializes the applet JStellaApplet */
    public void init() {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                    initStellaApplet();
                    initHelpDialog();
                    
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        DialogHelp = new javax.swing.JDialog();
        PanelHelpSouth = new javax.swing.JPanel();
        ButtonHelpOK = new javax.swing.JButton();
        ButtonShowDebugInfo = new javax.swing.JButton();
        SPHelp = new javax.swing.JScrollPane();
        TPHelp = new javax.swing.JTextPane();
        PanelOptions = new javax.swing.JPanel();
        ButtonReset = new javax.swing.JButton();
        ButtonSelect = new javax.swing.JButton();
        ButtonHelp = new javax.swing.JButton();
        LabelPause = new javax.swing.JLabel();

        DialogHelp.setTitle("JStella - Help");
        DialogHelp.setResizable(false);
        ButtonHelpOK.setText("OK");
        ButtonHelpOK.setFocusable(false);
        ButtonHelpOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonHelpOKActionPerformed(evt);
            }
        });

        PanelHelpSouth.add(ButtonHelpOK);

        ButtonShowDebugInfo.setText("Debug info");
        ButtonShowDebugInfo.setFocusable(false);
        ButtonShowDebugInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonShowDebugInfoActionPerformed(evt);
            }
        });

        PanelHelpSouth.add(ButtonShowDebugInfo);

        DialogHelp.getContentPane().add(PanelHelpSouth, java.awt.BorderLayout.SOUTH);

        TPHelp.setBorder(null);
        TPHelp.setEditable(false);
        TPHelp.setFocusable(false);
        TPHelp.setOpaque(false);
        SPHelp.setViewportView(TPHelp);

        DialogHelp.getContentPane().add(SPHelp, java.awt.BorderLayout.CENTER);

        PanelOptions.setFocusable(false);
        ButtonReset.setText("Reset");
        ButtonReset.setDefaultCapable(false);
        ButtonReset.setFocusable(false);
        ButtonReset.setRequestFocusEnabled(false);
        ButtonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonResetActionPerformed(evt);
            }
        });

        PanelOptions.add(ButtonReset);

        ButtonSelect.setText("Select");
        ButtonSelect.setDefaultCapable(false);
        ButtonSelect.setFocusable(false);
        ButtonSelect.setRequestFocusEnabled(false);
        ButtonSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonSelectActionPerformed(evt);
            }
        });

        PanelOptions.add(ButtonSelect);

        ButtonHelp.setText("Help");
        ButtonHelp.setDefaultCapable(false);
        ButtonHelp.setFocusable(false);
        ButtonHelp.setRequestFocusEnabled(false);
        ButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonHelpActionPerformed(evt);
            }
        });

        PanelOptions.add(ButtonHelp);

        PanelOptions.add(LabelPause);

    }// </editor-fold>//GEN-END:initComponents

    private void ButtonShowDebugInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonShowDebugInfoActionPerformed
        debugOut("YStart=" + myIntercessor.getConsole().getYStart() + ", displayHeight=" + myIntercessor.getConsole().getDisplayHeight());    
        debugOut("Canvas size=" + myIntercessor.getCanvas().toString());
        
        TPHelp.setText("" + myDebugOutput.toString());
    }//GEN-LAST:event_ButtonShowDebugInfoActionPerformed

    private void ButtonHelpOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonHelpOKActionPerformed
    DialogHelp.setVisible(false);
    
    refocusKeyboard();
    }//GEN-LAST:event_ButtonHelpOKActionPerformed

    private void ButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonHelpActionPerformed
       doHelp();
     
   
     
    }//GEN-LAST:event_ButtonHelpActionPerformed
    
    private void ButtonSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonSelectActionPerformed
     doSelect();
      
    }//GEN-LAST:event_ButtonSelectActionPerformed
    
    private void ButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonResetActionPerformed
       doReset();
       
     
    }//GEN-LAST:event_ButtonResetActionPerformed
    
    private void initStellaApplet() {
        try{
        myIntercessor=new Intercessor(this);
        
        String zJavaVersion=System.getProperty("java.version");
      /*  System.out.println("Java version: " + zJavaVersion);
        System.out.println("JStella version: " + JSTELLA_VERSION);
     
        System.out.println("JStellaApplet version " + JSTELLA_APPLET_VERSION);*/
        
        debugOut("Java version: " + zJavaVersion);
        debugOut("JStella version: " + JSTELLA_VERSION);
     
        debugOut("JStellaApplet version " + JSTELLA_APPLET_VERSION);
        
        
      //  this.addKeyListener(myInputMaster.createKeyListener());
        this.addMouseListener(new AppletRefocuser());
        this.setFocusable(true);
     //   myIntercessor.setAutoPauseMode(true);
        myIntercessor.setAutoPauseMode(false);
        refocusKeyboard();
        
        
               
        
        loadParameters();
        }//end : try
        catch (Exception e)
        {
           debugOut("JStella error : caught exception - " + e.toString());
        }
       // if (myCart!=null) startTimer();
        
    }
    
    
    private void debugOut(String aLine)
    {
        myDebugOutput.append(aLine + "\n");
        System.out.println("" + aLine);
    }
    
     private void initHelpDialog()
   {
       //TODO : use html, with a hyperlink for the html
       
       //Step 1: set attributes for the text
       SimpleAttributeSet zSAS=new SimpleAttributeSet();
       zSAS.addAttribute(StyleConstants.Alignment, StyleConstants.ALIGN_CENTER);
       zSAS.addAttribute(StyleConstants.Bold, Boolean.valueOf(true));
       
       //Step 2: set the text
       StringBuffer zSBuf=new StringBuffer();     
       zSBuf.append("" + JSTELLA_LONGTITLE + "\n");
       zSBuf.append("Version: " + JSTELLA_VERSION + "\n");
       zSBuf.append("Applet version: " + JSTELLA_APPLET_VERSION + "\n");
       zSBuf.append("" + JSTELLA_BYLINE + "\n");
       zSBuf.append("" + JSTELLA_HTTP + "\n");
       
       
       //Step 3 : add the text
       try{
       StyledDocument zSDoc=TPHelp.getStyledDocument();
       zSDoc.remove(0, zSDoc.getLength());
       zSDoc.insertString(0, zSBuf.toString(), null);
       zSDoc.setParagraphAttributes(0, zSDoc.getLength(), zSAS, false);
       }//end : try
       catch (BadLocationException e)
       {
           e.printStackTrace();
       }
   }
    
     
    
    
    private void loadParameters() {
     
        try{    
            String zROM=this.getParameter(PARAMETER_ROM);
            String zCartridgeType=this.getParameter(PARAMETER_CARTRIDGE_TYPE);
            String zDisplayHeightStr=this.getParameter(PARAMETER_DISPLAY_HEIGHT);
            String zPaddleMode=this.getParameter(PARAMETER_PADDLE_MODE);
            String zPaddleMouseAxis=this.getParameter(PARAMETER_PADDLE_MOUSE_AXIS);
            String zOptionsPanelEnabled=this.getParameter(PARAMETER_OPTIONS_PANEL_ENABLED);
            
            if (zOptionsPanelEnabled==null) 
            {
                setOptionsPanelVisible(true);
                debugOut("Using default options panel setting");
            }
            else if (isAffirmative(zOptionsPanelEnabled)==true) 
            {
                setOptionsPanelVisible(true);
                debugOut("Enabling options panel");
            }//end : is affirmative
            else 
            {
                setOptionsPanelVisible(false);
                debugOut("Disabling options panel");
            }//end : is negative
                
            if (zCartridgeType!=null) debugOut("Cartridge type=" + zCartridgeType + " (user specified)");
            else debugOut("Cartridge type=<auto-detect>");
            
           
            
            int zDisplayHeight=-1;
            if (zDisplayHeightStr!=null) 
                try{
                    zDisplayHeight=Integer.parseInt(zDisplayHeightStr);
                    debugOut("Display height=" + zDisplayHeight + " (user specified)");
                }//end : try
                catch (NumberFormatException e)
                {
                  debugOut("Error : invalid height parameter - " + zDisplayHeightStr);  
                }//end : poorly formatted
            
            
            if (zPaddleMouseAxis!=null)
            {
                if (zPaddleMouseAxis.toUpperCase().trim().equals("Y"))
                {
                    myIntercessor.getInputMaster().setPaddleMouseAxis(InputMaster.PaddleMouseAxis.MOUSE_Y);
                    System.out.println("Setting paddle mouse axis to Y axis");
                }
                else 
                {
                    myIntercessor.getInputMaster().setPaddleMouseAxis(InputMaster.PaddleMouseAxis.MOUSE_X);
                     System.out.println("Setting paddle mouse axis to X axis");
                }//end : default
               
            }
            
            
             if (zPaddleMode!=null) 
             {
                myIntercessor.lockPaddleMode();
                debugOut("Locking paddle mode");
             }//end : locking paddle mode
            
             debugOut("Attempting to load: codebase=" + this.getCodeBase() + ", ROM=" + zROM);
            java.net.URL zURL=new java.net.URL(this.getCodeBase(), zROM);
           
           
            
            debugOut("Loading URL: " + zURL);
       /*      URLConnection zURLConnection=zURL.openConnection();
            debugOut("Opened connection : " + zURLConnection);
            
            java.io.InputStream zIS=zURLConnection.getInputStream();
            debugOut("Opened stream");
        */
            java.io.InputStream zIS=zURL.openStream();
            myIntercessor.playROM(zIS, zCartridgeType, zDisplayHeight);
            debugOut("Loaded : " + myIntercessor.getCartridge());
       
            
            zIS.close();
      
        } catch (IOException e) {
            debugOut("JStella Applet: error, unable to load ROM");
            
            debugOut(e.toString());
            e.printStackTrace();
           
        }//end : catch
    }
    
  
    
    
  private static boolean isAffirmative(String aValue)
  {
      boolean zAffirmative=false;
      if (aValue!=null)
      {
      String zStr=aValue.trim().toLowerCase();
      if ((zStr.equals("true") || (zStr.equals("yes")))) zAffirmative=true;
      }//end : not null
      return zAffirmative;
  }
    
    
    
    
   private void refocusKeyboard()
   {
      // this.requestFocusInWindow();
       myIntercessor.refocusKeyboard();
   }
    
    
    
    
    
    public void stop() {
        //myIntercessor.stopTimer();
          try {
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            public void run() { myIntercessor.stopTimer(); }
              });
    } catch (Exception e) { debugOut("Error in stop() : " + e.toString()); }
       
        
     
    }
    
    public void start() {
        
       //detectOptionsApplet();
      try {
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            public void run() { myIntercessor.refocusKeyboard(); 
                                myIntercessor.startTimer(); }
              });
    } catch (Exception e) { debugOut("Error in start() : " + e.toString()); }
       
    }

        
        
        
        
       
    
    
    private void detectOptionsApplet()
    {
          if (getOptionsApplet()==null) 
        {
            debugOut("No options applet detected");
            setOptionsPanelVisible(true);
        }//end : did not detect options applet
        else 
        {
            debugOut("Detected options applet");
            setOptionsPanelVisible(false);
        }//end : detected
    }
    
    
    public void setOptionsPanelVisible(boolean aVisible)
    {
       if (aVisible==true) 
       {
           if (PanelOptions.getParent()==null) 
           {
               getContentPane().add(PanelOptions, BorderLayout.SOUTH);
               //PanelOptions.revalidate();
               getContentPane().validate();
               this.repaint();
           }//end : no parent yet
       }//end : set to visible
       else if (aVisible==false)
       {
           if (PanelOptions.getParent()!=null)
           {
               getContentPane().remove(PanelOptions);
               getContentPane().validate();
               this.repaint();
           }//end : has a parent
       }//end : set to invisible
       
    }
    
    
    public void destroy() {
        myIntercessor.stopTimer();
        //myTimer=null;
        myIntercessor.destroy();
        
        super.destroy();
        
    }

    public void displayCanvas(JPanel aCanvas) {
             this.add(aCanvas, java.awt.BorderLayout.CENTER); 
    }

    public boolean respondToException(JSException e) {
        debugOut("Responding to exception: " + e);
        myIntercessor.showDefaultExceptionResponse(e, this);
        return true;
    }

    public void informUserOfPause(boolean aIsPaused) {
        if (aIsPaused) LabelPause.setText("PAUSED");
        else LabelPause.setText("");
    }

    public void updateSwitches() {
    }

    public java.util.Map<String, String> getConfiguration() {
        return myConfiguration;
    }

  /*  public boolean isOkayToAutoPause() {
        boolean zReturn=true;
        if (getOptionsApplet()!=null)
        {
            if (getOptionsApplet().isFocusOwner()==true) zReturn=false;
        }//end : found options applet
        return zReturn;
    }
   */
    
     private JStellaOptionsApplet getOptionsApplet()
    {
        if (myOptionsApplet==null)
        {
       
        java.util.Enumeration zApplets=this.getAppletContext().getApplets();
        for (; zApplets.hasMoreElements() ;) {
            
            Object zObj=zApplets.nextElement();
            if (zObj instanceof JStellaOptionsApplet)
            {
                myOptionsApplet=(JStellaOptionsApplet)zObj;
                break;
            }//end : found a JStellaOptionsApplet
        }//end : for enumeration loop
              
        }//end : options applet not yet found
        
        return myOptionsApplet;
        
        
    }
     
     
     
     
     
     
     
     
     
     
     
     //================== Methods for JavaScript to Call =====================
                    
     //TODO : figure out what thread these would be in if executed by javascript, and whether they
               //should be run in the event thread with an invokeAndWait() method call.
     
   public void doSelect()
     {
         //boolean zOldAutoPause=myIntercessor.getAutoPauseMode();
         
         myIntercessor.emulateSelectPress();
 
        refocusKeyboard();
     }
     
     public void doReset()
     {
        myIntercessor.emulateResetPress();
        refocusKeyboard();
     }
     
     public void doHelp()
     {
          DialogHelp.setLocationRelativeTo(this); //middle of screen
        DialogHelp.setSize(400,200);
        DialogHelp.setVisible(true);
        //refocusKeyboard();
     }  
     
     public void setManualPause(boolean aPaused)
     {
         myIntercessor.setPausedByPlayer(aPaused);
         myIntercessor.getJStellaCanvas().refreshCanvas();
     }
     
     public void toggleManualPause()
     {
         boolean zOld=myIntercessor.isPausedByPlayer();
         myIntercessor.setPausedByPlayer(!zOld);
     }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonHelp;
    private javax.swing.JButton ButtonHelpOK;
    private javax.swing.JButton ButtonReset;
    private javax.swing.JButton ButtonSelect;
    private javax.swing.JButton ButtonShowDebugInfo;
    private javax.swing.JDialog DialogHelp;
    private javax.swing.JLabel LabelPause;
    private javax.swing.JPanel PanelHelpSouth;
    private javax.swing.JPanel PanelOptions;
    private javax.swing.JScrollPane SPHelp;
    private javax.swing.JTextPane TPHelp;
    // End of variables declaration//GEN-END:variables
    
    private class AppletRefocuser extends java.awt.event.MouseAdapter
    {
        public void mouseClicked(MouseEvent e) {
            myIntercessor.refocusKeyboard();
            
        }
        
    }
    
    
    
    
    
    
}
