package jrdesktop.server;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import jrdesktop.Commons;
import jrdesktop.Config;
import jrdesktop.HostProperties;
import jrdesktop.Settings;
import jrdesktop.SysTray;
import jrdesktop.rmi.client.RMIClient;
import jrdesktop.viewer.ViewerOptions;
import jrdesktop.utilities.ClipbrdUtility;
import jrdesktop.utilities.FileUtility;
import jrdesktop.rmi.server.RMIServer;
import jrdesktop.server.http.HttpServer;
import jrdesktop.utilities.screenCaptureCompressor.ScreenCapture;
import jrdesktop.viewer.Recorder;

/**
 * Server.java
 * @author benbac
 */

public class Server extends Thread {
       
    private static boolean running = false;
    
    private static robot rt;
    private static ClipbrdUtility clipbrdUtility;
              
    private static Hashtable<Integer, ViewerOptions> viewers =
            new Hashtable<Integer, ViewerOptions>();
       
    private int index = -1;
    private Recorder recorder;   

    private static HttpServer httpServer;
    private RMIClient client;
    private boolean connected = false; 
    
    public static void Start() {   
        running = false; 
        if (!RMIServer.Start()) return;
        init();
    }              
    
    public static void Start(Config config) {
        running = false; 
        if (!RMIServer.Start(config)) return;
        init();
    }      
    
    public static void init () {
        running = true;
        httpServer = new HttpServer();
        rt = new robot();
        clipbrdUtility = new ClipbrdUtility();
        SysTray.updateServerStatus(Commons.SERVER_STARTED);          
    }
    
