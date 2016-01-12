package jrdesktop.viewer;

import java.net.InetAddress;

import jrdesktop.Commons;
import jrdesktop.Config;
import jrdesktop.HostProperties;
import jrdesktop.rmi.server.RMIServer;
import jrdesktop.server.Server;
import jrdesktop.utilities.ClipbrdUtility;
import jrdesktop.utilities.InetAdrUtility;
import jrdesktop.viewer.FileMng.FileManager;

/**
 * Recorder.java
 * @author benbac
 */

public class Recorder extends Thread {
    
    private boolean recording = false;          // control recording
    private boolean viewOnly = false;
    private boolean pause = false;
    private boolean hold = false;
    private boolean side = Commons.viewerSide;
    
    public Config config;
    public Server server;
    public Viewer viewer;    
    public ViewerGUI viewerGUI;
    public ScreenPlayer screenPlayer;
    public EventsListener eventsListener;
    public ClipbrdUtility clipbrdUtility;
    public ViewerOptions viewerOptions; 
    public FileManager fileManager;
    
    public Recorder (InetAddress inetAddress) {
        config = RMIServer.serverConfig;
        
        init();       
        viewerOptions = new ViewerOptions(inetAddress);         
        screenPlayer = new ScreenPlayer(this);
        eventsListener = new EventsListener(this);
        viewerGUI = new ViewerGUI(this);
        viewerGUI.Start();
    }
    
    public Recorder(Viewer viewer, Config config) {
        this.viewer = viewer;   
        this.config = config;        
        side = Commons.viewerSide;
        
        init(); 
        viewerOptions = new ViewerOptions(InetAdrUtility.getLocalHost(),
                HostProperties.getLocalProperties());         
        start();            
        
        screenPlayer = new ScreenPlayer(this);
        eventsListener = new EventsListener(this);
        viewerGUI = new ViewerGUI(this);        
    }
    
    public Recorder(Server server, Config config) {
        this.server = server; 
        this.config = config;
        side = Commons.serverSide;
        
        init(); 
        viewerOptions = new ViewerOptions(InetAdrUtility.getLocalHost(),
                HostProperties.getLocalProperties());
        
        Start();
    }
    
    public void init () {            
        clipbrdUtility = new ClipbrdUtility();      
        fileManager = new FileManager(this);                    
    }    

    @Override
    public void  run() {
        while (true) {
            Wait();
            
            while (recording && !pause && !hold) {                
                viewer.sendData();  
                viewer.receiveData();
                Sleep();
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
        synchronized(this) {
            try {
                // usefull for reducing CPU usage
                sleep(viewerOptions.getRefreshRate());
            }
           catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void Stop() {
        recording = false;   
        pause = true;
        hold = true;
        viewOnly = false;
        clipbrdUtility.removeFlavorListener();     
        if (viewerGUI.isFullScreenMode())
            viewerGUI.changeFullScreenMode();     
        if ((config.reverseConnection && side == Commons.viewerSide) ||
                !config.reverseConnection)  {
            eventsListener.removeAdapters(true);
            //viewerOptions.getCapture().clearScreen();
        }

       if (config.reverseConnection && side == Commons.viewerSide)   
            Viewer.removeViewer(this);
    }
    
    public void terminate () {
        Stop();
        if (viewer != null) {
            viewer.disconnect();
            viewer.interrupt();
        }
        interrupt();
    }
    
    public void Start() {                 
        if (config.reverseConnection && side == Commons.serverSide) {            
            if (!server.isConnected())
                if (server.connect() == -1) return;             
        }
        
        if (!config.reverseConnection) {  
            if (!viewer.isConnected())
                if (viewer.connect() == -1) return; 
        }

        if ((config.reverseConnection && side == Commons.viewerSide) ||
                !config.reverseConnection)            
            eventsListener.addAdapters(true);

        clipbrdUtility.addFlavorListener();
      
        recording = true;            
        pause = false;   
        hold = false;
        viewOnly = false;     

        if (!config.reverseConnection) {
            viewer.setOptions(viewerOptions.getOptions());
            Notify();
        }         
    }
    
    public boolean isRecording () { 
        return recording;
    }
    
    public boolean isPaused() {
       // if (config.reverseConnection && side == Commons.viewerSide)
      //      return Viewer.isViewerPaused(this);
     //   else
            return pause;
    }
    
    public void setPause(boolean bool) {
        if (config.reverseConnection && side == Commons.viewerSide)
            Viewer.setPause(this, bool);
        else {
            pause = bool;
            if (pause) {
                   eventsListener.removeAdapters(true);
                   //viewerOptions.getCapture().clearScreen();
            }
            else
            {            
                if (recording && !viewOnly)
                    eventsListener.addAdapters(true);
                if (recording) Notify();            
            }
        }
    }
    
    public void setViewOnly(boolean bool){
        viewOnly = bool;
        if (viewOnly)
            eventsListener.removeAdapters();
        else
        {
            if (recording && !pause)
                eventsListener.addAdapters();
        }
    }
    
    public void setHold (boolean bool) {
        hold = bool;
    }
    
    public boolean isViewOnly() {
        return viewOnly;
    }
}
