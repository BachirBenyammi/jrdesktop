/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jrdesktop.viewer;

import java.awt.dnd.DropTarget;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import jrdesktop.viewer.FileMng.FilesDropTargetListener;

/**
 *
 * @author Admin
 */
public class EventsListener {

    private final ScreenPlayer player;
    private boolean isActive = false;

    private KeyAdapter keyAdapter;
    private MouseAdapter mouseAdapter;
    private MouseWheelListener mouseWheelListener;
    private MouseMotionAdapter mouseMotionAdapter;
    private FilesDropTargetListener filesDropTargetListener;
    private DropTarget dropTarget;

    private ArrayList<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
    private ArrayList<MouseEvent> mouseEvents = new ArrayList<MouseEvent>();

    public EventsListener (Recorder recorder) {
        player = recorder.screenPlayer;

        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyEvents.add(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyEvents.add(e);
            }
        };

        mouseWheelListener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                mouseEvents.add(e);
            }
        };

        mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseEvents.add(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                 if (player.isSelecting) {
                    player.destx = e.getX ();
                    player.desty = e.getY ();
                 }
                 else
                    mouseEvents.add(e);
            }
        };

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (player.isSelecting) {
                    player.destx = player.srcx = e.getX ();
                    player.desty = player.srcy = e.getY ();
                }
                else
                    mouseEvents.add(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                 player.doneSelecting();
                 mouseEvents.add(e);
            }
        };

        filesDropTargetListener = new FilesDropTargetListener(recorder);
        dropTarget = new DropTarget();
        dropTarget.setComponent(player);
    }

   public ArrayList<MouseEvent> getMouseEvents() {
        ArrayList<MouseEvent> _mouseEvents = new ArrayList<MouseEvent>();

        synchronized(mouseEvents){
            _mouseEvents = mouseEvents;
            mouseEvents = new ArrayList<MouseEvent>();
        }

        return _mouseEvents;
    }

    public ArrayList<KeyEvent> getKeyEvents () {
        ArrayList<KeyEvent> _keyEvents = new ArrayList<KeyEvent>();

        synchronized(keyEvents){
            _keyEvents = keyEvents;
            keyEvents = new ArrayList<KeyEvent>();
        }
        
        return _keyEvents;
    }

    public boolean isActive () {
        return isActive;
    }

    public void addAdapters() {
        addAdapters(false);
    } 

    public void addAdapters(boolean all) {
        if (isActive) return;
        player.addKeyListener(keyAdapter);
        player.addMouseWheelListener(mouseWheelListener);
        player.addMouseMotionListener(mouseMotionAdapter);
        player.addMouseListener(mouseAdapter);
        if (all)
            try {
                dropTarget.addDropTargetListener(filesDropTargetListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        isActive = true;
    }

    public void removeAdapters() {
        removeAdapters(false);
    }

    public void removeAdapters(boolean all) {
        if (!isActive) return;
        player.removeKeyListener(keyAdapter);
        player.removeMouseWheelListener(mouseWheelListener);
        player.removeMouseMotionListener(mouseMotionAdapter);
        player.removeMouseListener(mouseAdapter);
        if (all)
            dropTarget.removeDropTargetListener(filesDropTargetListener);
        isActive = false;
    }
}
