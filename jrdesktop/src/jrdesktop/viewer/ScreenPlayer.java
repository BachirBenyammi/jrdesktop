package jrdesktop.viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import jrdesktop.Commons;
import jrdesktop.utilities.ImageUtility;

/**
 * ScreenPlayer.java
 * @author benbac
 */

public class ScreenPlayer extends JLabel {
      
    private Recorder recorder;

    private float screenScale = 1.0f;
    private float oldscreenScale = 1.0f;
    boolean PartialScreenMode = false;
    private BufferedImage screenImage = null;
    private Rectangle selectionRect = Commons.emptyRect;
    private Rectangle oldselectionRect = Commons.diffRect;
    private Rectangle screenRect = Commons.emptyRect;
    private Rectangle oldScreenRect = Commons.diffRect;
    
    public boolean isSelecting = false;
    
    // mouse cordination for selection
    public int srcx, srcy, destx, desty;

    // Stroke-defined outline of selection rectangle.
    private BasicStroke bs;

    // used to create a distinctive-looking selection rectangle outline.
    private GradientPaint gp;
   
    public ScreenPlayer(Recorder recorder) { 
        this.recorder = recorder;
        setFocusable(true);
        InitialSelectionRect();
    };

    /*public void updateScreenRect(Rectangle rect) {
        if (rect.equals(Commons.emptyRect))
            screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        else
            screenRect = rect;
        if (!capture.getScreenRect().equals(screenRect)) {
            capture.updateScreenSize(screenRect);
            setNewScreenImage(screenRect, colorQuality);
        }
    }*/

    public void updateScreenRect () {
        screenScale = recorder.viewerOptions.getScreenScale();

         if (!PartialScreenMode) {
             screenRect = recorder.viewerOptions.getScreenRect();
               if (!screenRect.equals(oldScreenRect) ) {
               oldScreenRect = screenRect;
               setSize(screenRect.getSize());
               setPreferredSize(screenRect.getSize());
                if (!recorder.viewerOptions.capture.getScreenRect().equals(screenRect)) {
                    recorder.viewerOptions.capture.updateScreenSize(screenRect);
                    recorder.viewerOptions.setNewScreenImage(screenRect,
                            recorder.viewerOptions.getColorQuality());
                }
           }

            if (oldscreenScale != screenScale) {
                Dimension dimension = new Dimension(
                    (int) (screenScale * screenRect.getWidth()),
                    (int) (screenScale * screenRect.getHeight())
                );
                setSize(dimension);
                setPreferredSize(dimension);
                oldscreenScale = screenScale;
            }
         }
       else {       
             if(!isSelecting)
                if (!selectionRect.equals(oldselectionRect)) {
                    //screenRect = selectionRect;
                    oldselectionRect = selectionRect;
                    setSize(selectionRect.getSize());
                    setPreferredSize(selectionRect.getSize());
                    if (!recorder.viewerOptions.capture.getScreenRect().equals(selectionRect)) {
                        recorder.viewerOptions.capture.updateScreenSize(selectionRect);
                        recorder.viewerOptions.setNewScreenImage(selectionRect,
                            recorder.viewerOptions.getColorQuality());
                    }
                }

            if (screenScale != screenScale) {
                Dimension dimension = new Dimension(
                    (int) (screenScale * selectionRect.getWidth()),
                    (int) (screenScale * selectionRect.getHeight())
                );
                setSize(dimension);
                setPreferredSize(dimension);
                oldscreenScale = screenScale;
            }
       }
    }

    @Override
    public void paint(Graphics g) {
            g.drawImage(screenImage, 0, 0,
                (int) (screenRect.width * screenScale),
                (int) (screenRect.height * screenScale), this);
        DrawSelectingRect(g);
    }

    public void UpdateScreen(byte[] data) { 
        updateScreenRect();
        screenImage = ImageUtility.toBufferedImage(data);

        recorder.viewerOptions.setScreenImage(screenImage);
        repaint();
    }

