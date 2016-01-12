package jrdesktop.utilities;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;

/**
 * ImageUtility.java
 * @author benbac
 */

public class ImageUtility {

    private static ImageWriter writer = null;
    private static ImageWriteParam param = null;

    public static void init () {
        ImageIO.setUseCache(false);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
        writer = writers.next();
        param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(1);
    }

    public static BufferedImage read(InputStream in) throws IOException {
        BufferedImage image = null;  
        image = ImageIO.read(in);
        if (image == null)
            throw new IOException("Read fails");                  
        return image;
    } 

    public static void write(BufferedImage image, float quality,
            OutputStream out) throws IOException {
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);
        param.setCompressionQuality(quality);
        writer.write(null, new IIOImage(image, null, null), param);
        ios.close();
        //writer.dispose();
    }

    public static byte[] toByteArray(BufferedImage image, float quality) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (quality == -1)
                ImageIO.write(image, "jpeg", out); // write without compression
            else
                write(image, quality, out);       // write with compression
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte [] {};
        }
    }
    
    public static BufferedImage toBufferedImage(byte[] bytes) {
        try {
            return read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}