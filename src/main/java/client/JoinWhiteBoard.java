package client;

import java.net.PortUnreachableException;
import java.util.Locale;

/**
 * @author jingda Kang
 * @id 1276802
 **/

public class JoinWhiteBoard {

    private static String address = "localhost";
    private static int port = 1234;
    private static String username;


    public static void main(String[] args) {
        Locale.setDefault(new Locale("USA"));


        JoinWhiteBoard joinWhiteBoard = new JoinWhiteBoard();

        try {
            joinWhiteBoard.setAddr(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Not yet designed!!!");
            System.exit(0);
        }
        try {
            joinWhiteBoard.setPort(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Wrong port number! Using default port 1234.");
        }
        try {
            joinWhiteBoard.setUserName(args[2]);
        } catch (Exception e) {
            System.out.println("Username unavailable.");
            System.exit(0);
        }

        try {
            new UserPanel(address, port, username);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Create manager panel failed.");
        }
    }

    public void setAddr(String addr) {
        try {
            address = addr;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public void setPort(String num) {

        try {
            port = Integer.parseInt(num);
            if (port < 1024 || port > 65535) {
                throw new PortUnreachableException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Number format invalid.");
            e.printStackTrace();
        } catch (PortUnreachableException e) {
            System.out.println("Port number should be in range [1024, 65535]. Using default port 2022.");
        }

    }

    public void setUserName(String name) {

        try {
            username = name;
            if (username == null) throw new NullPointerException();
        } catch (NullPointerException e) {
            System.out.println("Username unavailable.");
            System.exit(0);
        }

    }
}
