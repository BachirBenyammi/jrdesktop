package jrdesktop.server;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import java.util.HashMap;
import jrdesktop.Commons;
import jrdesktop.utilities.ImageUtility;
import jrdesktop.viewer.ViewerOptions;

/**
 * robot.java
 * @author benbac
 */

public class robot extends Thread {

    private Robot rt;
    private Server server;
    public boolean running = false;
    Rectangle defaultScreenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

    private Rectangle screenRect = Commons.emptyRect;
    private Rectangle oldScreenRect = Commons.diffRect;
    
    public robot () {
        init();
    }
    
    public robot (Server server) {
        this.server = server;
        init();
        start();
    }
    
    public void init() {            
        try {               
            rt = new Robot();
        }
        catch (AWTException awte) {
            awte.printStackTrace();
        }         
    }

    @Override
    public void  run() {
        while (true) {
            Wait();

            while (running) { 
                server.sendData();  
                server.receiveData();
                Sleep(); //viewerOptions.getRefreshRate()
            }
        }
    }
   
    public void Wait() {
        try {
            synchronized(this) {    
                wait();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }         
    }
    
    public void Notify() {
        try {
            synchronized(this){            
                notify();
            }    
        }
        catch (Exception e) {
            e.printStackTrace();
        }   
    }

    public void Sleep() {
        try {
            Thread.sleep(500);  // reduce > 50% of CPU usage
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public HashMap<String, byte[]> getChangedScreenBlocks (
            ViewerOptions viewerOptions, boolean isEmpty) {
        if (isEmpty)
            viewerOptions.getCapture().clearScreen();
        
        updateScreenRect(viewerOptions);
        if (!oldScreenRect.equals(screenRect)) {
            oldScreenRect = screenRect;
            viewerOptions.capture.updateScreenSize(screenRect);
            viewerOptions.setNewScreenImage(screenRect,
                    viewerOptions.getColorQuality());
        }

        try {
            viewerOptions.getCapture().takeAndSaveImage(rt,
                    viewerOptions.getImageQuality(),
                    viewerOptions.getColorQuality(),
                    1, //viewerOptions.getScreenScale(),
                    viewerOptions.getScreenRect());
            return viewerOptions.getCapture().getChangedBlocks();
        } catch (Exception e) {
           e.printStackTrace();
           return new HashMap<String, byte[]>();
        }
    }

    public void updateScreenRect(ViewerOptions viewerOptions) {
        screenRect = new Rectangle(viewerOptions.getScreenRect());
        if (screenRect.equals(Commons.emptyRect))
             screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    }
  
    public BufferedImage captureScreen(ViewerOptions viewerOptions) {        
        //updateScreenRect(viewerOptions);
        updateScreenRect(viewerOptions);
        oldScreenRect = screenRect;
        BufferedImage screen =  rt.createScreenCapture(screenRect); 
        
        BufferedImage bimage = new BufferedImage (screenRect.width, 
                screenRect.height, viewerOptions.getColorQuality());
       Graphics2D g2d = bimage.createGraphics ();
       g2d.drawImage(screen, 0, 0, screenRect.width, screenRect.height, null);       
      // g2d.dispose ();  
       
       return bimage;
    }

    public byte[] CaptureScreenByteArray(ViewerOptions viewerOptions) {  
        return ImageUtility.toByteArray(captureScreen(viewerOptions), 
                viewerOptions.getImageQuality());
    }             

    public void setMouseEvents(ViewerOptions viewerOptions, ArrayList evts) {
        for (int i=0; i<evts.size(); i++)
            setMouseEvent(viewerOptions, (MouseEvent) evts.get(i));
    }

    public void setMouseEvent(ViewerOptions viewerOptions, MouseEvent evt) {
        final int x = viewerOptions.getScreenRect().x +
                (int) (evt.getX() / viewerOptions.getScreenScale());
        final int y = viewerOptions.getScreenRect().y +
                (int) (evt.getY() / viewerOptions.getScreenScale());
        rt.mouseMove(x, y);
        int buttonMask = 0;
        int buttons = evt.getButton();
        if ((buttons == MouseEvent.BUTTON1)) buttonMask = InputEvent.BUTTON1_MASK;
        if ((buttons == MouseEvent.BUTTON2)) buttonMask |= InputEvent.BUTTON2_MASK;
        if ((buttons == MouseEvent.BUTTON3)) buttonMask |= InputEvent.BUTTON3_MASK;     
        switch(evt.getID()) {         
            case MouseEvent.MOUSE_PRESSED: rt.mousePress(buttonMask); break;
            case MouseEvent.MOUSE_RELEASED: rt.mouseRelease(buttonMask); break;
            case MouseEvent.MOUSE_WHEEL: rt.mouseWheel(
                    ((MouseWheelEvent) evt).getUnitsToScroll()); break;
        }          
    }
    
    public void setKeyEvents(ArrayList evts) {
        for (int i=0; i<evts.size(); i++)
            setKeyEvent((KeyEvent) evts.get(i));
    }
    
    public void setKeyEvent(KeyEvent evt) {
        switch(evt.getID()) {
            case KeyEvent.KEY_PRESSED: rt.keyPress(evt.getKeyCode()); break;
            case KeyEvent.KEY_RELEASED: rt.keyRelease(evt.getKeyCode()); break; 
        }
    }  
}