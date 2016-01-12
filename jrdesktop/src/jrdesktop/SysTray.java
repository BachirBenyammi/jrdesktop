package jrdesktop;

import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.*;

import jrdesktop.rmi.server.RMIServer;
import jrdesktop.server.Server;
import jrdesktop.viewer.ConnectionDialog;

/**
 * SysTray.java
 * @author benbac
 */
public class SysTray {
  
    private static MenuItem serverItem;
    private static TrayIcon trayIcon;
    private static boolean enabled = false;
    public static byte customMsg = 0;

    public static void updateServerStatus(byte msgType) {
        updateServerStatus(msgType, null);
    }
        
    public static void updateServerStatus(byte msgType, String msg) {
        if (!isSupported() || enabled == false) return;
        
        switch (msgType) {
            case Commons.SERVER_RUNNING:
                serverItem.setLabel("Stop Server");
                if (Server.isRunning()) {
                    if (Server.getViewersCount() != 0)
                        trayIcon.setImage(new ImageIcon(Commons.ALIVE_ICON).getImage());
                    else
                        trayIcon.setImage(new ImageIcon(Commons.WAIT_ICON).getImage());
                }
                trayIcon.setToolTip("jrdesktop [Server running]\n" + 
                        RMIServer.serverConfig.server_address);              
                break;
            case Commons.SERVER_NOT_RUNNING:
                serverItem.setLabel("Start");
                trayIcon.setImage(new ImageIcon(Commons.IDLE_ICON).getImage());
                trayIcon.setToolTip("jrdesktop [Server stopped]\n" + 
                        RMIServer.serverConfig.server_address);
                break;          
            case Commons.SERVER_STARTED:
                serverItem.setLabel("Stop");
                trayIcon.displayMessage("Connection status", "Server Started !!",
                        MessageType.INFO);
                trayIcon.setImage(new ImageIcon(Commons.WAIT_ICON).getImage());
                trayIcon.setToolTip("jrdesktop [Server running]\n" + 
                        RMIServer.serverConfig.server_address);
                break;
            case Commons.CONNECTION_FAILED:
                trayIcon.displayMessage("Connection status", "Connection Failed !!",
                        MessageType.ERROR);
                break;
            case Commons.SERVER_STOPPED:
                serverItem.setLabel("Start");
                trayIcon.displayMessage("Connection status", "Server Stopped !!",
                        MessageType.INFO);
                trayIcon.setImage(new ImageIcon(Commons.IDLE_ICON).getImage());
                trayIcon.setToolTip("jrdesktop [Server stopped]\n" + 
                        RMIServer.serverConfig.server_address);
                break;
                
            default:
            trayIcon.displayMessage("File Reception:", msg, MessageType.INFO);      
        }
        
        serverItem.setEnabled(true);
        if (msgType > 0) 
            main.updateStatus();
    }

    public static void displayViewer(String viewer, int size, boolean connected) {
        if (!isSupported() || enabled == false) return;

        if (connected) {
            trayIcon.displayMessage("Viewer details", viewer + " connected !!",
                    MessageType.INFO);
            if (size == 0) {
                trayIcon.setImage(new ImageIcon(Commons.ALIVE_ICON).getImage());
            }
        } else {
            trayIcon.displayMessage("Viewer details", viewer + " disconnected !!",
                    MessageType.INFO);
            if (size == 0) {
                trayIcon.setImage(new ImageIcon(Commons.WAIT_ICON).getImage());
            }
        }
    }

    public static boolean isSupported() {
        return (Commons.java_version < 1.6f ? false : SystemTray.isSupported());
    }

    public static boolean isEnabled() {
        return enabled;
    }   

    public static void close() {
        if (!isSupported() || !enabled) return;
        enabled = false;        
        final SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
    }
    
    public static void display() {
        if (!isSupported() || enabled) return;
        enabled = true;
        Runnable runner = new Runnable() {
            public void run() {
                final SystemTray tray = SystemTray.getSystemTray();
                PopupMenu popup = new PopupMenu();
                trayIcon = new TrayIcon(new ImageIcon(Commons.IDLE_ICON).getImage(),
                        "jrdesktop", popup);
                trayIcon.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        main.displayTab(0);
                    }
                });

                MenuItem item = new MenuItem("Open jrdesktop");
                item.setFont(new Font(null, Font.BOLD, 12));
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        main.displayTab(0);
                    }
                });
                
                popup.add(item);  
                
                item = new MenuItem("-");
                popup.add(item);

                
                serverItem = new MenuItem("Start");

                serverItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        serverItem.setEnabled(false);
                        if (Server.isRunning()) {
                            Server.Stop();
                        } else {
                            Server.Start();
                        }
                    }
                });
                popup.add(serverItem);

                item = new MenuItem("Configuration ...");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        main.displayTab(1);
                    }
                });
                popup.add(item);
                
                item = new MenuItem("Active Connections");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        main.displayTab(2);
                    }
                });
                popup.add(item);

                item = new MenuItem("-");
                popup.add(item);

                item = new MenuItem("New connection ...");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        ConnectionDialog.main(null);
                    }
                });
                popup.add(item);

                item = new MenuItem("-");
                popup.add(item);

                item = new MenuItem("Settings ...");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        main.displayTab(3);
                    }
                });
                popup.add(item);
                
                item = new MenuItem("About");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        main.displayTab(4);
                    }
                });
                popup.add(item);

                item = new MenuItem("Exit");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        main.quit();
                    }
                });
                popup.add(item);

                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    System.err.println("Can't add to tray");
                }
                
                if (Server.isRunning())
                    SysTray.updateServerStatus(Commons.SERVER_RUNNING);
                else
                    SysTray.updateServerStatus(Commons.SERVER_NOT_RUNNING);
            }
        };
        EventQueue.invokeLater(runner);
    }
}