    public static void Stop() {
        if (running) {
            running = false;
            httpServer.disconnected();
            httpServer = null;
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
    
    public static byte[] getScreenCapture(int index) {
        if (!viewers.containsKey(index)) return new byte[0];
        byte[] screenCapture = rt.CaptureScreenByteArray(viewers.get(index));
        if (viewers.containsKey(index)) // try to remove this test
            viewers.get(index).connectionInfos.incReceivedData(screenCapture.length);                
        return screenCapture;
    }

    public static HashMap<String, byte[]> getChangedScreenBlocks(int index,
             boolean isEmpty) {
        if (!viewers.containsKey(index)) return new  HashMap<String, byte[]>();
        HashMap<String, byte[]> blocks = rt.getChangedScreenBlocks (
                viewers.get(index), isEmpty);
        if (viewers.containsKey(index)) // try to remove this test
            viewers.get(index).connectionInfos.incReceivedData(
                    ScreenCapture.getChangedBlocksSize(blocks));
        return blocks;
    }

    public static Rectangle getScreenRect(int index) {        
    //    if (!viewers.containsKey(index)) return Commons.emptyRect;
        if (viewers.get(index).getScreenRect().equals(Commons.emptyRect))
            return new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return viewers.get(index).getScreenRect();
    }

    public static void setKeyEvents(ArrayList events) {
        rt.setKeyEvents(events);
    }
    
    public static void setMouseEvents(int index, ArrayList events) {
        rt.setMouseEvents(viewers.get(index), events);
    }      
    public static void setOptions(Object data, int index) {
        viewers.get(index).setOptions(data);
    }
    
    public static void setOption(Object data, int index, int option) {
          viewers.get(index).setOption(data, option);
    }
    
    public static int addViewer(InetAddress inetAddress) {
        int index = viewers.size();        

        viewers.put(index, new ViewerOptions(inetAddress));        

        SysTray.displayViewer(inetAddress.toString(), index, true);        
        return index;
    }
         
    public static int removeViewer(int index) {
        String viewer = viewers.get(index).getInetAddress().toString();
        
        viewers.remove(index);
        
        SysTray.displayViewer(viewer, viewers.size(), false);        
        return index;
    } 
    
    public static void disconnectAllViewers() {              
        Enumeration<Integer> viewerEnum = viewers.keys();
        while (viewerEnum.hasMoreElements())
            removeViewer(viewerEnum.nextElement());
    }
    
   public static byte[] ReceiveFile(String fileName, int index){
      try {
         File file = new File(fileName);
         byte buffer[] = new byte[(int)file.length()];
         BufferedInputStream input = new
            BufferedInputStream(new FileInputStream(file));
         input.read(buffer, 0, buffer.length);
         input.close();
               
            /*if (Config.reverseConnection)
                ((Recorder) viewers.get(index)).viewerOptions.connectionInfos.
                        incSentData(buffer.length);
            else*/
                viewers.get(index).connectionInfos.incSentData(buffer.length);
         
         return(buffer);
      } catch(Exception e){
            e.printStackTrace();
         return(null);
      }
   }
      
    public static void SendFile(byte[] filedata, String fileName, int index) {
        try {                  
            fileName = Settings.downloadsDir + fileName;
            new File(new File(fileName).getParent()).mkdirs();
            File file = new File(fileName);

            BufferedOutputStream output = new
                BufferedOutputStream(new FileOutputStream(file));
            output.write(filedata, 0, filedata.length);
            output.flush();
            output.close();  
                      
            /*if (Config.reverseConnection)
                ((Recorder) viewers.get(index)).viewerOptions.connectionInfos.
                        incReceivedData(filedata.length);
            else*/
                viewers.get(index).connectionInfos.incReceivedData(filedata.length);
                SysTray.updateServerStatus(SysTray.customMsg, file.getName()
                        + "(" + FileUtility.getSizeHumanFormat(filedata.length,
                        FileUtility.BYTES) + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }                 
   }   
    
    public static ArrayList getFileList() {
        File[] files = clipbrdUtility.getFiles();
        if (files.length == 0) return null;
        ArrayList<Object> FileSysInfos = new ArrayList<Object>();
        FileSysInfos.add(files[0].getParent());
        FileSysInfos.add(FileUtility.getAllFiles(files)); 
        return FileSysInfos;         
    }     
    
    public static Hashtable<Integer, InetAddress> getConnectedHosts () {
        Hashtable<Integer, InetAddress> hosts = 
                new Hashtable<Integer, InetAddress>();
                
        Enumeration<Integer> viewerEnum = viewers.keys();
        while (viewerEnum.hasMoreElements()) {
            int key = viewerEnum.nextElement();
            hosts.put(key, viewers.get(key).getInetAddress());
        }
        
        return hosts;
    }
    
    public static int getViewersCount () {
        return viewers.size();
    }
    
    public static void setHostProperties (int index, Hashtable props) {                
        viewers.get(index).setProperties(props);
    }
    
    public static void displayViewerProperties (int index) {
        HostProperties.display(viewers.get(index).getProperties());
    }
    
    public static ArrayList getConnectionInfos(int index) {
        return viewers.get(index).connectionInfos.getData();
    }
    
    public static void displayConnectionInfos(int index) {
        viewers.get(index).connectionInfos.display();
    }
    
    public static Hashtable getHostProperties() {
        return HostProperties.getLocalProperties();
    }
    
    public static void setClipboardContent(Object object) {
        clipbrdUtility.setContent(object);
    }

    public static Object getClipboardContent() {
        return clipbrdUtility.getContent();
    }
    
    public static String getStatus() {
        return RMIServer.getStatus();
    }       
    
    public Server (Config viewerConfig) {
        client = new RMIClient(viewerConfig);
    }       
    
    public boolean isConnected() {
        return connected;
    }
    
    public void _Start() { 
        connect();
        if (connected) { 
            recorder = new Recorder(this, client.clientConfig);
            rt = new robot(this);      
            getOptions();                   
            rt.running = true;             
            rt.Notify();      
        }        
        else _Stop();
    }
    
    public void _Stop() {
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
    
    public void getOption (int option) {
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
            if (recorder.viewerOptions.isScreenCompressionEnabled())
                client.rmiServer.setChangedScreenBlocks(
                        rt.getChangedScreenBlocks(recorder.viewerOptions, true), index) ;
                        //recorder.viewerOptions.getCapture().getChangedBlocks(), index);
            else
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
    
    public void setHostProperties () {
        try {
            client.rmiServer.setHostProperties(index, 
                    HostProperties.getLocalProperties()); 
        } catch (RemoteException re) {
            re.printStackTrace();
        }    
    }
    
    public void sendData () {
       if (isOptionsChanged()) {
           getOptions();
           setOptionsChanged(false);
       }
       setScreenRect();
       setScreenCapture();       
    }
    
    public void receiveData () {
        getMouseEvents();
        getKeyEvents();
    }     
}