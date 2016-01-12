package jrdesktop.utilities;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.List;
import javax.swing.ImageIcon;

import jrdesktop.Commons;

/**
 * ClipbrdUtility.java
 * @author benbac
 */

public class ClipbrdUtility {
    
    private Clipboard clipboard;
    
    private FlavorListener flavorlistener;
    private Object object;
    
    private String txt = "jrdesktop";
    private ImageIcon img = new ImageIcon(Commons.ALIVE_ICON);  

    public ClipbrdUtility() {
        initClipboard();
        clipboard.addFlavorListener(flavorlistener);        
    }
    
    public void initClipboard() {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    
        flavorlistener = new FlavorListener() {
            public void flavorsChanged(FlavorEvent event) {
                try {                
                    Transferable content = clipboard.getContents(this);
                    if (content == null) return;
                        
                    if(content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        String newtxt = (String) clipboard.getData(DataFlavor.stringFlavor);
                        if (!txt.equals(newtxt)) {
                            txt = newtxt;
                            object = txt;
                        }
                    } 
                    else if (content.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                        Image image = (Image) clipboard.getData(DataFlavor.imageFlavor);                        
                        if (!img.getImage().equals(image)) {
                            img = new ImageIcon(image);
                            object = img;
                        }
                    }                             
                }    
                catch (Exception ex) {
                    ex.printStackTrace();
                }          
            }
        };
    }

    public boolean isEmpty() {
        return (object == null);
    }
    
   public void setText(String string) { 
       if (txt.equals(string)) return;
       txt = string;
       clipboard.setContents(new StringSelection(txt), null);     
    }
    
    public void setImage(ImageIcon image) {
        if (img.getImage().equals(image.getImage())) return;
        img = image;
        clipboard.setContents(new ImageSelection(img.getImage()), null);
    }   
    
    public void setContent(Object object) {
        if (object == null) return;
        
        if (object instanceof String)
            setText((String) object);    
        else if (object instanceof ImageIcon)
            setImage((ImageIcon) object);
    }
    
    public Object getContent() {
        Object obj = object;
        object = null;
        return obj;
    }
    
    public File[] getFiles() {
        File[] files = new File[]{};
        try {
            Transferable transferable = clipboard.getContents(this);
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List list = (List) clipboard.getData(DataFlavor.javaFileListFlavor);  
                files = (File[]) list.toArray(new File[list.size()]);//.clone();
            }
            else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String data = (String) clipboard.getData(
                    //new DataFlavor("text/uri-list;class=java.lang.String"));
                     new DataFlavor("application/x-java-serialized-object; class=java.lang.String"));
                List<File> list = FileUtility.textToFileList(data);
                files = (File[]) list.toArray(new File[list.size()]); //.clone();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
    
   public void addFlavorListener() {
        clipboard.addFlavorListener(flavorlistener);
   }
   
   public void removeFlavorListener() {
       clipboard.removeFlavorListener(flavorlistener);
   }  
    
    /*
    * Helper class to put an Image on a clipboard as DataFlavor.imageFlavor.
    */
    public static class ImageSelection implements Transferable {

        private final Image img;

        public ImageSelection(Image img) {
            this.img = img;
        }
        static DataFlavor[] flavors = new DataFlavor[]{DataFlavor.imageFlavor};

        public DataFlavor[] getTransferDataFlavors() {
            return (DataFlavor[]) flavors.clone();         
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.imageFlavor);
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return img;
        }
    } 
}
