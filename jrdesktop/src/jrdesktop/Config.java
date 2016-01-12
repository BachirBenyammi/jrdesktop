package jrdesktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import jrdesktop.utilities.FileUtility;
import jrdesktop.utilities.PasswordUtility;

/**
 * Config.java
 * @author benbac
 */

public class Config {

    public String server_address = Commons.defaultServerAddress;
    public int server_port = Commons.defaultServerPort;
    public int http_port = Commons.defaultHttpPort;
    public String username = Commons.defaultUsername;
    public String password = Commons.defaultPassword;    
    public boolean ssl_enabled = Commons.defaultSSL;
    public boolean multihomed_enabled = Commons.defaultMultihome;
    public boolean default_address = Commons.defaultAddress;
    public boolean reverseConnection = Commons.reverseConnection;
    
    public Config (boolean side, String name) {
        if (new File(name).canRead())  {
            loadData(name);
            storeConfiguration(side, "");
        }
        else
            loadConfiguration(side, name);
    }
    
    public Config(boolean side, String name, String Address, 
            boolean default_address, boolean Multihomed_enabled, int Port, 
            int httpPort, String Username, String Password, boolean Ssl_enabled,
            boolean reverse_connection) { 
        setConfiguration(side, name, Address, default_address, Multihomed_enabled,
            Port, httpPort, Username, Password, Ssl_enabled, reverse_connection);
    }    
    
    public Config(boolean side, String name, String Address, int Port, 
            String Username, String Password, boolean Ssl_enabled,
            boolean reverse_connection) { 
        setConfiguration(side, name, Address, Port, Username, Password, 
                Ssl_enabled, reverse_connection);
    } 
    
    public void loadData(String filename) {
            try {
                Properties properties = new Properties();            
                properties.load(new FileInputStream(filename));
                server_address = properties.get("server-address").toString(); 
                server_port = Integer.valueOf(properties.get("server-port").toString());
                http_port = Integer.valueOf(properties.get("http-port").toString());    
                username = properties.get("username").toString();       
                password = PasswordUtility.decodeString(properties.get("password").toString());
                ssl_enabled = Boolean.valueOf(properties.getProperty("ssl-enabled"));
                multihomed_enabled = Boolean.valueOf(properties.getProperty("multihomed-enabled"));
                default_address = Boolean.valueOf(properties.getProperty("default-address"));
                reverseConnection = Boolean.valueOf(properties.getProperty("reverse-connection"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }        
    }
    
    public void loadConfiguration(boolean side, String name) {
        String config_file = FileUtility.getConfigFilename(side, name);
        if (new File(config_file).canRead())      
            loadData(config_file);
       else
            storeConfiguration(side, name);   
    }
    
    public void storeConfiguration (boolean side, String name) {
        String config_file = FileUtility.getConfigFilename(side, name);
        
        try {
            new File(Settings.home).mkdirs();
            new File(config_file).createNewFile();        
            Properties properties = new Properties();
            properties.put("server-address", server_address);
            properties.put("server-port", String.valueOf(server_port));
            properties.put("http-port", String.valueOf(http_port));
            properties.put("username", username);
            properties.put("password", PasswordUtility.encodeString(password));      
            properties.put("ssl-enabled", String.valueOf(ssl_enabled));
            properties.put("multihomed-enabled", String.valueOf(multihomed_enabled));
            properties.put("default-address", String.valueOf(default_address));
            properties.put("reverse-connection", String.valueOf(reverseConnection));
        
            properties.store(new FileOutputStream(config_file), 
                "jrdesktop " + FileUtility.getSide(side) + " configuration file"); 
        } catch (Exception e) {
            e.printStackTrace();
        }            
    }    
    
    public void setConfiguration(boolean side, String name, String Address, 
            boolean Default_Address, boolean Multihomed_enabled, int Port, 
            int HttpPort, String Username, String Password, boolean Ssl_enabled,
            boolean reverse_connection) { 

        server_address = Address; 
        default_address = Default_Address;
        multihomed_enabled = Multihomed_enabled;
        server_port = Port;
        http_port = HttpPort;
        username = Username;
        password = Password;            
        ssl_enabled = Ssl_enabled;        
        reverseConnection = reverse_connection;        
                
        storeConfiguration(side, name);       
    }    
    
    public void setConfiguration(boolean side, String name, String address,
            int port, String Username, String password, boolean ssl_enabled,
            boolean reverse_connection) { 
        
        setConfiguration(side, name, address, false, false, port, http_port,
                Username, password, ssl_enabled, reverse_connection);
    }
}