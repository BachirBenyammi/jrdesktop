package jrdesktop.rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.JOptionPane;

import jrdesktop.rmi.server.ServerInterface;
import jrdesktop.utilities.FileUtility;
import jrdesktop.Config;
import jrdesktop.Settings;
import jrdesktop.SystemProperties;
import jrdesktop.utilities.InetAdrUtility;
import jrdesktop.utilities.PasswordUtility;

/**
 * RMIClient.java
 * @author benbac
 */

public class RMIClient {
    
    public Config clientConfig;
    private Registry registry; 
    public ServerInterface rmiServer;      
    
    private int index = -1;
    private boolean connected = false;
               
    public RMIClient (Config config) {
        clientConfig = config;
        if (clientConfig.ssl_enabled) {     
            FileUtility.extractFile(Settings.keyStore);
            FileUtility.extractFile(Settings.trustStore);
            SystemProperties.setSSLProps();
        }
        else
            SystemProperties.clearSSLProps();
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void Start() { 
        connect();
        if (!connected)
            disconnect();
    }
    
    public int connect() {  
        connected = false;

        try {
           if (clientConfig.ssl_enabled)
                registry = LocateRegistry.getRegistry(clientConfig.server_address, 
                        clientConfig.server_port, new SslRMIClientSocketFactory());
            else
                registry = LocateRegistry.getRegistry(clientConfig.server_address, 
                        clientConfig.server_port);
           
           rmiServer = (ServerInterface) registry.lookup("jrdesktop");

           /* rmiServer = (ServerInterface) Naming.lookup(
                    "rmi://" + clientConfig.server_address + ":" +
                   clientConfig.server_port + "/jrdesktop");*/

            index = rmiServer.startViewer(InetAdrUtility.getLocalHost(),
                    clientConfig.username, 
                    PasswordUtility.encodeString(clientConfig.password), 
                    clientConfig.reverseConnection);
        
            switch (index ) {
                case -1 :
                     JOptionPane.showMessageDialog(null, "Authentication failed !!", 
                        "Error !!", JOptionPane.ERROR_MESSAGE);   
                    return -1;
                case -2 :
                     JOptionPane.showMessageDialog(null, "Reverse connection failed !!", 
                        "Error !!", JOptionPane.ERROR_MESSAGE);                        
                    return -1;                    
            }
            
            displayStatus();
            connected = true;
            return index;
       } catch (Exception e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, e.getMessage(), "Error !!",
                 JOptionPane.ERROR_MESSAGE);
           return -1;
       }     
    }
    
    public void disconnect() {
        connected = false;
        try {
            if (rmiServer != null && index > -1) {
                rmiServer.stopViewer(index);                
                //UnicastRemoteObject.unexportObject(rmiServer, true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } 
      rmiServer = null;
      registry = null;
    } 
    
    public void displayStatus() {
        boolean auth = (clientConfig.username.length() != 0) || 
                (clientConfig.password.length() != 0);             
        System.out.println("Viewer connected to " + rmiServer +                                   
            "\n\tauthentication: " + (auth == true ? "enabled" : "desabled") +
            "\n\tencryption: " + (clientConfig.ssl_enabled == true ? "enabled" : "desabled") +
            "\n\treverse connection: " + 
            (clientConfig.reverseConnection == true ? "enabled" : "disabled"));            
    }
}
