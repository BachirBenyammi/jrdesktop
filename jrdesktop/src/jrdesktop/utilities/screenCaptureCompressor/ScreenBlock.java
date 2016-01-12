package jrdesktop.utilities.screenCaptureCompressor;

public class ScreenBlock {

    public static final int COMPARE_LENGTH = 1;
    public static final int COMPARE_STRING = 2;
    public static final int COMPARE_BYTES  = 3;
    
    private byte[] data = "".getBytes();
    
    private String name = "";
    
    private int differences = 0;

    ScreenBlock(String name) {
        this.name = name;
    }

    public ScreenBlock(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }
    
    public int getDifferences() {
        return differences;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public boolean isModified(byte[] newdata, int method) {
        /*Method 1*/
        if (method == COMPARE_LENGTH) {
            if (newdata.length != this.data.length) {
                this.data = newdata;
                return true;
            }
        }
        /*Method 2*/
        if (method == COMPARE_STRING) {
            if (new String(newdata).equals(new String(this.data)) == false) {
                this.data = newdata;
                return true;
            }
        }
        /*Method 3*/
        if (method == COMPARE_BYTES) {
            differences = 0;
            for (int i = 0; i < newdata.length; i++) {
                if (newdata[i] != this.data[i]) {
                    differences++;
                }
            }
            return (differences > 0);
        }
        return false;
    }
}
