/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jrdesktop.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import jrdesktop.main;

/**
 *
 * @author benbac
 */

public class PasswordUtility {
	private static SecretKey key = genetateKey();
            
    public static SecretKey genetateKey () {
        try {
            DESKeySpec keySpec = new DESKeySpec (
                    "My secret Key phrase".getBytes("UTF8"));
            SecretKeyFactory keyFactory = 
                    SecretKeyFactory.getInstance("DES");
            return keyFactory.generateSecret(keySpec);
            //return KeyGenerator.getInstance("DES").generateKey();        
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }               
    }

    public static String encodeString(String plainTextPassword) {
        BASE64Encoder encoder = new BASE64Encoder();
         try {
            byte[] cleartext = plainTextPassword.getBytes("UTF8");      
            Cipher cipher = Cipher.getInstance("DES"); 
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return encoder.encode(cipher.doFinal(cleartext));
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }        
    }

    public static String decodeString(String encryptedPwd) {
        BASE64Decoder decoder = new BASE64Decoder() ;
        
        try {        
            byte[] passwordBytes = 
                decoder.decodeBuffer(encryptedPwd);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            passwordBytes = cipher.doFinal(passwordBytes);
            return new String (passwordBytes, "UTF8");
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }               
    }
    
    public static void passwordGenerator() {
        System.out.println("This command takes a string and encodes it" +
            "for use in jrdesktop config files\n\n" +
            "Please enter the password:\n");
        String password;

        try {
            InputStreamReader in = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(in);
            password = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            password = "";
        }
            
        System.out.println("Encoded this password:\n '" + password + "'");
        System.out.println("Enter this encoded string into config files:\n" + 
                encodeString(password));
        main.exit();
    }
}