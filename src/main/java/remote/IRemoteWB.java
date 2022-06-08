package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


/**
 * @author jingda Kang
 * @id 1276802
 **/

public interface IRemoteWB extends Remote {

    public void createWhiteBoard() throws RemoteException;

    public void kickoutUser(int index) throws RemoteException;

    public void kickoutUser(String username) throws RemoteException;

    public void emptyUser() throws RemoteException;

    public byte[] getWhiteBoard() throws RemoteException;

    public void setWhiteBoard(byte[] imageBytes) throws RemoteException;

    public void updateChat(String content, String username) throws RemoteException;

    public ArrayList<String> getChat() throws RemoteException;

    public boolean updateUserList(String username) throws RemoteException;

    public ArrayList<String> getUserList() throws RemoteException;

    public boolean checkPermission(String username) throws RemoteException;

    public boolean checkRequest() throws RemoteException;

    public String getUsername() throws RemoteException;

    public void permissionGrant() throws RemoteException;

    public void permissionNotGrant() throws RemoteException;
}
