// ===== Channel.java (package common) =====
/**
 * LÀM GÌ: Là interface (không phải class thường), mô tả cách gửi/nhận dữ liệu qua socket:
 *         sendLine(String), readLine(), sendBytes(byte[], int length), readBytes(byte[], int length), close().
 *         Mọi nơi đọc/ghi socket đều đi qua interface này thay vì gọi BufferedReader/PrintWriter trực tiếp.
 * GỌI TỚI: không (chỉ khai báo các method, PlainChannel và SecureChannel sẽ cài đặt).
 * ĐƯỢC GỌI TỪ: ClientHandler (server), FTPClient (client), FileService (khi nhận/gửi file).
 * YÊU CẦU ĐỀ: tách cách truyền ra khỏi logic để cuối kỳ đổi sang kênh mã hóa (RSA + AES)
 *             mà không phải sửa lệnh nào. Cụm E, nhưng cần chốt từ đầu.
 */
package common;

import java.io.Closeable;
import java.io.IOException;

public interface Channel extends Closeable {
    void sendLine(String line) throws IOException;
    String readLine() throws IOException;                 // null khi bên kia đã đóng
    void sendBytes(byte[] buf, int off, int len) throws IOException;
    void readFully(byte[] buf, int off, int len) throws IOException;   // đọc đủ len byte
}
