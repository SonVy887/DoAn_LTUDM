package server.security;
// ===== PasswordUtil.java =====
/**
 * LÀM GÌ: hash(password) trả về "salt:hash" để lưu DB; verify(password, stored) so khớp lúc đăng nhập.
 *         Dùng PBKDF2WithHmacSHA256, 65536 vòng, mỗi user một salt ngẫu nhiên 16 byte (Base64).
 * GỌI TỚI: không (chỉ dùng thư viện có sẵn của Java).
 * ĐƯỢC GỌI TỪ: AuthService (register để băm, login để so khớp).
 * YÊU CẦU ĐỀ: bảo vệ mật khẩu, lưu dạng hash, không lưu dạng rõ. Bước 1.
 */

/**
 * YÊU CẦU ĐỀ: "Bảo vệ mật khẩu: mật khẩu lưu ở dạng hash, không lưu dạng rõ".
 *
 * TODO:
 *  - hash(String password): trả về chuỗi "salt:hash" để lưu vào users.password_hash
 *  - verify(String password, String stored): so khớp lúc đăng nhập
 *
 * GHI CHÚ (goi y AI, co the dung cac cach khac tu tim hieu)
 *  - Dùng PBKDF2WithHmacSHA256 (có sẵn trong JDK), 65536 vòng lặp, khóa 256 bit.
 *  - Mỗi user một salt ngẫu nhiên 16 byte (SecureRandom), mã hóa Base64 để lưu.
 *  - So sánh bằng MessageDigest.isEqual (tránh timing attack).
 *  - Dùng ở: AuthService (đăng ký, đăng nhập).
 */
public class PasswordUtil {

}