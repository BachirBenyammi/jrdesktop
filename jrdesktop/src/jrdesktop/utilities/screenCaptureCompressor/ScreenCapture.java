package jrdesktop.utilities.screenCaptureCompressor;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import jrdesktop.Commons;
import jrdesktop.utilities.ImageUtility;

public class ScreenCapture {

    //private ImageWriter writer = null;
    //private ImageWriteParam param = null;
    private Screen screen = null;
    private int modified = 0;

    public ScreenCapture(float compression, int blocksRows, int blocksColumns) {
     //   initJpgCompression(compression);
        screen = new Screen(blocksRows, blocksColumns);
    }

    public boolean isEmpty() {
        return screen.getScreenBlocks().isEmpty();
    }

    public Vector<String> getChangedScreenBlocks() {
        return screen.getChangedScreenBlocks();
    }

    public void clearScreen() {
        screen.getChangedScreenBlocks().clear();

        for (int y = 0; y < screen.getRowScreenBlocks(); y++) 
            for (int x = 0; x < screen.getColumnScreenBlocks(); x++) {
                screen.addScreenBlock(
                        new ScreenBlock(screen.getScreenBlockName(y, x)));
                screen.getChangedScreenBlocks().addElement(
                        screen.getScreenBlockName(y, x));
            }
    }

    public int getTrafficPercent() {
        return ((modified * 100) / (screen.getRowScreenBlocks() * screen.getColumnScreenBlocks()));
    }

    public void takeAndSaveImage(Robot rt, float compressionQuality, int colorQuality,
            double scaleAbsolute, Rectangle screenRect) throws IOException {

        if (screenRect.equals(Commons.emptyRect))
             screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        BufferedImage screenImage = rt.createScreenCapture(screenRect);       

        int pieceWidth = (int) screen.getScreenBlockWidth();
        int pieceHeight = (int) screen.getScreenBlockHeight();

        int newW = (int) (pieceWidth * scaleAbsolute);
        int newH = (int) (pieceHeight * scaleAbsolute);

        BufferedImage subImage = new BufferedImage(newW, newH, colorQuality);

        screen.getChangedScreenBlocks().clear();

        modified = 0;

        for (int y = 0; y < screen.getRowScreenBlocks(); y++)
            for (int x = 0; x < screen.getColumnScreenBlocks(); x++) {

                String name = screen.getScreenBlockName(y, x);

                int startx = pieceWidth * x;
                int starty = pieceHeight * y;

                Graphics2D subImageGraphics = subImage.createGraphics();
                subImageGraphics.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                subImageGraphics.drawImage(
                        screenImage,0, 0, newW, newH, startx, starty,
                        startx + pieceWidth, starty + pieceHeight,null);

                if (screen.getScreenBlockImage(name).isModified(
                        ImageUtility.toByteArray(subImage, compressionQuality),
                        ScreenBlock.COMPARE_LENGTH)) {
                    screen.getChangedScreenBlocks().addElement(name);
                    modified++;
                }
            }
    }

    public byte[] getBlock(String name) {
        return screen.getScreenBlockImage(name).getData();
    }

    public void setBlock(ScreenBlock block) {
         screen.addScreenBlock(block);
    }

    public static long getChangedBlocksSize(HashMap<String, byte[]> blocks) {
        long size = 0;
        for (String blockName : blocks.keySet())
            size += blocks.get(blockName).length;
        return size;
    }

    public HashMap<String, byte[]> getChangedBlocks() {
        HashMap<String, byte[]> blocks = new HashMap<String, byte[]>();

        for (int i = 0; i <  screen.getChangedScreenBlocks().size(); i++) {
            String blockName =  screen.getChangedScreenBlocks().elementAt(i);
            blocks.put(blockName, screen.getScreenBlockImage(blockName).getData());
        }

        return blocks;
    }

    public BufferedImage setChangedBlocks (BufferedImage screenImage,
            HashMap<String, byte[]> changedBlocks) {
        for (String blockName : changedBlocks.keySet()) {
            setBlock(new ScreenBlock(blockName, changedBlocks.get(blockName)));
            screenImage = insertMosaicPiece(screenImage, blockName);
        }
        return screenImage;
    }

    public BufferedImage insertMosaicPiece(BufferedImage imageTotal, String name) {
        byte[] piece = screen.getScreenBlockImage(name).getData();

        ImageIcon imgPiece = new ImageIcon(piece);

        int pieceWidth = imgPiece.getIconWidth();
        int pieceHeight = imgPiece.getIconHeight();

        int row = screen.getBlockRow(name);
        int col = screen.getBlockColumn(name);

        int x = pieceWidth * col;
        int y = pieceHeight * row;

        Graphics2D imageTotalGraphics = imageTotal.createGraphics();
        imageTotalGraphics.drawImage(imgPiece.getImage(), x, y, pieceWidth, pieceHeight, null);

        return imageTotal;
    }

    public Rectangle getScreenRect () {
        return new Rectangle(0, 0, (int) screen.getScreenSizeWidth(),
                (int) screen.getScreenSizeHeight());
    }
    public void updateScreenSize(Rectangle rectangle) {
        screen.updateScreenSize(rectangle);
    }
}