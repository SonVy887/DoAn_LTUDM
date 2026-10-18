// ===== Session.java =====
/**
 * LÀM GÌ: Lưu trạng thái 1 client: user đang đăng nhập, có phải ẩn danh không, thư mục hiện tại (cwd).
 * GỌI TỚI: không (chỉ chứa dữ liệu).
 * ĐƯỢC GỌI TỪ: ClientHandler, FileService, PermissionService.
 * YÊU CẦU ĐỀ: quản lý phiên người dùng. Bước 1.
 */
package server;

import server.model.User;

public class Session {
    public User user;               // null nếu chưa đăng nhập
    public boolean anonymous;
    public String cwd = "/";        // thư mục hiện tại, tính từ gốc của user

    public boolean isLoggedIn() {
        return user != null || anonymous;
    }
}
