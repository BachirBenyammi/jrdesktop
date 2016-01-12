package jrdesktop.utilities.screenCaptureCompressor;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Vector;

public class Screen {

    private HashMap<String,ScreenBlock> screenBlocks = new HashMap<String,ScreenBlock>();
    
    private double screenSizeWidth  = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private double screenSizeHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    
    private int rowScreenBlocks    = 0;
    private int columnScreenBlocks = 0;
    
    private double screenBlockHeight = 0;
    private double screenBlockWidth  = 0;
    
    private final String separator = "-";
    
    private Vector<String> changedScreenBlocks = new Vector<String>();
    
    public Screen(int rowScreenBlocks,int columnScreenBlocks) {
        this.rowScreenBlocks    = rowScreenBlocks;
        this.columnScreenBlocks = columnScreenBlocks;
        
        this.screenBlockHeight = getScreenSizeHeight() / rowScreenBlocks;
        this.screenBlockWidth  = getScreenSizeWidth()  / columnScreenBlocks;
    }

    public void updateScreenSize(Rectangle screenRect) {
        screenSizeWidth = screenRect.width;
        screenSizeHeight = screenRect.height;
        screenBlockHeight = screenSizeHeight / rowScreenBlocks;
        screenBlockWidth = screenSizeWidth / columnScreenBlocks;
    }

    public String getScreenBlockName(int y,int x) {
        return ("" + y + separator + x);
    }
    
    public ScreenBlock getScreenBlockImage(String name) {
        return (ScreenBlock) screenBlocks.get(name);
    }
   
    public void addScreenBlock(ScreenBlock block) {
        screenBlocks.put(block.getName(), block);
    }

    public int getBlockRow(String name) {        
        return Integer.parseInt(name.split(separator)[0]);
    }

    public int getBlockColumn(String name) {        
        return Integer.parseInt(name.split(separator)[1]);
    }
    
    public Vector<String> getChangedScreenBlocks() {
        return changedScreenBlocks;
    }

    public HashMap getScreenBlocks() {
        return screenBlocks;
    }     

    public int getRowScreenBlocks() {
        return rowScreenBlocks;
    }

    public int getColumnScreenBlocks() {
        return columnScreenBlocks;
    }

    public double getScreenBlockHeight() {
        return screenBlockHeight;
    }

    public double getScreenBlockWidth() {
        return screenBlockWidth;
    }

    public double getScreenSizeHeight() {
        return screenSizeHeight;
    }

    public double getScreenSizeWidth() {
        return screenSizeWidth;
    }
}
