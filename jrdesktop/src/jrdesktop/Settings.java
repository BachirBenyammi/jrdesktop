package jrdesktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import javax.swing.JOptionPane;

import jrdesktop.utilities.LookAndFeelUtility;

/**
 * Settings.java
 * @author benbac
 */

public class Settings {
    
    public static boolean guiDisabled = false;
    public static boolean exitDisabled = false;
    public static boolean systrayDisabled = false;
    public static boolean proxyManual = false;
    public static String proxyServer = Commons.proxyServer;
    public static int proxyPort = Commons.proxyPort;
    public static String home = Commons.HOME_DIR;
    public static String downloadsDir = Commons.downloadsLocation;
    public static String configFile = Commons.CONFIG_FILE;
    public static String keyStore = Commons.KEY_STORE;
    public static String trustStore = Commons.TRUST_STORE;
    public static String lookAndFeel = Commons.LOOK_AND_FILL_SYSTEM;
    public static byte displayMode = Commons.DISPLAY_MODE_FRAME;

    public static void loadConfig() {
        if (new File(configFile).canRead())
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(configFile));
                lookAndFeel = properties.getProperty("Look-And-Feel");
                guiDisabled = Boolean.valueOf(properties.getProperty("GUI-Disabled"));
                exitDisabled = Boolean.valueOf(properties.getProperty("exit-Disabled"));
                systrayDisabled = Boolean.valueOf(properties.getProperty("Systray-Disabled"));  
                proxyManual = Boolean.valueOf(properties.getProperty("Proxy-Manual"));
                proxyServer = properties.getProperty("Proxy-Server");
                proxyPort = Integer.valueOf(properties.getProperty("Proxy-Port"));
                downloadsDir = properties.getProperty("Downlods-Location");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
       else {
            storeConfig();
       }
    }
    
    public static void storeConfig () {
        try {
            new File(home).mkdirs();
            new File(configFile).createNewFile();
            Properties properties = new Properties();
            properties.put("Look-And-Feel", lookAndFeel);
            properties.put("GUI-Disabled", String.valueOf(guiDisabled));
            properties.put("exit-Disabled", String.valueOf(exitDisabled));
            properties.put("Systray-Disabled", String.valueOf(systrayDisabled));
            properties.put("Proxy-Manual", String.valueOf(proxyManual));
            properties.put("Proxy-Server", proxyServer);
            properties.put("Proxy-Port", String.valueOf(proxyPort));
            properties.put("Downlods-Location", downloadsDir);
                    
            properties.store(new FileOutputStream(configFile),
                "jrdesktop configuration file"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
    
    public static void setConfig (boolean nogui, boolean nosystray, boolean noexit,
            boolean proxy_Manual, String proxy_Server, int proxy_Port,
            String _downloadsDir, String _lookAndFeel) {

        guiDisabled = nogui;
        exitDisabled = noexit;
        systrayDisabled = nosystray;
        proxyServer = proxy_Server;
        proxyPort = proxy_Port;
        proxyManual = proxy_Manual;
        downloadsDir = _downloadsDir;
        lookAndFeel = _lookAndFeel;

        storeConfig();
    }

    public static void updateFilePaths (String _home) {
        try {
            home = new File(_home).getCanonicalPath().toString();
        }catch (Exception e) {
            e.printStackTrace();
            home = _home;
        }
        if (home.charAt(home.length()-1) != File.separatorChar)
            home += File.separatorChar;
        downloadsDir = home + "Downloads" + File.separatorChar;
        configFile = home + "jrdesktop.conf";
        keyStore = home + "keystore";
        trustStore = home + "truststore";
    }

    public static void applyConfig(boolean now) {
        if (!lookAndFeel.equals(LookAndFeelUtility.getCurrentLAF())) {
            LookAndFeelUtility.setLAF(lookAndFeel);
            LookAndFeelUtility.update();
        }

        if (proxyManual)
            SystemProperties.setProxyProps(proxyServer, proxyPort);
        else
            SystemProperties.clearProxyProps();

        if (now && !guiDisabled)
            if (Settings.displayMode != Commons.DISPLAY_MODE_APPLET)
                mainFrame.main(null);

        if (!SysTray.isSupported()) {
            if (now)
                System.out.println("Systray not supported !!");
            else
                JOptionPane.showMessageDialog(null, "Systray not supported !!",
                "Info", JOptionPane.ERROR_MESSAGE);
        } else {
            if (systrayDisabled)
                    SysTray.close();
             else 
                    SysTray.display();
        }
    }
}
