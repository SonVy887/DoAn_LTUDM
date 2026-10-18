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
import server.security.InputValidator;
import server.security.PasswordUtil;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.Locale;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public void register(String email, String password, String fullName,
                         String gender, LocalDate birthDate) throws SQLException {

        email = normalize(email);
        fullName = fullName == null ? "" : fullName.trim();

        if (!InputValidator.isValidEmail(email))         throw bad("Email không đúng định dạng");
        if (!InputValidator.isValidPassword(password))   throw bad("Mật khẩu phải từ 6 đến 100 ký tự");
        if (!InputValidator.isValidFullName(fullName))   throw bad("Họ tên không hợp lệ");
        if (!InputValidator.isValidGender(gender))       throw bad("Giới tính không hợp lệ");
        if (!InputValidator.isValidBirthDate(birthDate)) throw bad("Ngày sinh không hợp lệ");

        if (userDAO.findByUsername(email) != null) throw bad("Email đã được đăng ký");

        String hash = PasswordUtil.hashPassword(password);
        try {
            userDAO.insert(email, hash, fullName, gender, birthDate);
        } catch (SQLIntegrityConstraintViolationException e) {
            // 2 người đăng ký cùng email cùng lúc: DB chặn bằng UNIQUE
            throw bad("Email đã được đăng ký");
        }
        // TODO: otpService.sendOtp(email);
    }

    public User login(String email, String password) throws SQLException {
        User u = userDAO.findByUsername(normalize(email));

        if (u == null || password == null
                || !PasswordUtil.checkPassword(password, u.passwordHash)) {
            throw new SecurityException("Sai email hoặc mật khẩu");
        }
        if (!u.active) throw new IllegalStateException("Tài khoản chưa kích hoạt, hãy xác thực OTP");
        if (u.blocked) throw new SecurityException("Tài khoản đã bị khóa");
        return u;
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    private static IllegalArgumentException bad(String msg) {
        return new IllegalArgumentException(msg);
    }
}