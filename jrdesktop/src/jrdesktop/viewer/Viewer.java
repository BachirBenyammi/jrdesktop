package jrdesktop.viewer;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import jrdesktop.Commons;
import jrdesktop.Config;
import jrdesktop.HostProperties;
import jrdesktop.ConnectionInfos;
import jrdesktop.SysTray;
import jrdesktop.rmi.client.RMIClient;
import jrdesktop.rmi.server.RMIServer;
import jrdesktop.server.robot;
import jrdesktop.utilities.ClipbrdUtility;
import jrdesktop.utilities.screenCaptureCompressor.ScreenCapture;
import jrdesktop.viewer.FileMng.FileTransGUI;

/**
 * Viewer.java
 * @author benbac
 */

public class Viewer extends Thread {

    private int index = -1;
    private static robot rt;
    private Recorder recorder;   
    
    private RMIClient client;
    private boolean connected = false;       
                
    private static Hashtable<Integer, Recorder> viewers = 
            new Hashtable<Integer, Recorder>(); 
    private static ClipbrdUtility clipbrdUtility;

    private static boolean running = false;
    
    public Viewer (Config config) {
        client = new RMIClient(config);
    }   
   
    public Viewer (Recorder recorder) {
        this.recorder = recorder;
    }   
    
    public boolean isConnected() {
        return connected;
    }
    
    public void Start() { 
        connect();
        if (connected) { 
            recorder = new Recorder(this, client.clientConfig);            
            recorder.viewerGUI.Start();
        }        
        else Stop();
    }
    
    public void Stop() {
        disconnect();    
        interrupt();
    }
    
    public int connect() {  
        connected = false;
        
        index = client.connect();
        if (index == -1) return -1;    

        setHostProperties();
        connected = true;
        return index; 
    }
    
    public void disconnect() {
        connected = false;
        client.disconnect();
    }
   
    public void setOptions (Object data) {     
        try {
            client.rmiServer.setOptions(data, index);
        } 
        catch (RemoteException re) {
            re.printStackTrace();
        }          
    }
  
    public void setOption (int option) {        
        try {
            client.rmiServer.setOption(recorder.viewerOptions.getOption(option), 
                    index, option);
        } 
        catch (RemoteException re) {
            re.printStackTrace();
        }          
    }
    
    public void setKeyEvents () {
        try {
            client.rmiServer.setKeyEvents(recorder.eventsListener.getKeyEvents());
        } catch (RemoteException re) {
           re.printStackTrace();
        }          
    }
    
    public void setMouseEvents () {
        try {
            client.rmiServer.setMouseEvents(index, 
                    recorder.eventsListener.getMouseEvents());
        } catch (RemoteException re) {
           re.printStackTrace();
        }          
    }
    
    public void getClipboardContent () {
        if (!recorder.viewerOptions.getClipboardTransfer()) return;
        try {
            Object clipContent = client.rmiServer.getClipboardContent();
            recorder.clipbrdUtility.setContent(clipContent); 
        } catch (RemoteException re) {
            re.printStackTrace();
        }            
    } 
    
    public void setClipboardContect () {
        if (!recorder.viewerOptions.getClipboardTransfer()) return;
        Object clipContent = recorder.clipbrdUtility.getContent();
        if (clipContent == null) return;  
        try {
            client.rmiServer.setClipboardContent(clipContent);
        } catch (RemoteException re) {
           re.printStackTrace();
        }          
    }    
    
