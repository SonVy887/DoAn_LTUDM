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
/**
 * LÀM GÌ: Cài đặt Channel trên socket thường (chưa mã hóa); đọc dòng và đọc đủ byte từ cùng một luồng.
 * GỌI TỚI: java.net.Socket.
 * ĐƯỢC GỌI TỪ: ClientHandler, FTPClient (sau này thay bằng SecureChannel).
 * YÊU CẦU ĐỀ: nền cho kênh truyền; dòng JSON và byte file chung một luồng. Bước 1.
 */

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PlainChannel implements Channel {
    private static final int MAX_LINE = 1024 * 1024;   // chặn dòng quá dài

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    public PlainChannel(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedInputStream(socket.getInputStream());
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }

    @Override
    public String readLine() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1 && b != '\n') {
            if (buf.size() >= MAX_LINE) throw new IOException("Dòng quá dài");
            buf.write(b);
        }
        if (b == -1 && buf.size() == 0) return null;
        String s = buf.toString(StandardCharsets.UTF_8);
        return s.endsWith("\r") ? s.substring(0, s.length() - 1) : s;
    }

    @Override
    public synchronized void sendLine(String line) throws IOException {
        out.write((line + "\n").getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    @Override
    public synchronized void sendBytes(byte[] buf, int off, int len) throws IOException {
        out.write(buf, off, len);
        out.flush();
    }

    @Override
    public void readFully(byte[] buf, int off, int len) throws IOException {
        if (in.readNBytes(buf, off, len) < len) throw new EOFException("Kết nối bị ngắt giữa chừng");
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
