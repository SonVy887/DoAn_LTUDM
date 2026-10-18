// ===== AuthService.java =====
/**
 * LÀM GÌ: register(), login(), loginAnonymous(). Kiểm tra dữ liệu đầu vào, băm/so khớp mật khẩu.
 * GỌI TỚI: UserDAO, PasswordUtil, SettingService (ẩn danh), OtpService (đăng ký).
 * ĐƯỢC GỌI TỪ: ClientHandler (cmdRegister, cmdLogin, cmdLoginAnon).
 * YÊU CẦU ĐỀ: tạo tài khoản, đăng nhập, ẩn danh, bảo vệ mật khẩu. Bước 1, 4.
 */
package server.service;

import server.dao.UserDAO;
import server.model.User;
import server.security.PasswordUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    public String register(String email, String password, String fullName, String gender, LocalDate birthDate) throws SQLException {
        // 1. Kiểm tra email
        if (email == null || email.isBlank()) {
            return "Email không được để trống";
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Email không đúng định dạng";
        }

        // 2. Kiểm tra mật khẩu
        if (password == null || password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }

        // 3. Kiểm tra họ tên
        if (fullName == null || fullName.isBlank()) {
            return "Họ tên không được để trống";
        }

        fullName = fullName.trim();

        // 4. Kiểm tra giới tính
        if (gender == null ||
                (!gender.equals("Nam")
                        && !gender.equals("Nữ")
                        && !gender.equals("Khác"))) {
            return "Giới tính không hợp lệ";
        }

        // 5. Kiểm tra ngày sinh
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            return "Ngày sinh không hợp lệ";
        }

        // 6. Kiểm tra email đã tồn tại
        User existingUser = userDAO.findByUsername(email);

        if (existingUser != null) {
            return "Email đã được đăng ký";
        }

        // 7. Mã hóa mật khẩu bằng BCrypt
        String passwordHash = PasswordUtil.hashPassword(password);

        // 8. Lưu tài khoản vào MySQL
        // is_active mặc định là FALSE
        userDAO.insert(
                email,
                passwordHash,
                fullName,
                gender,
                birthDate
        );

        // 9. TODO: Tạo và gửi OTP xác thực email
        // otpService.sendOtp(email);

        return null;
    }

    public User login(String email, String password) throws SQLException {

        User user = userDAO.findByUsername(
                email.trim().toLowerCase()
        );

        if (user == null) {
            System.out.println("LOGIN FAILED: Không tìm thấy tài khoản");
            return null;
        }

        if (!PasswordUtil.checkPassword(password, user.passwordHash)) {
            System.out.println("LOGIN FAILED: Sai mật khẩu");
            return null;
        }

        if (!user.active) {
            System.out.println("LOGIN FAILED: Tài khoản chưa kích hoạt");
            return null;
        }

        if (user.blocked) {
            System.out.println("LOGIN FAILED: Tài khoản bị khóa");
            return null;
        }

        System.out.println("LOGIN SUCCESS");
        return user;
    }
}
