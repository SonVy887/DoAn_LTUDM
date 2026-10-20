// ===== FTPClient.java =====
/**
 * LÀM GÌ: Giữ socket tới server; mỗi method gửi 1 lệnh và đọc phản hồi: connect, register, verifyOtp,
 *         login, loginAnonymous, list, cwd, mkd, upload, download, share, listShared, quit.
 * GỌI TỚI: SecureChannel (sau này), Protocol.
 * ĐƯỢC GỌI TỪ: tất cả GUI của client.
 * YÊU CẦU ĐỀ: toàn bộ chức năng phía client. Làm song song với từng bước server.
 */
package client;

import common.Channel;
import common.PlainChannel;
import common.Protocol;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FTPClient {
    private Socket socket;
    private Channel channel;

    public void connect(String host, int port) throws IOException {
        if (socket != null && !socket.isClosed()) return;
        socket = new Socket(host, port);
        channel = new PlainChannel(socket);
    }

    public synchronized JSONObject sendRequest(JSONObject request) throws IOException {
        if (channel == null) throw new IOException("Chưa kết nối đến Server");
        channel.sendLine(request.toString());
        String response = channel.readLine();
        if (response == null) throw new EOFException("Server đã đóng kết nối");
        return new JSONObject(response);
    }

    public JSONObject login(String email, String password) throws IOException {
        JSONObject data = new JSONObject().put("email", email).put("password", password);
        return sendRequest(Protocol.createRequest(Protocol.LOGIN, data));
    }

    public JSONObject register(JSONObject data) throws IOException {
        return sendRequest(Protocol.createRequest(Protocol.REGISTER, data));
    }

    public JSONObject verifyOtp(String email, String otp) throws IOException {
        JSONObject data = new JSONObject().put("email", email).put("otp", otp);
        return sendRequest(Protocol.createRequest(Protocol.VERIFY_OTP, data));
    }

    public JSONObject resendOtp(String email) throws IOException {
        JSONObject data = new JSONObject().put("email", email);
        return sendRequest(Protocol.createRequest(Protocol.RESEND_OTP, data));
    }

    public void disconnect() throws IOException {
        if (channel != null) channel.close();
        channel = null;
        socket = null;
    }
}

