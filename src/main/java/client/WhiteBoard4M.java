package client;

/**
 * @author jingda Kang
 * @id 1276802
 **/

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WhiteBoard4M extends JPanel {

    private JToolBar toolBar;
    private JButton openBtn;
    private JButton saveasBtn;
    private JButton pencilBtn;
    private JButton eraserBtn;
    private JButton lineBtn;
    private JButton circleBtn;
    private JButton triangleBtn;
    private JButton rectangleBtn;
    private JButton textBtn;
    private JButton pxsizeBtn;
    private JPopupMenu pxsizeMenu;
    private JButton colorBtn;

    // variables for mouse listener
    int x;
    int y;
    int x1;
    int y1;
    int x2;
    int y2;
    ArrayList<Integer> xs = new ArrayList<Integer>();
    ArrayList<Integer> ys = new ArrayList<Integer>();

    // default setting
    Color foregroundColor = Color.BLACK;
    Color backgroundColor = Color.WHITE;
    Color eraserColor = Color.WHITE;
    private int opCode = 0; // default code -> pencil
    private int pixel_size = 2;

    public volatile boolean sending = false;

    // board setting
    int boardX = 350;
    int boardY = 30;
    int boardWidth = 820;
    int boardHeight = 720;

    int canvasWidth = 750;
    int canvasHeight = 680;
    public BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_BGR);
    Graphics gs = image.getGraphics();
    Graphics2D g = (Graphics2D) gs;
    DrawWithCanvas canvas = new DrawWithCanvas();

    // white board for manager
    public WhiteBoard4M() {
        this.setBounds(boardX, boardY, boardWidth, boardHeight);
        createNewWB();
        setToolBar();
        setActionListener();
    }

    // create new white board
    public void createNewWB() {
        g.setColor(backgroundColor);// set the color for background
        g.fillRect(0, 0, canvasWidth, canvasHeight);//set background
        g.setColor(foregroundColor);// set the color for drawing
        canvas.setImage(image);// set background color
        canvas.setBounds(0, 0, canvasWidth, canvasHeight);
        this.add(canvas, BorderLayout.CENTER);
    }

    // add tool bar
    private void setActionListener() {
        // mouse click action listener
        canvas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                x1 = e.getX();
                y1 = e.getY();
                BasicStroke bStroke = new BasicStroke(pixel_size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                switch (opCode) {
                    case 4:// triangle
                        sending = false;
                        xs.add(x1);
                        ys.add(y1);
                        if (xs.size() == 3 && ys.size() == 3) {
                            sending = true;

                            int[] xp = xs.stream().mapToInt(Integer::intValue).toArray();
                            int[] yp = ys.stream().mapToInt(Integer::intValue).toArray();
                            xs.clear();
                            ys.clear();
                            g.setStroke(bStroke);
                            g.drawPolygon(xp, yp, 3);
                            canvas.repaint();
                            sending = false;

                        }
                        break;
                    case 6: //text
                        sending = false;
                        String inputText = JOptionPane.showInputDialog("Plese input the content:", JOptionPane.PLAIN_MESSAGE);
                        if (inputText != null) {
                            sending = true;


                            g.setFont(new Font("Times New Roman", Font.PLAIN, 10 * pixel_size));
                            g.setColor(foregroundColor);
                            g.drawString(inputText, x1, y1);
                            canvas.repaint();
                            sending = false;

                        }
                        break;

                    default:
                        break;
                }

            }
        });

        canvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(final MouseEvent e) {
                sending = false;
                x1 = e.getX();
                y1 = e.getY();
            }
        });

        canvas.addMouseListener(new MouseAdapter() {
            public void mouseReleased(final MouseEvent e) {
                x = -1;
                y = -1;
                x2 = e.getX();
                y2 = e.getY();
                g.setColor(foregroundColor);
                BasicStroke bStroke = new BasicStroke(pixel_size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

                switch (opCode) {
                    case 2: // line
                        sending = true;
                        g.setStroke(bStroke);
                        g.setColor(foregroundColor);
                        g.drawLine(x1, y1, x2, y2);
                        canvas.repaint();
                        sending = false;
                        break;
                    case 3: // circle
                        sending = true;
                        g.setStroke(bStroke);
                        g.setColor(foregroundColor);
                        g.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.max(Math.abs(x1 - x2), Math.abs(x1 - x2)), Math.max(Math.abs(x1 - x2), Math.abs(x1 - x2)));
                        canvas.repaint();
                        sending = false;
                        break;
                    case 5:
                        sending = true; // rectangle
                        g.setStroke(bStroke);
                        g.setColor(foregroundColor);
                        g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
                        canvas.repaint();
                        sending = false;
                        break;
                    default:
                        sending = false;
                        break;
                }
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(final MouseEvent e) {
                if (x > 0 && y > 0) {
                    switch (opCode) {
                        case 0: // pencil
                            sending = true;
                            BasicStroke bStroke = new BasicStroke(pixel_size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                            g.setStroke(bStroke);
                            g.setColor(foregroundColor);
                            g.drawLine(x, y, e.getX(), e.getY());
                            break;
                        case 1: // eraser
                            sending = true;
                            BasicStroke bStroke1 = new BasicStroke(pixel_size * 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                            g.setStroke(bStroke1);
                            g.setColor(eraserColor);
                            g.drawLine(x, y, e.getX(), e.getY());
                            break;
                        default:
                            break;
                    }
                }
                x = e.getX();
                y = e.getY();
                canvas.repaint();
                //sending = false; // It seems like impossible to use drag listener to real time checking sending
            }

            // set cursor
            public void mouseMoved(final MouseEvent event) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        toolBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        openBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sending = true;
                openWhiteBoard();
            }
        });

        saveasBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sending = false;
                saveAs();
            }
        });

        pencilBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sending = false;
                opCode = 0;
            }
        });

        eraserBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sending = false;
                //penEraOperation = true;
                opCode = 1;
            }
        });

        lineBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sending = false;
                opCode = 2;
            }
        });

        circleBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sending = false;
                opCode = 3;
            }
        });

        triangleBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sending = false;
                opCode = 4;
            }
        });

        rectangleBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sending = false;
                opCode = 5;
            }
        });

        textBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sending = false;
                opCode = 6;
            }
        });

        colorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sending = false;
                new JColorChooser();
                Color fgColor = JColorChooser.showDialog(WhiteBoard4M.this, "Color", Color.CYAN);
                if (fgColor != null) {
                    foregroundColor = fgColor;
                }
                colorBtn.setBackground(foregroundColor);
            }
        });

        pxsizeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sending = false;
                pxsizeMenu.show(pxsizeBtn, 0 + pxsizeBtn.getWidth(), 0);
            }
        });
    }

    public void setCanvas(BufferedImage img) {
        this.image = img;
        this.gs = this.image.getGraphics();
        this.g = (Graphics2D) this.gs;
        this.g.setColor(backgroundColor);
        this.g.setColor(foregroundColor);
        this.canvas.setImage(this.image);
        this.canvas.repaint();
    }

    public void saveAs() {
        try {
            BufferedImage imgSaved = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = imgSaved.getGraphics();
            canvas.printAll(g);
            JFileChooser fileSaveChooser = new JFileChooser("Save a file");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG", "jpg");
            fileSaveChooser.setFileFilter(filter);
            fileSaveChooser.addChoosableFileFilter(new
                    FileNameExtensionFilter("PNG", "png"));
            int confirm = fileSaveChooser.showSaveDialog(fileSaveChooser);
            if (confirm == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileSaveChooser.getSelectedFile();
                String ends = fileSaveChooser.getFileFilter().getDescription().toLowerCase();
                String fileName = fileToSave.getAbsolutePath().toLowerCase();
                if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg") ||
                        fileName.endsWith(".png")) {
                    ImageIO.write(imgSaved, ends, fileToSave);
                } else {
                    // create a default jpg type and save
                    File newFile = new File(fileToSave.getAbsolutePath() + "." + ends);
                    ImageIO.write(imgSaved, ends, newFile);
                }
            }

        } catch (IOException e1) {
            System.out.println("Save failed.");
        }
    }

    // open an existed white board
    public void openWhiteBoard() {
        try {
            JFileChooser fileOpenChooser = new JFileChooser("Open file");
            int returnVal = fileOpenChooser.showOpenDialog(fileOpenChooser);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = fileOpenChooser.getSelectedFile();
                String fileName = fileToOpen.getAbsolutePath().toLowerCase();
                if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg") ||
                        fileName.endsWith(".png")) {
                    image = ImageIO.read(fileToOpen);
                    this.canvas.setImage(image);
                } else {
                    JOptionPane.showMessageDialog(null, "Should be img file.", "File Type Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Open white board failed.");
        }
    }

    public void setToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOrientation(SwingConstants.VERTICAL);
        this.add(toolBar, BorderLayout.EAST);
        toolBar.addSeparator();

        // Open
        openBtn = new JButton();
        openBtn.setBackground(Color.WHITE);
        openBtn.setToolTipText("Open file");
        openBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/open.png")));
        toolBar.add(openBtn);
        toolBar.addSeparator();
        // Save as
        saveasBtn = new JButton();
        saveasBtn.setBackground(Color.WHITE);
        saveasBtn.setToolTipText("Save as");
        saveasBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/saveas.png")));
        toolBar.add(saveasBtn);
        toolBar.addSeparator();

        // pencil
        pencilBtn = new JButton();
        pencilBtn.setBackground(Color.WHITE);
        pencilBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/pencil.png")));
        pencilBtn.setToolTipText("Pencil");
        toolBar.add(pencilBtn);
        // eraser
        eraserBtn = new JButton();
        eraserBtn.setBackground(Color.WHITE);
        eraserBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/eraser.png")));
        eraserBtn.setToolTipText("Eraser");
        toolBar.add(eraserBtn);
        // line
        lineBtn = new JButton();
        lineBtn.setBackground(Color.WHITE);
        lineBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/line.png")));
        lineBtn.setToolTipText("Line");
        toolBar.add(lineBtn);
        // circle
        circleBtn = new JButton();
        circleBtn.setBackground(Color.WHITE);
        circleBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/circle.png")));
        circleBtn.setToolTipText("Circle");
        toolBar.add(circleBtn);
        // triangle
        triangleBtn = new JButton();
        triangleBtn.setBackground(Color.WHITE);
        triangleBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/triangle.png")));
        triangleBtn.setToolTipText("Triangle");
        toolBar.add(triangleBtn);
        // rectangle
        rectangleBtn = new JButton();
        rectangleBtn.setBackground(Color.WHITE);
        rectangleBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/rectangle.png")));
        rectangleBtn.setToolTipText("Rectangle");
        toolBar.add(rectangleBtn);
        // text
        textBtn = new JButton();
        textBtn.setBackground(Color.WHITE);
        textBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/text.png")));
        textBtn.setToolTipText("Text");
        toolBar.add(textBtn);
        toolBar.addSeparator();

        // PopMenu of pixel size selection
        pxsizeMenu = new JPopupMenu();
        pxsizeMenu.setBackground(Color.WHITE);
        String[] pixelsize = {"2", "4", "6", "8", "10", "12", "14", "16"};
        for (int i = 0; i < pixelsize.length; i++) {
            final int index = i;
            JMenuItem item = new JMenuItem(pixelsize[index]);
            item.setBackground(Color.WHITE);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pixel_size = new Integer(pixelsize[index]);
                    pxsizeBtn.setText(pixelsize[index]);
                    BasicStroke bStroke = new BasicStroke(pixel_size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                    g.setStroke(bStroke);
                }
            });
            pxsizeMenu.add(item);
        }

        // pixel Size
        pxsizeBtn = new JButton();
        pxsizeBtn.setBackground(Color.WHITE);
        pxsizeBtn.setToolTipText("Line size");
        pxsizeBtn.setIcon(new ImageIcon(WhiteBoard4M.class.getResource("/img/size.png")));
        toolBar.add(pxsizeBtn);
        toolBar.addSeparator();

        // color
        colorBtn = new JButton("     ");
        colorBtn.setBackground(foregroundColor);
        colorBtn.setToolTipText("Foreground Color");
        toolBar.add(colorBtn);
        toolBar.addSeparator();
    }
}

class DrawWithCanvas extends Canvas {
    private Image image = null;

    public void setImage(Image image) {
        this.image = image;
    }

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    public void update(Graphics g) {
        paint(g);
    }
}
