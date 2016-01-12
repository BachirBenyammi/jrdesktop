package jrdesktop.rmi.server;

import java.awt.Rectangle;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import jrdesktop.server.Server;
import jrdesktop.utilities.PasswordUtility;
import jrdesktop.viewer.Viewer;

/**
 * ServerImpl.java
 * @author benbac
 */

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {        
     
    public ServerImpl (int port) throws RemoteException {super(port);}

    public ServerImpl (RMIClientSocketFactory csf, RMIServerSocketFactory ssf,
            int port) throws RemoteException {
        super(port, csf, ssf);
    }

    public ServerImpl (RMIClientSocketFactory csf, RMIServerSocketFactory ssf)
            throws RemoteException {
        super(0, csf, ssf);
    }
    
    @Override
    public boolean isOptionsChanged (int index) {
        return Viewer.isOptionsChanged(index);
    }
         
    @Override
    public void setOptionsChanged (int index, boolean bool) {
        Viewer.setOptionsChanged(index, bool);
    }
    
    @Override
    public int startViewer(InetAddress inetAddress,
            String username, String password, boolean isReversedConnection) throws RemoteException {
       
        if (!RMIServer.serverConfig.username.equals(username) || 
                !RMIServer.serverConfig.password.equals(
                PasswordUtility.decodeString(password)))
            return -1;  
        if (RMIServer.serverConfig.reverseConnection != isReversedConnection)
            return -2;
        if (RMIServer.serverConfig.reverseConnection)
            return Viewer.addViewer(inetAddress);
        else                    
            return Server.addViewer(inetAddress);
    }
    
    @Override
    public void stopViewer(int index) throws RemoteException {
        if (RMIServer.serverConfig.reverseConnection)
            Viewer.removeViewer(index);
        else            
            Server.removeViewer(index);
    }
    
    @Override
    public void setScreenCapture(byte[] data, int index) throws RemoteException {
        Viewer.setScreenCapture(data, index);
    }

    @Override
    public HashMap<String, byte[]> getChangedScreenBlocks(int index, boolean isEmpty) throws RemoteException {
        return Server.getChangedScreenBlocks(index, isEmpty);
    }

    @Override
    public void setChangedScreenBlocks(HashMap<String, byte[]> changedBlocks, int index) {
        Viewer.setChangedScreenBlocks(changedBlocks, index);
    }

    @Override
    public byte[] getScreenCapture(int index) throws RemoteException {
        return Server.getScreenCapture(index);
    }
    
    @Override
    public void setScreenRect(Rectangle rect, int index) throws RemoteException {        
        Viewer.setScreenRect(rect, index);
    }
        
    @Override
    public Rectangle getScreenRect(int index) throws RemoteException {        
        return Server.getScreenRect(index);
    }
    
    @Override
    public void setMouseEvents(int index, ArrayList events) throws RemoteException {
        Server.setMouseEvents(index, events);
    }
    
    @Override
    public ArrayList getMouseEvents(int index) throws RemoteException {
        return Viewer.getMouseEvents(index);
    }
    
    @Override
    public void setKeyEvents(ArrayList events) throws RemoteException {
        Server.setKeyEvents(events);
    }
    
    @Override
    public ArrayList getKeyEvents(int index) throws RemoteException {
        return Viewer.getKeyEvents(index);
    }
    
    @Override
    public void setClipboardContent (Object object) throws RemoteException {
        Server.setClipboardContent(object);
    }
    
    @Override
    public void setClipboardContent (Object object, int index) throws RemoteException {
        Viewer.setClipboardContent(object, index);
    }
    
    @Override
    public Object getClipboardContent () throws RemoteException {
        return Server.getClipboardContent();
    }   
    
    @Override
    public Object getClipboardContent (int index) throws RemoteException {
        return Viewer.getClipboardContent(index);
    }
    
    @Override
    public void setOptions(Object data, int index) throws RemoteException {
        Server.setOptions(data, index);
    }

    @Override
    public Object getOptions (int index) throws RemoteException {
        return Viewer.getOptions(index);
    }
    
    @Override
    public void setOption(Object data, int index, int option) throws RemoteException {
        Server.setOption(data, index, option);
    }

    @Override
    public Object getOption(int index, int option) throws RemoteException {
        return Viewer.getOption(index, option);
    }
    
    @Override
    public ArrayList getFileList () throws RemoteException {
        return Server.getFileList();
    }  
    
    @Override
    public void SendFile(byte[] filedata, String fileName, int index) {
        Server.SendFile(filedata, fileName, index);
    }    
    
    @Override
    public byte[] ReceiveFile(String fileName, int index) {
        return Server.ReceiveFile(fileName, index);
    }
    
    @Override
    public ArrayList getConnectionInfos (int index) throws RemoteException {
        if (RMIServer.serverConfig.reverseConnection)
            return Viewer.getConnectionInfos(index);
        else
            return Server.getConnectionInfos(index);
    }
    
    @Override
    public Hashtable getHostProperties () throws RemoteException {
        return Server.getHostProperties();
    }
    
    @Override
    public void setHostProperties (int index, Hashtable props) throws RemoteException {
        if (RMIServer.serverConfig.reverseConnection)
            Viewer.setHostProperties(index, props);
        else              
            Server.setHostProperties(index, props);
    }  
}
