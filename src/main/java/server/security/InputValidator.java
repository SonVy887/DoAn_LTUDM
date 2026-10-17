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

/**
 * YÊU CẦU ĐỀ: "Kiểm tra và xử lý mọi lỗi dữ liệu đầu vào;
 * không để phát sinh lỗi chưa xử lý khi chạy".
 *
 * TODO:
 *  - isValidUsername(String): 3-30 ký tự, chỉ gồm chữ, số, dấu gạch dưới
 *  - isValidPassword(String): tối thiểu 6 ký tự
 *  - isValidEmail(String): đúng định dạng email
 *  - isSafeFileName(String): không chứa "..", "/", "\", không rỗng
 *
 * GHI CHÚ:
 *  - Mọi hàm trả về boolean, không ném exception.
 *  - isSafeFileName/đường dẫn quan trọng: chặn path traversal (../) để user
 *    không đọc được file ngoài thư mục của mình.
 *  - Tùy chọn: có thể viết thẳng trong AuthService/FileService thay vì class riêng.
 */
public class InputValidator {

}