    public void UpdateScreen(HashMap<String, byte[]> changedBlocks) {
        updateScreenRect();

        screenImage = recorder.viewerOptions.getScreenImage();

        screenImage = recorder.viewerOptions.getCapture().
                setChangedBlocks(screenImage, changedBlocks);
  
        recorder.viewerOptions.setScreenImage(screenImage);        

        repaint();
    }

    /*public void clearScreen() {
        setSize(new Dimension(1, 1));
        setPreferredSize(new Dimension(1, 1));
        //recorder.viewerOptions.getCapture().clearScreen();
        screenRect = Commons.emptyRect;
        oldScreenRect = Commons.diffRect; 
        recorder.viewerOptions.setScreenRect(Commons.emptyRect);
        recorder.viewer.setOption(Commons.RECT_OPTION);
        repaint();
    }*/
    
    public void InitialSelectionRect() {
        // Define the stroke for drawing selection rectangle outline.
        bs = new BasicStroke (5, BasicStroke.CAP_ROUND, 
                               BasicStroke.JOIN_ROUND,
                               0, new float [] { 12, 12 }, 0);

        // Define the gradient paint for coloring selection rectangle outline.
        gp = new GradientPaint (0.0f, 0.0f, Color.red, 1.0f, 1.0f, Color.white, true);    
    }  
    
    public void DrawSelectingRect(Graphics g) {
        if (isSelecting)
            if (srcx != destx || srcy != desty)
            {
                // Compute upper-left and lower-right coordinates for selection
                // rectangle corners.

                int x1 = (srcx < destx) ? srcx : destx;
                int y1 = (srcy < desty) ? srcy : desty;

                int x2 = (srcx > destx) ? srcx : destx;
                int y2 = (srcy > desty) ? srcy : desty;

                // Establish selection rectangle origin.
                selectionRect.x = x1;
                selectionRect.y = y1;

                // Establish selection rectangle extents.
                selectionRect.width = (x2-x1)+1;
                selectionRect.height = (y2-y1)+1;

                // Draw selection rectangle.
                Graphics2D g2d = (Graphics2D) g;
                g2d.setStroke (bs);
                g2d.setPaint (gp);
                g2d.draw (selectionRect);

                PartialScreenMode = true;
      }         
    }
    
    public boolean isPartialScreenMode() {
        return PartialScreenMode;
    } 
    
    public Rectangle getSelectionRect () {
        return selectionRect;
    }
    
    public void startSelectingMode() {
        isSelecting = true;
        Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        setCursor(cursor);
    }
    
    public void stopSelectingMode() {
        PartialScreenMode = false;
        selectionRect = Commons.emptyRect;
        oldselectionRect = Commons.diffRect;
        //screenRect = Commons.emptyRect;
        recorder.viewerOptions.setScreenRect(new Rectangle(0, 0, 0, 0));        
        if (recorder.config.reverseConnection)                        
            recorder.viewerOptions.setChanged(true);       
        else
            recorder.viewer.setOption(Commons.RECT_OPTION);
    }

    public void doneSelecting ()
    {        
        if (isSelecting) {
            isSelecting = false;
            oldselectionRect = Commons.emptyRect;
            
            if (PartialScreenMode) {
                float screenScale = 1.0f / recorder.viewerOptions.getScreenScale();
                Rectangle rect = new Rectangle(selectionRect);
                rect.x = (int) (rect.x * screenScale);
                rect.y = (int) (rect.y * screenScale);
                rect.height = (int) (rect.height * screenScale);
                rect.width = (int) (rect.width * screenScale);
                recorder.viewerOptions.setScreenRect(rect);
                if (recorder.config.reverseConnection)                        
                    recorder.viewerOptions.setChanged(true);       
                else                
                    recorder.viewer.setOption(Commons.RECT_OPTION);
                recorder.viewerGUI.jBtnPartialComplete.setIcon(
                        new ImageIcon(Commons.DEFAULT_SCREEN_ICON));
            }
            
            srcx = destx;
            srcy = desty;     
        
            Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
            setCursor(cursor);   
        }
    }
}