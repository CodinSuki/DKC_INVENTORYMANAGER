import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {
    //data fields for width and height
    private int W, H;

    public MainFrame() {
        super(); //calls superclass JFrame
        H = 800;
        W = 500;
        setWindowSize(W, H);
    }

    public MainFrame(int width, int height) {
        super();
        setWindowSize(width, height);
    }

    public MainFrame(String title, int width, int height) {
        super(title);
        setWindowSize(width, height);
    }

    public MainFrame(String title,int width, int height, boolean visible) {
        super(title);
        setWindowSize(width, height);
        setVisible(visible);
    }

    public void setWindowSize(int width, int height) {
        H=height; W=width;
        setSize(width, height);
    }
    public void setMyFrame(String title, int width, int height){
        setTitle(title);
        setWindowSize(width, height);
    }
    public void setMyFrame(String title, int width, int height, boolean visible){
        setMyFrame(title, width, height);
        setVisible(visible);
    }
    public void setMyFrame(String title, int width, int height, boolean visible, int close_operation){
        setMyFrame(title, width, height, visible);
        setDefaultCloseOperation(close_operation);
    }
    public void setMyFrame(String title, int width, int height, boolean visible, int close_operation, boolean resize){
        setMyFrame(title, width, height, visible, close_operation);
        setResizable(resize);
    }
    public void setBackgroundColor(int red, int green, int blue, int opacity) {
        getContentPane().setBackground(new Color(red,green,blue,opacity));
    }
    public JPanel setBackgroundImage(String file) {
        JPanel panelBG = new JPanel();
        JLabel img = new JLabel(new ImageIcon(file));//set image to Jlabel
        panelBG.add(img); //add label to panelBG
        return panelBG;
    }


}