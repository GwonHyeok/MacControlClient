package com.hyeok.maccontrol.app.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

/**
 * Created by GwonHyeok on 2014. 6. 19..
 */
public class SocketInit {
    private final String IP = "192.168.0.9";
    private final int PORT = 1837;
    private static Socket socket = null;
    private static BufferedInputStream bufferedInputStream;
    private static BufferedOutputStream bufferedOutputStream;

    public SocketInit(String IP) {
        try {
            if(socket != null) socket.close();
            socket = new Socket(IP, PORT);
            bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean SendCommand(String command) {
        try {
            bufferedOutputStream.write(command.getBytes());
            bufferedOutputStream.write(System.getProperty("line.separator").getBytes());
            bufferedOutputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            CloseConnection();
            return false;
        }
    }

    public boolean CheckConnection() {
        try {
            bufferedOutputStream.write(System.getProperty("line.separator").getBytes());
            bufferedOutputStream.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void CloseConnection() {
        try {
            bufferedOutputStream.close();
        } catch (Exception e) {
        }
    }
}
