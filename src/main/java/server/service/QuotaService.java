// ===== QuotaService.java =====
/**
 * LÀM GÌ: hasSpace(user, size), checkMaxFileSize(size), addUsed, subtractUsed. Cũng kiểm tra
 *         quyền upload/download bị khóa (can_upload, can_download).
 * GỌI TỚI: UserDAO, SettingService.
 * ĐƯỢC GỌI TỪ: ClientHandler.cmdUpload, cmdDownload; UserPanel (sửa quota).
 * YÊU CẦU ĐỀ: hạn mức lưu trữ từng user, kích thước file tối đa, khóa upload/download. Bước 3.
 */
package server.service;

public class QuotaService {
}
