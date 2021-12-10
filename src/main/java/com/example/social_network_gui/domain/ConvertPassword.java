package com.example.social_network_gui.domain;
import java.util.Base64;
public class ConvertPassword {

    public ConvertPassword() {
    }

    public String encrypt(String unencryptedString) {
            String encryptedString = Base64.getEncoder().encodeToString(unencryptedString.getBytes());
            return encryptedString;
    }

    public String decrypt(String encryptedString) {
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedString);
            String decryptedText = new String(decodedBytes);
            return decryptedText;
        }

    }
