package client;
/**
 * @author jingda Kang
 * @id 1276802
 **/

import java.net.PortUnreachableException;
import java.net.UnknownHostException;
import java.util.Locale;


public class CreateWhiteBoard {

    private static String address = "localhost";
    private static int port = 1234;
    private static String username;


    public static void main(String[] args) {
        Locale.setDefault(new Locale("USA"));


        CreateWhiteBoard managerWB = new CreateWhiteBoard();

        try {
            managerWB.setAddr(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Address input invalid.");
            System.exit(0);
        }
        try {
            managerWB.setPort(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Wrong port number! Using default port 1234.");
        }
        try {
            managerWB.setUserName(args[2]);
        } catch (Exception e) {
            System.out.println("Username unavailable.");
            System.exit(0);
        }
        try {
            // Create a manager panel
            new ManagerPanel(address, port, username);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Create manager panel failed.");
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

    public void setAddr(String addr) {
        try {
            address = addr;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Address input invalid.");
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
