package jrdesktop.viewer.FileMng;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

import jrdesktop.utilities.FileUtility;
import jrdesktop.viewer.Recorder;

/**
 * FilesDropTargetListener.java
 * @author benbac
 */

public class FilesDropTargetListener implements DropTargetListener {

    private Recorder recorder;
    
    public FilesDropTargetListener( Recorder recorder) {
        this.recorder = recorder;
    }

    public void dragEnter(DropTargetDragEvent event) {
        if (!isDragAcceptable(event)) {
            event.rejectDrag();
            return;
        }
    }

    public void dragExit(DropTargetEvent event) {}

    public void dragOver(DropTargetDragEvent event) {}

    public void dropActionChanged(DropTargetDragEvent event) {
        if (!isDragAcceptable(event)) {
            event.rejectDrag();
            return;
        }
    }

    public void drop(DropTargetDropEvent event) {
        if (!isDropAcceptable(event)) {
            event.rejectDrop();
            return;
        }

        event.acceptDrop(DnDConstants.ACTION_COPY);

        Transferable content = event.getTransferable();
        
        if (content == null) return;
        
        if (recorder.isRecording())
            try {
                if (content.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {               
                    List list = (List) content.getTransferData(DataFlavor.javaFileListFlavor);
                    File[] files = (File[]) list.toArray(new File[list.size()]); //.clone();
                    if (files == null) return;
                    recorder.fileManager.setFiles(files);
                    recorder.viewer.SendFiles();
                }
                else if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String data = (String) content.getTransferData(
                           // new DataFlavor("text/uri-list;class=java.lang.String"));
                            new DataFlavor("application/x-java-serialized-object; class=java.lang.String"));
                    List<File> list = FileUtility.textURIListToFileList(data);
                    File[] files = (File[]) list.toArray(new File[list.size()]); //.clone();
                    if (files == null) return;
                    recorder.fileManager.setFiles(files);
                    recorder.viewer.SendFiles();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        event.dropComplete(true);
    }

    public boolean isDragAcceptable(DropTargetDragEvent event) {
        return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }

    public boolean isDropAcceptable(DropTargetDropEvent event) {
        return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }
}
