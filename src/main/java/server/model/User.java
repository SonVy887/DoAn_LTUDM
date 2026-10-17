// ===== User.java =====
/**
 * LÀM GÌ: Đại diện 1 dòng bảng users (id, username, passwordHash, email, quota, used, quyền, bị khóa).
 * GỌI TỚI: không.
 * ĐƯỢC GỌI TỪ: UserDAO (tạo ra), AuthService, QuotaService, PermissionService, UserPanel.
 * YÊU CẦU ĐỀ: tài khoản + hạn mức lưu trữ. Bước 1.
 */
package server.model;

public class User {
    public int id;
    public String username;
    public String passwordHash;
    public String email;
    public long quotaBytes;
    public long usedBytes;
    public boolean canUpload;
    public boolean canDownload;
    public boolean blocked;
}