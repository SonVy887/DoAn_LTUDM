// ===== InputValidator.java =====
/**
 * LÀM GÌ: Kiểm tra dữ liệu đầu vào, mọi hàm trả boolean và không ném exception:
 *         isValidUsername (3-30 ký tự, chữ/số/_), isValidPassword (tối thiểu 6 ký tự),
 *         isValidEmail, isSafeFileName (không chứa "..", "/", "\", không rỗng).
 * GỌI TỚI: không.
 * ĐƯỢC GỌI TỪ: AuthService (đăng ký), FileService (tên file, tên thư mục, chống path traversal).
 * YÊU CẦU ĐỀ: kiểm tra và xử lý mọi lỗi dữ liệu đầu vào. Bước 1 (tùy chọn: có thể viết thẳng
 *             trong AuthService), bước 2 cho phần tên file/đường dẫn.
 */
package server.security;

import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

public final class InputValidator {
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Set<String> GENDERS = Set.of("Nam", "Nữ", "Khác");

    private InputValidator() {}

    public static boolean isValidEmail(String s) {
        return s != null && s.length() <= 100 && EMAIL.matcher(s).matches();
    }

    public static boolean isValidPassword(String s) {
        return s != null && s.length() >= 6 && s.length() <= 100;
    }

    public static boolean isValidFullName(String s) {
        return s != null && !s.isBlank() && s.length() <= 100;
    }

    public static boolean isValidGender(String s) {
        return s != null && GENDERS.contains(s);
    }

    public static boolean isValidBirthDate(LocalDate d) {
        return d != null && !d.isAfter(LocalDate.now());
    }

    // Dùng cho upload/mkdir ở bước sau (chặn ../)
    public static boolean isSafeFileName(String n) {
        return n != null && !n.isBlank() && n.length() <= 255
                && !n.contains("..") && !n.contains("/") && !n.contains("\\");
    }
}