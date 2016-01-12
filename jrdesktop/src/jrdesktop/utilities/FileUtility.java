package jrdesktop.utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import jrdesktop.Commons;
import jrdesktop.Settings;
import jrdesktop.main;

/**
 * FileUtility.java
 * @author benbac
 */
public class FileUtility {

    // measurement units for size formatting, from bytes to yottabytes.
    public static final String[] BYTES = {" B", " kB", " MB", " GB", " TB",
        " PB", " EB", " ZB", " YB"};
    // measurement units for speed formatting, from "bytes per second" to yottabytes.
    public static final String[] BYTES_PER_SECOND = {" B/s", " kB/s", " MB/s",
        " GB/s", " TB/s", " PB/s", " EB/s", " ZB/s", " YB/s"};

    public static String getSizeHumanFormat(long size, String[] measureUnits) {
        int measureQuantity = 1024;

        if (size <= 0) {
            return null;
        }

        if (size < measureQuantity) {
            return size + measureUnits[0];
        }

        // incrementing "letter" while value >1023
        int i = 1;
        double d = size;
        while ((d = d / measureQuantity) > (measureQuantity - 1)) {
            i++;
        }

        // remove symbols after coma, left only 2:
        long l = (long) (d * 100);
        d = (double) l / 100;

        if (i < measureUnits.length) {
            return d + measureUnits[i];
        }

        // if we still here - value is tooo big for us.
        return String.valueOf(size);
    }

    public static void extractFile(String filename) {
        File file = new File(filename);

        if (!file.canRead()) {
            try {
                file.createNewFile();
                InputStream is = main.class.getResourceAsStream(file.getName());
                BufferedInputStream bis = new BufferedInputStream(is);
                FileOutputStream fos = new FileOutputStream(file);

                int ch;
                while ((ch = bis.read()) != -1) {
                    fos.write(ch);
                }

                is.close();
                bis.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static String getBuiltDate() {
        if (!new File(getJarnameURL()).isFile()) return null;
        try {
            URL manifestUrl = new URL("jar:" + getJarnameURI() +
                    "!/META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(manifestUrl.openStream());
            Attributes attr = manifest.getMainAttributes();
            return attr.getValue("Built-Date");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<File> getFiles(File[] files) {
        ArrayList<File> allFiles = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                allFiles.addAll(getFiles(new File(files[i].toString()).listFiles()));
            } else {
                allFiles.add(files[i]);
            }
        }
        return allFiles;
    }

    public static File[] getAllFiles(File[] files) {
        ArrayList<File> fs = getFiles(files);
        return (File[]) fs.toArray(new File[fs.size()]);
    }

    // delect jrdesktop.conf, server*.conf
    //    viewer*.conf, truststore & keystore files
    public static String[] getConfigFiles() {
        final String jrdesktop_config = new File(Settings.configFile).getName();
        final String trust_store = new File(Settings.trustStore).getName();
        final String key_stroe = new File(Settings.keyStore).getName();

        FilenameFilter filenameFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return (name.startsWith("viewer") && name.endsWith(".conf")) ||
                        (name.startsWith("server") && name.endsWith(".conf")) ||
                        name.equals(trust_store) || name.equals(key_stroe) ||
                        name.equals(jrdesktop_config) ? true : false;
            }
        };
        return new File(Settings.home).list(filenameFilter);
    }

    public static String[] getSideConfigFiles(final boolean side) {
        FilenameFilter filenameFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.startsWith(getSide(side) + "_") &&
                        name.endsWith(".conf") ? true : false;
            }
        };
        String[] files = new File(Settings.home).list(filenameFilter);
        if (files == null) {
            files = new String[]{};
        }

        for (int i = 0; i < files.length; i++) {
            String name = files[i];
            files[i] = name.substring(((String) getSide(side) + "_").length(),
                    name.lastIndexOf(".conf"));
        }

        String[] allFiles = new String[files.length + 1];
        System.arraycopy(files, 0, allFiles, 0, files.length);
        allFiles[allFiles.length - 1] = Commons.DEFAULT_CONFIG;
        return allFiles;
    }

    public static String getSide(boolean side) {
        return side ? "server" : "viewer";
    }

    public static String getConfigFilename(boolean side, String name) {
        if (name.trim().length() == 0 || name.equals(Commons.DEFAULT_CONFIG)) {
            return Settings.home + getSide(side) + ".conf";
        } else {
            return Settings.home + getSide(side) + "_" + name + ".conf";
        }
    }

    public static List<File> textURIListToFileList(String data) {
        List<File> list = new ArrayList<File>(1);
        for (StringTokenizer st = new StringTokenizer(data, "\r\n");
                st.hasMoreTokens();) {
            try {
                list.add(new File(new URI(st.nextToken())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<File> textToFileList(String data) {
        List<File> list = new ArrayList<File>(1);
        for (StringTokenizer st = new StringTokenizer(data, "\r\n");
                st.hasMoreTokens();) {
            try {
                list.add(new File(st.nextToken()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static String getJarnameURI() {
        try {
            String appName = main.class.getProtectionDomain().getCodeSource().getLocation().toString();
            return new File(new URL(appName).getFile()).toURI().normalize().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "file:./jrdesktop.jar";
        }
    }

    public static String getJarnameURL() {
        try {
            return new URL(URLDecoder.decode(
                getJarnameURI(), "UTF-8")).getFile();
        } catch (Exception e) {
            e.printStackTrace();
            return "./jrdesktop.jar";
        }
    }

    public static byte[] fileToByteArray(String fileName) {
        try {
            File file = new File(fileName);
            byte buffer[] = new byte[(int) file.length()];
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
            input.read(buffer, 0, buffer.length);
            input.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }
}
