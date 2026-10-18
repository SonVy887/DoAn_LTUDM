// ===== PlainChannel.java (package common) =====
/**
 * LÀM GÌ: Cài đặt Channel bằng cách gửi/nhận dữ liệu bình thường, không mã hóa. Bọc sẵn
 *         BufferedReader/PrintWriter (UTF-8) cho các dòng lệnh, InputStream/OutputStream cho byte của file.
 * GỌI TỚI: không (chỉ dùng socket).
 * ĐƯỢC GỌI TỪ: ClientHandler và FTPClient, ở chỗ tạo kênh (new PlainChannel(socket)).
 *              Khi làm xong cụm E, chỉ cần đổi thành new SecureChannel(socket) ở hai chỗ này.
 * YÊU CẦU ĐỀ: bản dùng tạm trong lúc làm các chức năng khác, giúp thử bằng PuTTY dễ dàng.
 *             Sau khi có mã hóa thì không dùng nữa (hoặc chỉ giữ để debug). Cụm E.
 */
package common;

public class PlainChannel {
}
