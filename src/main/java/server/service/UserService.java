// ===== UserService.java (package server.service) =====
/**
 * LÀM GÌ: Lớp mỏng cho giao diện quản trị thao tác với người dùng: findAll(), setBlocked(userId, b),
 *         setCanUpload(userId, b), setCanDownload(userId, b), setQuota(userId, bytes).
 *         Không chứa luật phức tạp, chỉ chuyển lời gọi xuống UserDAO để giao diện không gọi DAO trực tiếp.
 * GỌI TỚI: UserDAO.
 * ĐƯỢC GỌI TỪ: UserPanel (giao diện server).
 * YÊU CẦU ĐỀ: server cho phép hoặc khóa quyền upload/download của người dùng; cấu hình dung lượng
 *             lưu trữ tối đa từng người dùng. Cụm G (giao diện server).
 */
package server.service;

public class UserService {
}
