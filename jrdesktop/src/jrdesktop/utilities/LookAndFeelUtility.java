package jrdesktop.utilities;

import java.awt.Window;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jrdesktop.Commons;
import jrdesktop.mainFrame;

/**
 * 05-06-09 0.3
 * @author benabc
 */
public class LookAndFeelUtility {

    public static String getCurrentLAF() {
        return UIManager.getLookAndFeel().getName();
    }

    public static void setLAF(String laf) {
        String lookAndFeel = null;

        if (laf != null) {
            if (laf.equals(Commons.LOOK_AND_FILL_SYSTEM))
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            else if (laf.equals(Commons.LOOK_AND_FILL_WINDOWS))
                lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            else if (laf.equals(Commons.LOOK_AND_FILL_WINDOWS_CLASSIC))
                lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
            else if (laf.equals(Commons.LOOK_AND_FILL_GTK))
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            else if (laf.startsWith(Commons.LOOK_AND_FILL_METAL))
                lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
            else if (laf.equals(Commons.LOOK_AND_FILL_MOTIF))
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            else if (laf.equals(Commons.LOOK_AND_FILL_MACOSX))
                lookAndFeel = "apple.laf.AquaLookAndFeel";
            else
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();

            try {
                UIManager.setLookAndFeel(lookAndFeel);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String[] getLAFs() {
        ArrayList<String> LookAndFeels = new ArrayList<String>();
        for (UIManager.LookAndFeelInfo info : 
            UIManager.getInstalledLookAndFeels())
                LookAndFeels.add(info.getName());
        return LookAndFeels.toArray(new String[LookAndFeels.size()]);
    }

    public static void update() {
        for (Window window : mainFrame.getFrames())
            SwingUtilities.updateComponentTreeUI(window);
    }
}