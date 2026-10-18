package server.security;
// ===== PasswordUtil.java =====

import org.mindrot.jbcrypt.BCrypt;

/**
 * LÀM GÌ: hash(password) trả về "salt:hash" để lưu DB; verify(password, stored) so khớp lúc đăng nhập.
 *         BCrypt để băm
 * GỌI TỚI: không (chỉ dùng thư viện có sẵn của Java).
 * ĐƯỢC GỌI TỪ: AuthService (register để băm, login để so khớp).
 * YÊU CẦU ĐỀ: bảo vệ mật khẩu, lưu dạng hash, không lưu dạng rõ. Bước 1.
 */



public class PasswordUtil {
    public static String hashPassword(String password){
        String salt=BCrypt.gensalt(12);
        String hashedPassword=BCrypt.hashpw(password,salt);
        return  hashedPassword;
    }

    public static boolean checkPassword(String password,String storeHash){
        boolean valid=BCrypt.checkpw(password,storeHash);
        return valid;
    }
}