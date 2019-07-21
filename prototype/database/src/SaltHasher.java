package prototype.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SaltHasher {

    static class HashResult{

        String hash;
        String salt;

        private HashResult(String hash, String salt){
            this.hash = hash;
            this.salt = salt;
        }

        public String getHash(){
            return hash;
        }

        public String getSalt(){
            return salt;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static String saltGenerator(){
        StringBuilder salt = new StringBuilder();

        for (int i = 0; i < 16; i++){
            int newChar = (int) (Math.random() * 62);
            if (newChar < 10){
                salt.append(newChar);
            }else if (newChar < 36){
                salt.append((char)((byte) newChar + 55));
            }else{
                salt.append((char)((byte) newChar + 61));
            }
        }


        return salt.toString();
    }

    public static HashResult hash(String text){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            String salt = saltGenerator();
            byte[] encodedhash = digest.digest((salt + text).getBytes(StandardCharsets.UTF_8));
            return new HashResult(bytesToHex(encodedhash), salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String hash(String text, String salt){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest((salt + text).getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

}
