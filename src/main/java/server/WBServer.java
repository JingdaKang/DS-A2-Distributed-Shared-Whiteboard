package server;
/**
 * @author jingda Kang
 * @id 1276802
 **/

import remote.IRemoteWB;

import java.net.PortUnreachableException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Locale;


public class WBServer {

    // set default port and address for incorrect input
    private static int port = 1234;
    private static String address = "localhost";


    // rmi remote white board servant
    public static IRemoteWB remoteWBServant;

    public static void main(String[] args) {

        Locale.setDefault(new Locale("USA"));
        try {
            WBServer wbServer = new WBServer();
            try {
                wbServer.setAddr(args[0]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Address input invalid.");
                System.exit(0);
            }
            try {
                wbServer.setPort(args[1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Wrong port number ! Using default port 1234.");
            }

            remoteWBServant = new RemoteWBServant();

            // Initialize RMI registry in local address
            Registry registry = LocateRegistry.createRegistry(port);

            registry.rebind("WhiteBoard", remoteWBServant);

            System.out.println("The server is running successfully...");
            System.out.println("on address: " + address);
            System.out.println("on port: " + port);


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RMI start failed.");
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


}


