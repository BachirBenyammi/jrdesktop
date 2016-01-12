package jrdesktop;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import jrdesktop.utilities.FileUtility;

/**
 * ConnectionInfos.java
 * @author benbac
 */
public class ConnectionInfos {
    
    private long previous = 0;
    private long startedAt = 0;
    private long duration = 0;
    private long dataSize = 0;
    private long sentData = 0;
    private long receivedData = 0;
    private long transferSpeed = 0;

    public ConnectionInfos(boolean start) {
        if (start) init();
    }
    
    public void init() {
        startedAt = System.currentTimeMillis();
        duration = 0;
        dataSize = 0;
        sentData = 0;
        receivedData = 0;
        transferSpeed = 0;
    }

    public void display() {
        refresh();
        JOptionPane.showMessageDialog(null, 
                "Duration: \t" + getDuration() + "\n\n" +
                "Sent data: \t" + getSize(sentData) + "\n" +
                "Received data: \t" + getSize(receivedData) + "\n\n" +
                "Total data size: \t" + getSize(dataSize) + "\n\n" +
                "Transfer speed: \t" + getSpeed(),
                "Connection infos", JOptionPane.INFORMATION_MESSAGE);
    }   
    
    public static void display(ArrayList data) {
        JOptionPane.showMessageDialog(null, 
                "Duration: \t" + data.get(0) + "\n\n" +
                "Sent data: \t" + data.get(1) + "\n" +
                "Received data: \t" + data.get(2) + "\n\n" +
                "Total data size: \t" + data.get(3) + "\n\n" +
                "Transfer speed: \t" + data.get(4),
                "Connection infos", JOptionPane.INFORMATION_MESSAGE);
    }  
    
    public ArrayList getData() {
        refresh();
        ArrayList<Object> data = new ArrayList<Object>();        
        data.add(getDuration());           
        data.add(getSize(sentData));          
        data.add(getSize(receivedData));            
        data.add(getSize(dataSize));        
        data.add(getSpeed());        
        return data;        
    }
    
    public void incSentData(long size) {
        sentData += size;
    }

    public void incReceivedData(long size) {
        receivedData += size;
    }
    
    public void refresh() {
        duration = previous + System.currentTimeMillis() - startedAt;
        dataSize = sentData + receivedData;
        transferSpeed = dataSize * 1000 / duration;
    }

    public String getDuration() {
        long h = duration / 3600000;
        long m = (duration % 3600000) / 60000;
        long s = (duration % 60000) / 1000;
        return h + ":" + m + ":" + s;
    }

    public String getSize(long size) {
        return FileUtility.getSizeHumanFormat(size, FileUtility.BYTES);
    }

    public String getSpeed() {
        return FileUtility.getSizeHumanFormat(transferSpeed, 
                FileUtility.BYTES_PER_SECOND);
    }

    public void resetStartTime() {
        previous = System.currentTimeMillis() - startedAt;
        startedAt = 0;
    }
}
