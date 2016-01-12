package jrdesktop.rmi.server;

import java.awt.Rectangle;
import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * ServerInterface.java
 * @author benbac
 */

public interface ServerInterface extends Remote {
    
    public boolean isOptionsChanged (int index) throws RemoteException;
    public void setOptionsChanged (int index, boolean bool) throws RemoteException;
    
    public int startViewer (InetAddress inetAddress, 
            String username, String password, boolean isReversedConnection) throws RemoteException;
    public void stopViewer (int index) throws RemoteException;

    public HashMap<String, byte[]> getChangedScreenBlocks(int index,
            boolean isEmpty) throws RemoteException;
    public void setChangedScreenBlocks(HashMap<String, byte[]> changedBlocks,
            int index) throws RemoteException;

    public byte[] getScreenCapture (int index) throws RemoteException;
    public void setScreenCapture(byte[] data, int index) throws RemoteException;
    
    public Rectangle getScreenRect (int index) throws RemoteException;
    public void setScreenRect(Rectangle rect, int index) throws RemoteException;
     
    public ArrayList getMouseEvents(int index) throws RemoteException;
    public void setMouseEvents(int index, ArrayList events) throws RemoteException;
    
    public ArrayList getKeyEvents(int index) throws RemoteException;    
    public void setKeyEvents(ArrayList events) throws RemoteException;
            
    public Object getClipboardContent () throws RemoteException;
    public void setClipboardContent (Object object) throws RemoteException;
    
    public Object getClipboardContent (int index) throws RemoteException;
    public void setClipboardContent (Object object, int index) throws RemoteException;
    
    public Object getOptions(int index) throws RemoteException;
    public void setOptions (Object data, int index) throws RemoteException;
    
    public Object getOption(int index, int option)throws RemoteException;
    public void setOption(Object data, int index, int option) throws RemoteException;
    
    public ArrayList getFileList () throws RemoteException;    
    public byte[] ReceiveFile (String fileName, int index) throws RemoteException;
    public void SendFile (byte[] filedata, String fileName, int index) throws RemoteException;
    
    public ArrayList getConnectionInfos (int index) throws RemoteException;
    public Hashtable getHostProperties () throws RemoteException;
    public void setHostProperties (int index, Hashtable props) throws RemoteException;    
}
