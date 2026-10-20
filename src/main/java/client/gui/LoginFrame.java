// ===== LoginFrame.java =====
/**
 * LÀM GÌ: Màn hình đăng nhập: host, port, username, password; nút Login, Anonymous, Register.
 * GỌI TỚI: FTPClient, RegisterDialog, MainFrame.
 * ĐƯỢC GỌI TỪ: main() của phía client.
 * YÊU CẦU ĐỀ: GUI bắt buộc; đăng nhập thường và ẩn danh. Bước 9.
 */
package client.gui;


import client.FTPClient;
import common.Protocol;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final FTPClient ftpClient;

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginFrame(FTPClient ftpClient) {
        this.ftpClient = ftpClient;

        setTitle("Đăng nhập FTP");
        setSize(420, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        txtEmail = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký");

        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        panel.add(new JLabel("Mật khẩu:"));
        panel.add(txtPassword);

        panel.add(btnRegister);
        panel.add(btnLogin);

        add(panel);

        btnLogin.addActionListener(e -> login());

        btnRegister.addActionListener(e -> {
            new RegisterDialog(ftpClient).setVisible(true);
        });

        getRootPane().setDefaultButton(btnLogin);
    }

    private void login() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập email và mật khẩu!"
            );
            return;
        }

        JSONObject data = new JSONObject();
        data.put("email", email);
        data.put("password", password);

        JSONObject request = Protocol.createRequest(
                Protocol.LOGIN, data
        );

        btnLogin.setEnabled(false);

        SwingWorker<JSONObject, Void> worker = new SwingWorker<>() {

            @Override
            protected JSONObject doInBackground() throws Exception {
                return ftpClient.sendRequest(request);
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);

                try {
                    JSONObject response = get();

                    int code = response.optInt("code");
                    String message = response.optString("message");

                    if (code == Protocol.LOGIN_SUCCESS) {
                        JOptionPane.showMessageDialog(
                                LoginFrame.this,
                                "Đăng nhập thành công!"
                        );

                        System.out.println(
                                "LOGIN SUCCESS: " + email
                        );

                        // Sau này mở MainFrame tại đây.

                    } else if (code == Protocol.NOT_ACTIVATED) {
                        // Đúng mật khẩu nhưng chưa nhập OTP: mở cửa sổ nhập mã
                        JOptionPane.showMessageDialog(
                                LoginFrame.this,
                                message,
                                "Chưa kích hoạt",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        RegisterDialog.showOtpDialog(
                                LoginFrame.this, ftpClient, email
                        );

                    } else {
                        JOptionPane.showMessageDialog(
                                LoginFrame.this,
                                message,
                                "Đăng nhập thất bại",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            "Lỗi đăng nhập: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            FTPClient ftpClient = new FTPClient();

            try {
                ftpClient.connect("localhost", 8386);

                LoginFrame frame = new LoginFrame(ftpClient);
                frame.setVisible(true);

                // Đóng kết nối khi đóng ứng dụng
                Runtime.getRuntime().addShutdownHook(
                        new Thread(() -> {
                            try {
                                ftpClient.disconnect();
                            } catch (Exception ignored) {
                            }
                        })
                );

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Không kết nối được Server: " + e.getMessage()
                );
            }
        });
    }
}