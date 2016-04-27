package com.ibrahim.aefs;

/**
 * Created by Scryptech on 1/16/2016.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.os.Environment;

public class MainAESActivity {

    private static String algorithm = "AES/CBC/PKCS5Padding";
    public static SecretKey yourKey = null;

    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.

//          PBKDF2 (Password-Based Key Derivation Function 2) is a key derivation function that is part of RSA Laboratories
//          PBKDF2 applies a pseudorandom function, such as a cryptographic hash, cipher,
//          or HMAC to the input password or passphrase along with a salt value and repeats
//          the process many times to produce a derived key, which can then be used as a cryptographic
//          key in subsequent operations. The added computational work makes password cracking much more difficult,
//          and is known as key stretching.
        final int iterations = 1000;
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey tmp = secretKeyFactory.generateSecret(keySpec);
        SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secretKey;
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        yourKey = keyGenerator.generateKey();
        return yourKey;
    }

    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] encrypted = null;
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
                algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        return encrypted;
    }

    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    public static String saveFile(byte[] stringToSave, String fileName, boolean enc) {
        try {
            File file;
            if (enc) {
                File sdcard = new File(Environment.getExternalStorageDirectory() + "/AEFS/encrypted");
                sdcard.mkdirs();
                file = new File(sdcard
                        + File.separator, fileName);
            } else {
                File sdcard = new File(Environment.getExternalStorageDirectory() + "/AEFS/decrypted");
                sdcard.mkdirs();
                file = new File(sdcard
                        + File.separator, fileName);
            }

            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file, false));

            bos.write(stringToSave);
            bos.flush();
            bos.close();
            return file.getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//    void decodeFile(String path) {
//
//        try {
//            byte[] decodedData = decodeFile(yourKey, readFile(path));
//            saveFile(decodedData, decryptedFileName , false);
//            String str = new String(decodedData);
//            System.out.println("DECODED FILE CONTENTS : " + str);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static byte[] readFile(String path) {
        byte[] contents = null;

        File file = new File(path);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

}