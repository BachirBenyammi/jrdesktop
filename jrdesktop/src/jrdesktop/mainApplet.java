package jrdesktop;

import jrdesktop.server.ActiveConnectionsJPanel;
import jrdesktop.server.ConfigJPanel;
import jrdesktop.utilities.InetAdrUtility;

/**
 *
 * @author Admin
 */
public class mainApplet extends javax.swing.JApplet {

    private static mainApplet applet;

    private static HomeJPanel hjp;
    private static ConfigJPanel cjp;
    private static ActiveConnectionsJPanel acjp;
    private static SettingsJPanel sjp;
    private static AboutJPanel ajp;

    public void main () {
        Commons.init();
        Settings.displayMode = Commons.DISPLAY_MODE_APPLET;
        analyseAppletArgs();
        Settings.loadConfig();
        Settings.applyConfig(true);
    }

    /** Initializes the applet MainJApplet */
    @Override
    public void init() {
        main();
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                 initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        applet = this;
    }

    public void analyseAppletArgs() {
        String home = getParameter("home");
        if (home != null) {
            Settings.updateFilePaths(home);
            Settings.loadConfig();
        }

        String noexit = getParameter("noexit");
        boolean noSystExit = (noexit != null ? Boolean.valueOf(noexit):
            Settings.exitDisabled);

        String noicon = getParameter("noicon");
        boolean noTrayicon = (noicon != null ? Boolean.valueOf(noicon):
            Settings.systrayDisabled);

        String proxy_server = getParameter("pxserver");
        if (proxy_server == null)
            proxy_server = Settings.proxyServer;

        String proxyport = getParameter("pxport");
        int proxy_port = ( proxyport != null ? Integer.valueOf(proxyport):
            Settings.proxyPort);

        String downloads = getParameter("downloads");
        if (downloads == null)
            downloads = Settings.downloadsDir;

        String lookAndFeel = getParameter("lookAndFeel");
        if (lookAndFeel == null)
            lookAndFeel = Settings.lookAndFeel;

            boolean proxy_manual =
                    !(proxy_server.equals("127.0.0.1") && proxy_port == 80);
            Settings.setConfig(false, noTrayicon, noSystExit, proxy_manual,
                    proxy_server, proxy_port, downloads, lookAndFeel);

            String server = getParameter("server");
            if (server == null)
                server = Commons.defaultServerAddress;

            String default__ = getParameter("default");
            boolean default_ = ( default__ != null ? Boolean.valueOf(default__) :
                Commons.defaultAddress);
            
            String multihome_ = getParameter("multihome");
            boolean multihome = ( multihome_ != null ? Boolean.valueOf(multihome_) :
                Commons.defaultMultihome);

            String port_ = getParameter("port");
            int port = (port_ != null ? InetAdrUtility.getPort(port_,
                    Commons.defaultServerPort) :
                Commons.defaultServerPort);

            String httpPort_ = getParameter("httpPort");
            int httpPort = (httpPort_ != null ? InetAdrUtility.getPort(httpPort_,
                    Commons.defaultHttpPort) :
                Commons.defaultHttpPort);

            String username = getParameter("username");
            if (username == null)
                username = Commons.defaultUsername;

            String password = getParameter("password");
            if (password == null)
                password = Commons.defaultPassword;

            String ssl_ = getParameter("ssl");
            boolean ssl = ( ssl_ != null ? Boolean.valueOf(ssl_) :
                Commons.defaultSSL);

            String reverse_ = getParameter("reverse");
            boolean reverse = ( reverse_ != null ? Boolean.valueOf(reverse_) :
                Commons.reverseConnection);

            String config = getParameter("config");
            if (config == null)
                config = Commons.DEFAULT_CONFIG;

            String side = getParameter("side");
            if (side != null)
                if (side.equals("server")) {
                    if (config.equals(Commons.DEFAULT_CONFIG))
                        main.serverConfig = new Config (Commons.serverSide,
                            config, server, default_, multihome, port, httpPort,
                            username, password, ssl, reverse);
                    else
                        main.serverConfig = new Config(Commons.serverSide, config);
                    main.startServer();
                }
                else if (side.equals("viewer")) {
                    if (config.equals(Commons.DEFAULT_CONFIG))
                        main.viewerConfig = new Config (Commons.viewerSide, config,
                        server, port, username, password, ssl, reverse);
                    else
                        main.viewerConfig = new Config(Commons.viewerSide, config);
                    main.startViewer();
                }
    }
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hjp = new HomeJPanel();
        cjp = new ConfigJPanel();
        acjp = new ActiveConnectionsJPanel();
        sjp = new SettingsJPanel();
        ajp = new AboutJPanel();
        jTabbedPane = new javax.swing.JTabbedPane();

        setStub(null);

        jTabbedPane.addTab("Home", new javax.swing.ImageIcon(getClass().getResource("/jrdesktop/images/gohome.png")), hjp);
        jTabbedPane.addTab("Config", new javax.swing.ImageIcon(getClass().getResource("/jrdesktop/images/configure.png")), cjp);
        jTabbedPane.addTab("Connections", new javax.swing.ImageIcon(getClass().getResource("/jrdesktop/images/connect_established.png")), acjp);
        jTabbedPane.addTab("Settings", new javax.swing.ImageIcon(getClass().getResource("/jrdesktop/images/settings.png")), sjp);
        jTabbedPane.addTab("About", new javax.swing.ImageIcon(getClass().getResource("/jrdesktop/images/about.png")), ajp);
        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneStateChanged
        switch (jTabbedPane.getSelectedIndex()) {
           case 0: hjp.updateStatus(); break;
           case 2: acjp.updateList(); break;
       }
        //setTitle(jTabbedPane.getTitleAt(jTabbedPane.getSelectedIndex()) +
        //        " - jrdesktop " + Commons.jrdesktop_version);
}//GEN-LAST:event_jTabbedPaneStateChanged

    public static void displayTab(int index) {
        jTabbedPane.setSelectedIndex(index);
    }

    public static void updateStatus() {
        if (hjp != null)
            hjp.updateStatus();
    }

    public static void close () {
        applet.destroy();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTabbedPane jTabbedPane;
    // End of variables declaration//GEN-END:variables

}
