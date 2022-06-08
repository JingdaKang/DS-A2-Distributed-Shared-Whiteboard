package client;
/**
 * @author jingda Kang
 * @id 1276802
 **/

import remote.IRemoteWB;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


public class UserPanel {
    private String address;
    private int port;
    private String username;

    private JFrame frame;
    private JPanel function_panel;
    static JList<String> userList;
    public IRemoteWB remoteWBServant;
    private WhiteBoard4U localWhiteBoard;
    private JTextPane chatBox;


    public UserPanel(String addr, int port, String name) {

        this.address = addr;
        this.port = port;
        this.username = name;
        waitingPermission();

    }


    private void initialize() {

        frame = new JFrame("User Panel");
        frame.setBounds(200, 100, 1220, 820);
        frame.getContentPane().setLayout(null);
        // Set left side panel for user and chat info first
        function_panel = new JPanel();
        function_panel.setBounds(0, 0, 350, 800);
        frame.getContentPane().add(function_panel);

        function_panel.setLayout(null);
        JLabel host = new JLabel("Host: " + address);
        host.setBounds(20, 750, 100, 15);
        JLabel p = new JLabel("Port: " + port);
        p.setBounds(120, 750, 100, 15);

        function_panel.add(host);
        function_panel.add(p);

        JLabel board_title = new JLabel("Welcome, " + username + ".");
        board_title.setFont(new Font("Arial", Font.PLAIN, 21));
        board_title.setBounds(60, 25, 200, 30);
        function_panel.add(board_title);

        JLabel users_title = new JLabel("User List");
        users_title.setFont(new Font("Arial", Font.PLAIN, 20));
        users_title.setBounds(60, 85, 87, 20);
        function_panel.add(users_title);

        userList = new JList<String>();
        userList.setFont(new Font("Arial", Font.PLAIN, 18));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane_users = new JScrollPane();
        scrollPane_users.setBounds(45, 110, 260, 150);
        function_panel.add(scrollPane_users);
        scrollPane_users.setViewportView(userList);

        JLabel chat_title = new JLabel("Chat Box");
        chat_title.setFont(new Font("Arial", Font.PLAIN, 20));
        chat_title.setBounds(60, 300, 87, 30);
        function_panel.add(chat_title);

        chatBox = new JTextPane();
        chatBox.setFont(new Font("Arial", Font.PLAIN, 18));
        chatBox.setBounds(45, 335, 270, 320);
        chatBox.setEditable(false);

        JScrollPane scrollPane_chatbox = new JScrollPane();
        scrollPane_chatbox.setBounds(45, 335, 270, 320);
        function_panel.add(scrollPane_chatbox);
        scrollPane_chatbox.setViewportView(chatBox);

        JTextField textInput = new JTextField();
        textInput.setFont(new Font("Arial", Font.PLAIN, 16));
        textInput.setBounds(45, 670, 192, 30);
        textInput.setColumns(14);
        function_panel.add(textInput);

        JButton send = new JButton("Send");
        send.setBounds(247, 670, 68, 30);
        function_panel.add(send);

        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String content = textInput.getText();
                    remoteWBServant.updateChat(content, username);
                    textInput.setText("");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    System.out.println("Send chat failed.");
                }
            }
        });

        // Logic for closing the manager panel
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int choice = JOptionPane.showConfirmDialog(frame, "Do you want to quit?", "Warning", JOptionPane.YES_NO_OPTION);

                // if yes then turn off the user panel
                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        remoteWBServant.kickoutUser(username);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        System.out.println("User quits failed.");
                    }
                    System.exit(0);
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

        // Update the user list for user
        try {
            remoteWBServant.updateUserList(this.username);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Update user list failed.");
        }

        // keeping updating user list
        Thread userRoom = new Thread(new UserPanel.UpdatingUser());
        userRoom.start();
        // keeping updating chatbox
        Thread chatRoom = new Thread(new UserPanel.UpdatingChat());
        chatRoom.start();

        // create a local white board for user
        localWhiteBoard = new WhiteBoard4U();
        try {
            InputStream is = new ByteArrayInputStream(remoteWBServant.getWhiteBoard());
            BufferedImage img2local = ImageIO.read(is);
            is.close();
            localWhiteBoard.setCanvas(img2local);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Join whiteboard failed.");
        }

        frame.getContentPane().add(localWhiteBoard);

        // keeping updating the local white board content to server white board
        Thread updatingClient2Server = new Thread(new UpdatingCanvas2Server());
        updatingClient2Server.start();
        // keeping updating server white board to local white board
        Thread updatingServer2Client = new Thread(new UpdatingCanvas2Local());
        updatingServer2Client.start();

        frame.setVisible(true);
    }


    class UpdatingChat implements Runnable {
        @Override
        public void run() {
            while (true) {
                // update chat box every .5s
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ArrayList<String> chatContent = null;
                try {
                    chatContent = remoteWBServant.getChat();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.out.println("Updating chat failed.");
                }
                // convert arraylist to String
                String Chat = "";
                for (int i = 0; i < chatContent.size(); i++) {
                    Chat = Chat + chatContent.get(i) + "\n";
                }
                // if contents changes then set chatbox again
                if (chatBox.getText().length() != chatContent.size()) {
                    chatBox.setText(Chat);
                }
            }
        }
    }


    private void waitingPermission() {
        // connect to RMI registry and get servant
        try {
            Registry registry = LocateRegistry.getRegistry(address, port);
            this.remoteWBServant = (IRemoteWB) registry.lookup("WhiteBoard");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RMI client connection failed at lookup.");
        }

        // Verification
        JOptionPane waitPermit = new JOptionPane("Waiting for authorization...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        JDialog dialog = waitPermit.createDialog(null, "Message");

        try {
            Thread.sleep(400);
            // if the manager grants the permission
            if (remoteWBServant.checkPermission(this.username)) {
                initialize();
            }
            // if the manager refuses the permission
            else {
                dialog.dispose();
                JOptionPane.showMessageDialog(null, "You are refused by the manager", "Permission Refused", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Verification failed.");
        }
    }


    class UpdatingUser implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    // check join request and update user list every .5s
                    Thread.sleep(500);

                    // get latest user list from servant
                    ArrayList<String> userListUpdate = remoteWBServant.getUserList();

                    // if user list is empty, manager has closed the white board
                    if (userListUpdate.isEmpty()) {
                        frame.setVisible(false);
                        JOptionPane.showMessageDialog(null, "The manager has turned off the white board.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    } else if (!userListUpdate.contains(username)) {
                        frame.setVisible(false);
                        JOptionPane.showMessageDialog(null, "The manager has moved you out.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                    // if user list changes then update
                    ListModel<String> originalUserList = userList.getModel();
                    if (originalUserList.getSize() != userListUpdate.size()) {
                        userList.setListData(userListUpdate.toArray(new String[0]));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Update userlist failed.");
            }
        }
    }

    // synchronized update local board to server board
    class UpdatingCanvas2Server implements Runnable {
        @Override
        public synchronized void run() {
            try {
                while (true) {
                    if (localWhiteBoard.sending) {
                        BufferedImage img2server = localWhiteBoard.image;
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(img2server, "JPG", os);
                        os.flush();
                        byte[] imageBytes = os.toByteArray();
                        os.close();
                        remoteWBServant.setWhiteBoard(imageBytes);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Updating from local to server failed.");
            }
        }
    }

    // synchronized update server board to local board
    class UpdatingCanvas2Local implements Runnable {
        @Override
        public synchronized void run() {
            try {
                while (true) {
                    if (!localWhiteBoard.sending) {
                        InputStream is = new ByteArrayInputStream(remoteWBServant.getWhiteBoard());
                        BufferedImage img2local = ImageIO.read(is);
                        is.close();
                        localWhiteBoard.setCanvas(img2local);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Updating from server to local failed.");
            }
        }
    }


}
