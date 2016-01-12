package jrdesktop;

/**
 * SystemProperties.java
 * @author benbac
 */

public class SystemProperties {

   public static void clear () {
        clearSSLProps();
        clearDefaultAdr();
        clearProxyProps();
   }

   public static void setSSLProps() {
        System.setProperty("javax.net.ssl.trustStore", Settings.trustStore); 
        System.setProperty("javax.net.ssl.trustStorePassword", "trustword"); 
        System.setProperty("javax.net.ssl.keyStore", Settings.keyStore); 
        System.setProperty("javax.net.ssl.keyStorePassword", "password");   
   }
    
   public static void clearSSLProps() {
        System.getProperties().remove("javax.net.ssl.trustStore"); 
        System.getProperties().remove("javax.net.ssl.trustStorePassword");         
        System.getProperties().remove("javax.net.ssl.keyStore"); 
        System.getProperties().remove("javax.net.ssl.keyStorePassword");               
    }

   public static void setDefaultAdr(String address) {
        System.setProperty("java.rmi.server.hostname", address);
   }

   public static void clearDefaultAdr() {
        System.getProperties().remove("java.rmi.server.hostname");
   }

   public static void setProxyProps(String server, int port) {
        System.setProperty("http.proxyHost", server);
        System.setProperty("http.proxyPort", String.valueOf(port));
   }

   public static void clearProxyProps() {
       System.getProperties().remove("http.proxyHost");
       System.getProperties().remove("http.proxyPort");
   }

    public static String getHomeDirectory () {
        return System.getProperty("user.home");
    }

    public static String getOS () {
        return System.getProperty("os.name") + ", " +
                System.getProperty("os.arch") + ", " +
                System.getProperty("os.version");
    }

    public static String getUserName () {
        return System.getProperty("user.name");
    }

    public static String getCurrentDir () {
        return System.getProperty("user.dir");
    }

    public static float getJavaVersion () {
        try {
            return Float.parseFloat(
                    System.getProperty("java.version").substring(0, 3));
        } catch (NumberFormatException e) {
            return 1.5f;
        }
    }
}
