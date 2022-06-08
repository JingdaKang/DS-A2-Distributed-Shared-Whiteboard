package server;

import client.WhiteBoard4M;
import remote.IRemoteWB;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * @author jingda Kang
 * @id 1276802
 **/

public class RemoteWBServant extends UnicastRemoteObject implements IRemoteWB {

    private JFrame f;

    private WhiteBoard4M whiteBoard;
    private ArrayList<String> chatContent = new ArrayList<>();
    private ArrayList<String> userList = new ArrayList<>();
    private boolean request = false;
    private int permit = -1;
    private String username;


    public String getUsername() throws RemoteException{
        return username;
    }

    protected RemoteWBServant() throws RemoteException {
    }

    @Override
    public void createWhiteBoard() throws RemoteException {

        try {
            // Determine whether the canvas has been created before

                // Create a new white board
                this.whiteBoard = new WhiteBoard4M();
                f = new JFrame("Server Board");
                f.setBounds(200, 100, 1220, 820);
                f.getContentPane().add(this.whiteBoard);
                f.setVisible(false);

        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurs in creating a white board.");
            //return null;
        }

    }

    @Override
    public void kickoutUser(int index) throws RemoteException {
        userList.remove(index);
    }

    @Override
    public void kickoutUser(String username) throws RemoteException {
        userList.remove(username);
    }

    @Override
    public void emptyUser() throws RemoteException {
        userList.clear();
    }

    @Override
    public byte[] getWhiteBoard() throws RemoteException {
        try {
            // Convert the buffered image to bytes and return
            BufferedImage outputImg = this.whiteBoard.image;
            byte[] imageBytes;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(outputImg, "JPG", bos);
            bos.flush();
            imageBytes = bos.toByteArray();
            bos.close();

            return imageBytes;

        } catch (IOException e) {
            System.out.println("IOException caught");
            e.printStackTrace();
            return null;
        }
    }


    public void setWhiteBoard(byte[] imageBytes) throws RemoteException {
        try {
            // Convert to buffer image
            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(inputStream);
            inputStream.close();

            this.whiteBoard.setCanvas(image);
        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
    }

    @Override
    public void updateChat(String content, String username) throws RemoteException {
        this.chatContent.add(username+": "+content);
    }

    public ArrayList<String> getChat() throws RemoteException{
        return this.chatContent;
    }

    @Override
    public boolean updateUserList(String username) throws RemoteException {
        try {
            this.userList.add(username);
            return true;
        } catch (Exception e){
            return false;
        }

    }

    @Override
    public ArrayList<String> getUserList() throws RemoteException{
        return this.userList;
    }

    @Override
    public boolean checkPermission(String username) throws RemoteException {
        this.request = true;
        this.username = username;
        try{
            Thread.sleep(500);
        }catch (Exception e) {
            e.printStackTrace();
        }
        this.request = false;
        this.username = null;

        while (true) {
            System.out.println("checking");
            if (this.permit == 1) {
                this.permit = -1;
                return true;
            }
            else if (this.permit == 0) {
                this.permit=-1;
                return false;
            }
        }
    }

    @Override
    public boolean checkRequest() throws RemoteException{
        boolean tmp = this.request;
        this.request = false;
        return tmp;
    }

    @Override
    public void permissionGrant() throws RemoteException {
        this.permit = 1;
        this.request = false;
    }

    @Override
    public void permissionNotGrant() throws RemoteException {
        this.permit = 0;
        this.request = false;
    }
}

