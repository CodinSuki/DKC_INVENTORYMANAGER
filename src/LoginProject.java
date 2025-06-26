import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;



public class LoginProject extends JFrame {
    private JTextField AdminInput;
    private JPasswordField PasswordInput;
    private JButton ConfirmBtn;
    private JPanel LoginScreen;
    private JLabel LoginText, LostPassword, NoAccountInquiry;

    private String SecretPass = "user1234", SecretName = "user";

    public LoginProject() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("This is Java");
        setResizable(false);
        setSize(300,300);
                LoginScreen = new JPanel();
        LoginScreen.setLayout(new GridLayout(5, 1));

        LoginText = new JLabel("Login", SwingConstants.CENTER);
        AdminInput = new JTextField("Username");
        PasswordInput = new JPasswordField("Password");
        PasswordInput.setEchoChar('*');


        LoginScreen.add(LoginText);
        LoginScreen.add(AdminInput);
        LoginScreen.add(PasswordInput);


        AdminInput.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AdminInput.setText("");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        PasswordInput.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PasswordInput.setText("");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        ConfirmBtn = new JButton("Sign in");
        LoginScreen.add(ConfirmBtn);

        ConfirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (PasswordInput.getText().equals(SecretPass) && AdminInput.getText().equals(SecretName)){
                    JOptionPane.showMessageDialog(null, "Correct Info. Hello, Admin!");
                    dispose();
                    MainProgram Main = new MainProgram();
                    Main.setVisible(true);


                } else {
                    JOptionPane.showMessageDialog(null, "Login failed!");
                    System.exit(0);
                }
            }
        });

        LostPassword = new JLabel ("Lost your password?", SwingConstants.CENTER);
        LoginScreen.add(LostPassword);

        add(LoginScreen);
        setVisible(true);
    }


    public static void main(String[] args) {
        new LoginProject();
    }
}