package jrdesktop;

import javax.swing.JOptionPane;

import jrdesktop.server.Server;
import jrdesktop.viewer.Viewer;
import jrdesktop.utilities.InetAdrUtility;
import jrdesktop.utilities.PasswordUtility;
import jrdesktop.viewer.ConnectionDialog;

/**
 * main.java
 * @author benbac
 */

public class main  {

    public static Config serverConfig, viewerConfig;
    public static int activeConnection = 0;

    public static void main (String args[]) {
        Commons.init();
        if (!analyseCMDArgs(args));
            Settings.loadConfig();
        Settings.applyConfig(true);
    }

    public static void loadExtension (String args[]) {
        Commons.init();
        Settings.displayMode = Commons.DISPLAY_MODE_EXTENSION;
        if (!analyseCMDArgs(args));
            Settings.loadConfig();
        Settings.applyConfig(true);
    }

    public static boolean analyseCMDArgs(String args[]) {
        if (args == null) return false;
        String arg;        
        if (args.length > 0) {

            String home = Settings.home; // Commons.HOME_DIR;
            for (int i=0; i<args.length; i++) {
                arg = args[i];
                if (arg.startsWith("--home:"))
                    home = arg.substring(7);  // set jrdesktop's home directory
            }
            Settings.updateFilePaths(home);
            Settings.loadConfig();

            boolean hideMainWindow = Settings.guiDisabled; // Commons.hideMainWindow;
            boolean noexit = Settings.exitDisabled;
            boolean noicon = Settings.systrayDisabled; // Commons.noSysTray;
            String proxy_server = Settings.proxyServer; // Commons.proxyServer;
            int proxy_port = Settings.proxyPort; //Commons.proxyPort;
            String downloads = Settings.downloadsDir;  // Commons.downloadsLocation;
            String lookAndFeel = Settings.lookAndFeel; // Commons.LOOK_AND_FILL_SYSTEM;

            for (int i=0; i<args.length; i++) {
                arg = args[i];
                if (arg.startsWith("--hide"))
                    hideMainWindow = true;       // hide main window
                else if (arg.startsWith("--noicon"))
                    noicon = true;              // hide tray icon
                else if (arg.startsWith("--noexit"))
                    noexit = true;              // disable exit
                else if (arg.startsWith("--pxport:"))
                    proxy_port = Integer.valueOf(arg.substring(9)); // get proxy port
                else if (arg.startsWith("--pxserver:"))
                    proxy_server = arg.substring(11);  // get proxy server
                else if (arg.startsWith("--downloads:"))
                    downloads = arg.substring(12); // set downloads location
                else if (arg.startsWith("--lookAndFeel:"))
                    lookAndFeel = arg.substring(14); // set look And Feel
            }

            boolean proxy_manual =
                    !(proxy_server.equals("127.0.0.1") && proxy_port == 80);
            Settings.setConfig(hideMainWindow, noicon, noexit, proxy_manual,
                    proxy_server, proxy_port, downloads, lookAndFeel);
            
            int side = -1;
            String config = Commons.DEFAULT_CONFIG;
            
            arg = args[0];          
            if (arg.equals("server"))
                side = 0;
            else if (arg.equals("viewer"))
                side = 1;
            else if (arg.equals("pwd-gen"))
                    PasswordUtility.passwordGenerator();
            else if (arg.equals("--help") || arg.equals("-?")) 
                displayHelp();  // display usage information
            else if (arg.equals("--version") || arg.equals("-v")) 
                displayHelp(false);  // display version information
            
            String server = Commons.defaultServerAddress;
            boolean default_ = Commons.defaultAddress;
            boolean multihome = Commons.defaultMultihome;
            int port = Commons.defaultServerPort;
            int httpPort = Commons.defaultHttpPort;
            String username = Commons.defaultUsername;
            String password = Commons.defaultPassword;
            boolean ssl = Commons.defaultSSL;
            boolean reverse = Commons.reverseConnection;

            for (int i=1; i<args.length; i++) {
                arg = args[i];

                if (arg.startsWith("--conf:"))
                    config = arg.substring(7);
                else if (arg.startsWith("-a:"))
                    server = arg.substring(3);
                else if (arg.startsWith("-p:"))
                    port = InetAdrUtility.getPort(arg.substring(3),
                            Commons.defaultServerPort);
                else if (arg.startsWith("-http:"))
                    httpPort = InetAdrUtility.getPort(arg.substring(3),
                            Commons.defaultHttpPort);
                else if (arg.startsWith("-u:"))
                    username = arg.substring(3);
                else if (arg.startsWith("-w:"))
                    password = arg.substring(3);
                else if (arg.startsWith("-d"))
                    default_ = true;
                else if (arg.startsWith("-s"))
                    ssl = true;
                else if (arg.startsWith("-i"))
                    multihome = true;
                else if (arg.startsWith("-r"))
                    reverse = true;
            }

            if (side == 0) {
                if (config.equals(Commons.DEFAULT_CONFIG))
                    serverConfig = new Config (Commons.serverSide, config,
                            server, default_, multihome, port, httpPort,
                            username, password, ssl, reverse);
                else
                    serverConfig = new Config(Commons.serverSide, config);
                startServer();
            }
            else if (side == 1) {
                if (config.equals(Commons.DEFAULT_CONFIG))
                    viewerConfig = new Config (Commons.viewerSide, config, server,
                        port, username, password, ssl, reverse);
                else
                    viewerConfig = new Config(Commons.viewerSide, config);
                startViewer();
            }
        }
        return args.length != 0;
    }

