package jrdesktop;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Hashtable;
import javax.swing.JOptionPane;

import jrdesktop.utilities.InetAdrUtility;

/**
 * HostProperties.java
 * @author benbac
 */
public class HostProperties {

    public static Hashtable getLocalProperties() {
        Toolkit tk = Toolkit.getDefaultToolkit();        
        Hashtable<String, Object> localProperties = new Hashtable<String, Object>();        
        localProperties.put("host-address", InetAdrUtility.getLocalHost().toString());
        localProperties.put("java.version", SystemProperties.getJavaVersion());
        localProperties.put("os", SystemProperties.getOS());
        localProperties.put("user.name", SystemProperties.getUserName());
        localProperties.put("user.dir", SystemProperties.getCurrentDir());
        localProperties.put("screen.size", tk.getScreenSize());
        localProperties.put("screen.resolution", tk.getScreenResolution());

        return localProperties;
    }  
    
    public static void display(Hashtable prop) {
        Dimension size = (Dimension) prop.get("screen.size");
        
        JOptionPane.showMessageDialog(null,
            "Host: \t" + prop.get("host-address") + "\n\n" +        
            
            "Java version: \t" + prop.get("java.version") + "\n\n" +
            
            "OS: \t" + prop.get("os") + "\n\n" +
            
            "User's name: \t" + prop.get("user.name") + "\n" +
            "User's current directory: \t" + prop.get("user.dir") + "\n\n" +
            
            "Screen resolution: \t" + 
            String.valueOf(size.width) + "x" + String.valueOf(size.height) + "\n" +               
            "Screen size: \t" + prop.get("screen.resolution").toString() + 
            " PPI (Pixels Per Inch)",
            "Remote host properties", JOptionPane.INFORMATION_MESSAGE);
    }    
}
