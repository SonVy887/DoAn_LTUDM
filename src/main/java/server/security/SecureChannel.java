// ===== SecureChannel.java =====
/**
 * LÀM GÌ: Bọc InputStream/OutputStream của Socket để mọi dữ liệu tự được mã hóa:
 *         handshake (server gửi RSA public key, client gửi lại khóa AES đã mã hóa RSA),
 *         sendLine / readLine cho lệnh, sendBytes / readBytes cho file theo từng khối.
 *         Mỗi gói: [độ dài 4 byte][IV][dữ liệu AES-GCM]. Mỗi client một đối tượng riêng,
 *         nên khóa phiên khác nhau.
 * GỌI TỚI: CryptoUtil.
 * ĐƯỢC GỌI TỪ: ClientHandler (server), FTPClient (client), thay cho việc đọc/ghi socket trực tiếp.
 * YÊU CẦU ĐỀ: mã hóa ở tầng kênh truyền client-server (không phải đầu-cuối); server vẫn xử lý
 *             và lưu dữ liệu bình thường. Bước 8.
 */
package server.security;

/**
 * YÊU CẦU ĐỀ: "Mã hóa áp dụng ở tầng kênh truyền client-server
 * (không phải mã hóa đầu-cuối), server vẫn xử lý/lưu trữ dữ liệu bình thường".
 *
 * TODO:
 *  - Bắt tay (handshake): server gửi RSA public key -> client sinh khóa AES,
 *    mã hóa bằng RSA gửi lại -> server giải mã, giữ khóa AES cho phiên đó.
 *  - sendLine(String) / readLine(): gửi/nhận một dòng lệnh đã mã hóa.
 *  - sendBytes(byte[]) / readBytes(): dùng cho truyền file theo từng khối.
 *
 * GHI CHÚ:
 *  - Bọc ngoài InputStream/OutputStream của Socket, để ClientHandler và FTPClient
 *    chỉ gọi send/read mà không quan tâm mã hóa (đổi ít code nhất).
 *  - Mỗi gói: [độ dài 4 byte][IV][dữ liệu mã hóa AES-GCM].
 *  - Mỗi client một đối tượng SecureChannel riêng -> khóa phiên khác nhau.
 *  - Cả kênh lệnh lẫn dữ liệu file đều phải đi qua kênh mã hóa.
 */
public class SecureChannel {

}