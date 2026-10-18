// ===== RegisterDialog.java =====
/**
 * LÀM GÌ: Form đăng ký + bước nhập mã OTP.
 * GỌI TỚI: FTPClient (register, verifyOtp).
 * ĐƯỢC GỌI TỪ: LoginFrame.
 * YÊU CẦU ĐỀ: tạo tài khoản mới với xác thực OTP. Bước 9 (OTP ở bước 7).
 */
package client.gui;

import client.FTPClient;
import common.Protocol;
import com.toedter.calendar.JDateChooser;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class RegisterDialog extends JFrame {

    private final FTPClient ftpClient;

    private JTextField txtEmail;
    private JTextField txtFullName;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> cboGender;
    private JDateChooser dateBirth;
    private JButton btnRegister;

    public RegisterDialog(FTPClient ftpClient) {
        this.ftpClient = ftpClient;

        setTitle("Đăng ký tài khoản FTP");
        setSize(470, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {

        JPanel panel = new JPanel(
                new GridLayout(7, 2, 10, 15)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        txtEmail = new JTextField();
        txtFullName = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();

        cboGender = new JComboBox<>(
                new String[]{"Nam", "Nữ", "Khác"}
        );

        // Lịch chọn ngày sinh
        dateBirth = new JDateChooser();
        dateBirth.setDateFormatString("dd/MM/yyyy");
        dateBirth.setMaxSelectableDate(new Date());
        dateBirth.setDate(null);

        btnRegister = new JButton("Đăng ký");

        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        panel.add(new JLabel("Mật khẩu:"));
        panel.add(txtPassword);

        panel.add(new JLabel("Xác nhận mật khẩu:"));
        panel.add(txtConfirmPassword);

        panel.add(new JLabel("Họ và tên:"));
        panel.add(txtFullName);

        panel.add(new JLabel("Giới tính:"));
        panel.add(cboGender);

        panel.add(new JLabel("Ngày sinh:"));
        panel.add(dateBirth);

        panel.add(new JLabel(""));
        panel.add(btnRegister);

        add(panel);

        btnRegister.addActionListener(e -> register());

        getRootPane().setDefaultButton(btnRegister);
    }

    private void register() {

        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword =
                new String(txtConfirmPassword.getPassword());

        String fullName = txtFullName.getText().trim();
        String gender = (String) cboGender.getSelectedItem();

        // Kiểm tra thông tin
        if (email.isEmpty() || password.isEmpty()
                || fullName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập đầy đủ thông tin!"
            );
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {
            JOptionPane.showMessageDialog(
                    this,
                    "Email không hợp lệ!"
            );
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(
                    this,
                    "Mật khẩu phải có ít nhất 6 ký tự!"
            );
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Mật khẩu xác nhận không khớp!"
            );
            return;
        }

        Date selectedDate = dateBirth.getDate();

        if (selectedDate == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn ngày sinh!"
            );
            return;
        }

        LocalDate birthDate = selectedDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Tạo JSON chứa thông tin đăng ký
        JSONObject data = new JSONObject();

        data.put("email", email);
        data.put("password", password);
        data.put("fullName", fullName);
        data.put("gender", gender);
        data.put("birthDate", birthDate.toString());

        btnRegister.setEnabled(false);

        SwingWorker<JSONObject, Void> worker = new SwingWorker<>() {

            @Override
            protected JSONObject doInBackground() throws Exception {
                // Sử dụng kết nối chung với LoginFrame
                return ftpClient.sendRequest(
                        Protocol.createRequest(
                                Protocol.REGISTER,
                                data
                        )
                );
            }

            @Override
            protected void done() {
                btnRegister.setEnabled(true);

                try {
                    JSONObject response = get();

                    int code = response.optInt("code");
                    String message = response.optString("message");

                    if (code == Protocol.OK) {

                        JOptionPane.showMessageDialog(
                                RegisterDialog.this,
                                message,
                                "Đăng ký thành công",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        // Khi có OTPVerificationFrame:
                        // new OTPVerificationFrame(
                        //         ftpClient, email
                        // ).setVisible(true);
                        // dispose();

                    } else {
                        JOptionPane.showMessageDialog(
                                RegisterDialog.this,
                                message,
                                "Đăng ký thất bại",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            RegisterDialog.this,
                            "Lỗi đăng ký: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }
}