    public static void displayHelp() {
        displayHelp(true);
    }

    public static void displayHelp(boolean full) {
        System.out.println(
            "jrdesktop - Java Remote Desktop.\n" +
            "A cross-platform software that provides remote view and control of a computer.\n" +
            "http://jrdesktop.net/\n\n");
        if (full)
            System.out.println(
            "Usage: java -jar jrdesktop.jar <command> [options]\n\n" + 
            
            "Commands:\n" +          
            "   server | viewer             start server (or viewer) using default parameters " +
            "(address: 127.0.0.1, port: " + Commons.defaultServerPort + ").\n" +
            "   server | viewer [options]   start server (or viewer) using " +
            "a specific options.\n" +
            "   pwd-gen                     password generation utility.\n" +   
            "   --version | -v              display version information.\n" +
            "   --help | -?                 display usage information.\n\n" +
            
            "Options:\n" + 
            "   -a:address              servers address.\n" +
            "   -d                      set as the default JVM IP address.\n" +  
            "   -i                      auto detect server IP address.\n" +            
            "   -p:port                 servers port.\n" +
            "   -http:port              servers http port.\n" +
            "   -u:user                 user's name.\n" +
            "   -w:pwd                  user's password.\n" +
            "   -s                      secured connection using SSL.\n" +
            "   -r                      reverse connection. \n" +
            "   --hide                  hide main window. \n" +
            "   --noicon                disable system tray icon. \n" +
            "   --noexit                disable system exit. \n" +
            "   --pxserver:address      proxy servers address. \n" +
            "   --pxport:port           proxy servers port. \n" +
            "   --home:directory        jrdesktops home directory. \n" +
            "   --conf:file             load configuration file. \n" +
            "   --downloads:directory   downloads location. \n" +
            "   --lookAndFeel:laf       look and feel theme. \n"
        );
        else
            System.out.println(
                Commons.jrdesktop_version + " " + Commons.jrdesktop_build_date);
        exit();
    }    
    
    public static void startServer() {        
        if (serverConfig.reverseConnection)
            Viewer._Start(serverConfig);
        else
            Server.Start(serverConfig);
    }    
    
    public static void startViewer() {        
        if (viewerConfig.reverseConnection)
            new Server(viewerConfig)._Start();
        else
            new Viewer(viewerConfig).Start();
    }             

    public static void updateStatus() {
        if (Settings.displayMode == Commons.DISPLAY_MODE_APPLET)
            mainApplet.updateStatus();
        else
            mainFrame.updateStatus();
    }

    public static void displayTab(int index) {
       if (Settings.displayMode == Commons.DISPLAY_MODE_APPLET)
            mainApplet.displayTab(index);
        else
            mainFrame.displayTab(index);
    }

    public static void exit() {
        if (Settings.exitDisabled) {
            if (activeConnection > 0) {
                 JOptionPane.showMessageDialog(null,
                        "Please close all active connections first !!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } 

       if (serverConfig != null && serverConfig.reverseConnection) {
            if (Viewer.isRunning())
                Viewer._Stop();
       } else {
            if (Server.isRunning())
                Server.Stop();
       }
        SystemProperties.clear();
        System.setSecurityManager(null);

        if (Settings.exitDisabled)
            close();
        else
            System.exit(0);
    }

    public static void close () {
        if (Settings.displayMode == Commons.DISPLAY_MODE_APPLET) {
           mainApplet.close();
           JOptionPane.showConfirmDialog(null, "Please close the browser's window to exit this application", "Information",
                   JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION);
        }
        else
            mainFrame.close();
        ConnectionDialog.close();
        DirDialog.close();
        SysTray.close();
    }

    public static void quit () {
        if (Settings.displayMode != Commons.DISPLAY_MODE_FRAME && !Settings.exitDisabled) {
            if (JOptionPane.showConfirmDialog(null, "jrdesktop is running as a " +
                (Settings.displayMode == Commons.DISPLAY_MODE_APPLET ? "web applet" : "Firefox extension") +
                ". If you chosse OK, you my lose all opened pages." +
                "\nYou may want instead to : \n   * Close jrdesktop using the window's close buttom; " +
                "\n   * Disable system exit as settings tab to have a normal shutdown of jrdesktop" +
                "\n\nAre you sure you want to continue ?", "Warning Dialog",
                JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
                    main.exit();
        } else
            if (JOptionPane.showConfirmDialog(null, "Exit application ?", "Confirm Dialog",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
                    main.exit();
    }
}