    public void getScreenCapture() {
        //if (!connected) return;
        try {
            if (recorder.viewerOptions.isScreenCompressionEnabled())
                recorder.screenPlayer.UpdateScreen(
                        client.rmiServer.getChangedScreenBlocks(index,
                        recorder.viewerOptions.getCapture().isEmpty()));
            else
                recorder.screenPlayer.UpdateScreen(
                      client.rmiServer.getScreenCapture(index));
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    /*public void setScreenCapture (HashMap<String, byte[]> changedBlocks) {
        try {
            client.rmiServer.setChangedScreenBlocks(changedBlocks, index);
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }*/

    public void getScreenRect () {
        if (!connected) return;     // try to remove this test
        try {
            recorder.viewerOptions.setScreenRect(
                    client.rmiServer.getScreenRect(index));
        } catch (RemoteException re) {
            re.printStackTrace();
        }            
    }

    public ArrayList getFileList () {
        try {
            return client.rmiServer.getFileList(); 
        } catch (RemoteException re) {
            re.printStackTrace();
            return null;
        }           
    }
    
    public void getConnectionInfos () {
        try {
            ConnectionInfos.display(client.rmiServer.getConnectionInfos(index)); 
        } catch (RemoteException re) {
            re.printStackTrace();
        }            
    }    
    
    public void getHostProperties () {
        try {
            HostProperties.display(client.rmiServer.getHostProperties()); 
        } catch (RemoteException re) {
            re.printStackTrace();
        }            
    }
    
    public void setHostProperties () {
        try {
            client.rmiServer.setHostProperties(index, 
                    HostProperties.getLocalProperties()); 
        } catch (RemoteException re) {
            re.printStackTrace();
        }    
    }   
    
    public void sendData() {
        if (!recorder.viewerGUI.isActive()) return;
        setMouseEvents();
        setKeyEvents();
        setClipboardContect();
    } 
    
    public void receiveData() {
        getScreenRect();  
        getScreenCapture();
        if (!recorder.viewerGUI.isActive()) return;
        getClipboardContent();
    }   
    
    public void SendFiles () { 
        File[] files = recorder.fileManager.getFiles();
        if (files.length == 0) return;
        new FileTransGUI(recorder).SendFiles(files);
    }

    public void ReceiveFiles() {
        ArrayList fileList = null;
        try {
            fileList = client.rmiServer.getFileList();
        } catch(RemoteException re) {
            re.printStackTrace();
        }
        if (fileList == null) return;
        new FileTransGUI(recorder).ReceiveFiles(fileList);  
    }
    
    public byte[] ReceiveFile(String filename) {
        try {            
            return client.rmiServer.ReceiveFile(filename, index);
        } catch(RemoteException re) {
            re.printStackTrace();
            return null;
        }
    }
    
    public void SendFile (byte[] buffer, String filename) {
        try {        
            client.rmiServer.SendFile(buffer, filename, index);
       } catch(RemoteException re) {
            re.printStackTrace();
       }        
    }
    
    /*public void getOption (int option) {
        try {        
            recorder.viewerOptions.setOption(client.rmiServer.getOption(index, 
                    option), option);
        } catch (RemoteException re) {
           re.printStackTrace();
        }          
    }
    
    public void getOptions () {
        try {        
            recorder.viewerOptions.setOptions(client.rmiServer.getOptions(index));
        } catch (RemoteException re) {
           re.printStackTrace();
        }          
    }
    
    public void setScreenRect() {
        try {
            client.rmiServer.setScreenRect(recorder.viewerOptions.getScreenRect(), index);
        } catch (RemoteException re) {
           re.printStackTrace();
        }        
    }
    
    public void setScreenCapture () {
        try {
            client.rmiServer.setScreenCapture(
                    rt.CaptureScreenByteArray(recorder.viewerOptions), index);
        } catch (RemoteException re) {
           re.printStackTrace();
        }
    }
    
    public void getKeyEvents () {
        try {
            rt.setKeyEvents(client.rmiServer.getKeyEvents(index));
        } catch (RemoteException re) {
           re.printStackTrace();
        }          
    }
    
    public void getMouseEvents () {
        try {
            rt.setMouseEvents(recorder.viewerOptions, 
                    client.rmiServer.getMouseEvents(index));
        } catch (RemoteException re) {
           re.printStackTrace();
        }          
    }    
    
    public boolean isOptionsChanged () {
        try {
            return client.rmiServer.isOptionsChanged(index);
        } catch (RemoteException re) {
           re.printStackTrace();
           return false;
        }          
    }
    
    public void setOptionsChanged (boolean bool) {
        try {
            client.rmiServer.setOptionsChanged(index, bool);
        } catch (RemoteException re) {
           re.printStackTrace();
        }          
    }
    
    public void _sendData () {
       if (isOptionsChanged()) {
           getOptions();
           setOptionsChanged(false);
       }
       setScreenRect();
       setScreenCapture();       
    }
    
    public void _receiveData () {
        getMouseEvents();
        getKeyEvents();
    }     
    */
    
    public static synchronized int addViewer(InetAddress inetAddress) {
        int index = viewers.size();        

        viewers.put(index, new Recorder(inetAddress));            

        SysTray.displayViewer(inetAddress.toString(), index, true);

        return index;
    }
         
    public static synchronized int removeViewer(int index) {
        String viewer = viewers.get(index).viewerOptions.getInetAddress().toString();                        
          
        viewers.remove(index);
        
        SysTray.displayViewer(viewer, viewers.size(), false);        
        return index;
    } 
    
    public static int getViewerIndex(Recorder _recorder) { 
       /* Enumeration<Recorder> enumeration = viewers.elements();
        int index = -1;
        while (enumeration.hasMoreElements()) {            
            index++;
            if (enumeration.nextElement().equals(_recorder)) break;                        
        }
        
        return index;*/
        
        for (int i=0; i<viewers.size(); i++)
            if (viewers.get(i).equals(_recorder))
                return i;
        return -1;
    }
    
    public static synchronized int removeViewer(Recorder _recorder) {
        int index = getViewerIndex(_recorder);

        if (index == -1) return index;
            
        String viewer = viewers.get(index).viewerOptions.getInetAddress().toString();                        
          
        viewers.remove(index);
        
        SysTray.displayViewer(viewer, viewers.size(), false);        
        return index;
    }
    
    public static void setPause (Recorder _recorder, boolean bool) {
        int index = getViewerIndex(_recorder);
        viewers.get(index).setPause(bool);
    }
    
    public static boolean isViewerPaused(Recorder _recorder) {   
        int index = getViewerIndex(_recorder);
        return viewers.get(index).isPaused();
    }
    
    public static void setKeyEvents(ArrayList events) {
        rt.setKeyEvents(events);
    }
    
    public static void setMouseEvents(int index, ArrayList events) {
        rt.setMouseEvents(viewers.get(index).viewerOptions, events);
    }
    
   /* public static void setClipboardContent(Object object) {
        if (object instanceof String)
            clipbrdUtility.setTextToClipboard((String ) object);    
        else if (object instanceof ImageIcon)
            clipbrdUtility.setImageToClipboard((ImageIcon) object);
    }

    public static Object getClipboardContent() {
        return clipbrdUtility.getContent();
    }*/
    
    public static String getStatus() {
        return RMIServer.getStatus();
    }
    
    public static void init() {
        running = true;
        rt = new robot();
        clipbrdUtility = new ClipbrdUtility();
        SysTray.updateServerStatus(Commons.SERVER_STARTED);         
    }
    
    public static void _Start() {
        running = false;                                                           

        if (!RMIServer.Start()) return;
        init();       
    }   
    
    public static void _Start(Config serverConfig) {
        running = false;                                                           

        if (!RMIServer.Start(serverConfig)) return;
        init();       
    }              
    
    public static void _Stop() {
        if (running) {
            running = false;
            disconnectAllViewers();
            SysTray.updateServerStatus(Commons.SERVER_STOPPED);            
        }
        else
            SysTray.updateServerStatus(Commons.CONNECTION_FAILED);
        RMIServer.Stop();
    }
    
    public static boolean isRunning() {
        return running;
    }

    public static void disconnectAllViewers() {              
        Enumeration<Integer> viewerEnum = viewers.keys();
        while (viewerEnum.hasMoreElements())
            removeViewer(viewerEnum.nextElement());
    }
    
    public static void setScreenCapture(byte[] data, int index) {
         if (!viewers.containsKey(index)) return;
        viewers.get(index).screenPlayer.UpdateScreen(data);
        viewers.get(index).viewerOptions.connectionInfos.
                incReceivedData(data.length);       
    }

    public static void setChangedScreenBlocks (HashMap<String, byte[]> changedBlocks,
            int index) {
         if (!viewers.containsKey(index)) return;
        viewers.get(index).screenPlayer.UpdateScreen(changedBlocks);
        if (viewers.containsKey(index))
            viewers.get(index).viewerOptions.connectionInfos.
                incReceivedData(ScreenCapture.getChangedBlocksSize(changedBlocks));
    }

    public static void setScreenRect(Rectangle rect, int index) {
        if (!viewers.containsKey(index)) return;
        viewers.get(index).viewerOptions.setScreenRect(rect);        
    }
    
    public static ArrayList getMouseEvents(int index) {
        if (!viewers.containsKey(index)) return new ArrayList<MouseEvent>();
        return viewers.get(index).eventsListener.getMouseEvents();
    }    

    public static ArrayList getKeyEvents(int index) {
        if (!viewers.containsKey(index)) return  new ArrayList<KeyEvent>();
        return viewers.get(index).eventsListener.getKeyEvents();
    }
    
    public static void setClipboardContent (Object object, int index) {
        if (!viewers.containsKey(index)) return;
        viewers.get(index).clipbrdUtility.setContent(object);
    }
    
    public static Object getClipboardContent (int index)  {
        if (!viewers.containsKey(index)) return null;
        return viewers.get(index).clipbrdUtility.getContent();
    }   
    
    public static void setOptionsChanged (int index, boolean bool) {
        if (!viewers.containsKey(index)) return;
        viewers.get(index).viewerOptions.setChanged(bool);
    }
    
    public static boolean isOptionsChanged (int index) {
        if (!viewers.containsKey(index)) return false;
        return viewers.get(index).viewerOptions.isChanged();
    }
    
    public static Object getOptions(int index) {                
        if (!viewers.containsKey(index)) return null;
        return viewers.get(index).viewerOptions.getOptions();  
    }

    public static Object getOption(int index, int option) {
        if (!viewers.containsKey(index)) return null;
        return viewers.get(index).viewerOptions.getOption(option);
    }       
    
    public static ArrayList getConnectionInfos(int index) {
        if (!viewers.containsKey(index)) return null;
        return viewers.get(index).viewerOptions.connectionInfos.getData();
    }
    
    public static void displayConnectionInfos(int index) {
        if (!viewers.containsKey(index)) return;
        viewers.get(index).viewerOptions.connectionInfos.display();
    }    
    
    public static void setHostProperties (int index, Hashtable props) {
        if (!viewers.containsKey(index)) return;
        viewers.get(index).viewerOptions.setProperties(props);
    }    
    
    public static void displayViewerProperties (int index) {
        HostProperties.display(viewers.get(index).viewerOptions.getProperties());
    }
    
    public static Hashtable<Integer, InetAddress> getConnectedHosts () {
        Hashtable<Integer, InetAddress> hosts = 
                new Hashtable<Integer, InetAddress>();
                
        Enumeration<Integer> viewerEnum = viewers.keys();
        while (viewerEnum.hasMoreElements()) {
            int key = viewerEnum.nextElement();
            hosts.put(key, viewers.get(key).viewerOptions.getInetAddress());
        }
        
        return hosts;
    }    
}