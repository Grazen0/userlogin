package com.elchologamer.userlogin.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class PasswordEncryptor {

    public static String encodeBase64(String password) {
        if (password == null || password.startsWith("§")) return password;

        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(io);

            os.writeObject(password);
            os.flush();

            return "§" + Base64.getEncoder().encodeToString(io.toByteArray());
        } catch (IOException e) {
            return password;
        }
    }

    public static String decodeBase64(String password) {
        if (password == null || !password.startsWith("§")) return password;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(password.replaceFirst("§", "")));
            ObjectInputStream is = new ObjectInputStream(in);
            return (String) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return password;
        }
    }
}
