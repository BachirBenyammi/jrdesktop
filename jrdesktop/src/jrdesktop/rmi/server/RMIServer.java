package jrdesktop.rmi.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.swing.JOptionPane;

import jrdesktop.Commons;
import jrdesktop.SystemProperties;
import jrdesktop.Config;
import jrdesktop.Settings;
import jrdesktop.rmi.client.MultihomeRMIClientSocketFactory;
import jrdesktop.utilities.FileUtility;
import jrdesktop.utilities.InetAdrUtility;

/**
 * RMIServer.java
 * @author benbac
 */

public class RMIServer {   
    
    private static Registry registry;
    private static ServerImpl serverImpl;
    public static Config serverConfig = new Config(Commons.serverSide, "");   
    
    public static boolean Start(Config config) {
        serverConfig = config;
        return Start();
    }
    
    public static boolean Start() {            
        if (serverConfig.ssl_enabled) {        
            FileUtility.extractFile(Settings.keyStore);
            FileUtility.extractFile(Settings.trustStore);
            SystemProperties.setSSLProps();
        }
        else
            SystemProperties.clearSSLProps();
        
        if (serverConfig.default_address)
            SystemProperties.setDefaultAdr(serverConfig.server_address);
        else
            SystemProperties.clearDefaultAdr();

        try {
            if (serverConfig.ssl_enabled)
                registry = LocateRegistry.createRegistry(serverConfig.server_port,
                        new SslRMIClientSocketFactory(),
                        new SslRMIServerSocketFactory(null, null, true));
            else
                registry = LocateRegistry.createRegistry(serverConfig.server_port);

            if (serverConfig.ssl_enabled && serverConfig.multihomed_enabled)
                serverImpl = new ServerImpl(
                        new MultihomeRMIClientSocketFactory(
                            new SslRMIClientSocketFactory(),
                            InetAdrUtility.getLocalIPAdresses()),
                        new SslRMIServerSocketFactory(null, null, true)/*,
                        serverConfig.server_port*/);
            else if (serverConfig.ssl_enabled && !serverConfig.multihomed_enabled)
                serverImpl = new ServerImpl(
                        new SslRMIClientSocketFactory(), 
                        new SslRMIServerSocketFactory(null, null, true),
                        serverConfig.server_port);
            else if (!serverConfig.ssl_enabled && serverConfig.multihomed_enabled) {
                serverImpl = new ServerImpl(
                        new MultihomeRMIClientSocketFactory(null,
                        InetAdrUtility.getLocalIPAdresses()), null/*,
                        serverConfig.server_port*/);
            }
            else if (!serverConfig.ssl_enabled && !serverConfig.multihomed_enabled)
                serverImpl = new ServerImpl(serverConfig.server_port);

            System.out.println(serverImpl);

            registry.rebind("jrdesktop", serverImpl);
            
          /* Naming.rebind("rmi://" + serverConfig.server_address + ":" +
                   serverConfig.server_port + "/jrdesktop", (ServerInterface) serverImpl);*/
      // }  catch (ExportException ee) {
            // registry already exists, we'll just use it.
        }catch (Exception e) {
            Stop();
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error !!",
                    JOptionPane.ERROR_MESSAGE);           
            return false;
        }   

        System.out.println(getStatus());    
        return true;
    }              
    
    public static void Stop() {
        try {            
            if (registry != null) {
                UnicastRemoteObject.unexportObject(registry, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  
        registry = null;
        serverImpl = null;
    }
    
    public static String getStatus() {
        boolean auth = (serverConfig.username.length() != 0) || 
            (serverConfig.password.length() != 0);
        String status = "Running ..." + 
            "\nat: " + serverConfig.server_address + ":" + serverConfig.server_port + 
            "\nauthentication: " + (auth == true ? "enabled" : "disabled") +
            "\nencryption: " + 
            (serverConfig.ssl_enabled == true ? "enabled" : "disabled") +
            "\nmultihomed: " + 
            (serverConfig.multihomed_enabled == true ? "enabled" : "disabled") +
            "\nreverse connection: " + 
            (serverConfig.reverseConnection == true ? "enabled" : "disabled");        
        return status;
    }
}
