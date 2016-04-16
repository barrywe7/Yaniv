package com.bazsoft.yaniv;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AESencryption {

    private static final String ALGO = "AES";
    private static final String strKeyValue = "WhenTheMoonIsRed";
    private static final byte[] keyValue = strKeyValue.getBytes();
    /*private static final byte[] keyValue = new byte[] { 'W', 'h', 'e', 'n',
            'T', 'h', 'e', 'M', 'o', 'o', 'n', 'I', 's', 'R', 'e', 'd' };*/

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes("UTF8"));
        return HexConverter.toHex(encVal);
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = HexConverter.toByteArray(encryptedData);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue, "UTF8");
    }

    private static Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }

    private static class HexConverter {
        private static final String HEXES = "0123456789ABCDEF";

        public static String toHex(byte[] raw) {
            if (raw == null) {
                return null;
            }
            final StringBuilder hex = new StringBuilder(2 * raw.length);
            for (final byte b : raw) {
                hex.append(HEXES.charAt((b & 0xF0) >> 4))
                        .append(HEXES.charAt((b & 0x0F)));
            }
            return hex.toString();
        }

        public static byte[] toByteArray(String s) {
            byte[] b = new byte[s.length() / 2];
            for (int i = 0; i < b.length; i++) {
                int index = i * 2;
                int v = Integer.parseInt(s.substring(index, index + 2), 16);
                b[i] = (byte) v;
            }
            return b;
        }
    }
}
