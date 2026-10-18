// ===== FTPClient.java =====
/**
 * LÀM GÌ: Giữ socket tới server; mỗi method gửi 1 lệnh và đọc phản hồi: connect, register, verifyOtp,
 *         login, loginAnonymous, list, cwd, mkd, upload, download, share, listShared, quit.
 * GỌI TỚI: SecureChannel (sau này), Protocol.
 * ĐƯỢC GỌI TỪ: tất cả GUI của client.
 * YÊU CẦU ĐỀ: toàn bộ chức năng phía client. Làm song song với từng bước server.
 */
package client;

import common.Protocol;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FTPClient {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public void connect(String host, int port) throws IOException {
        if (socket != null && !socket.isClosed()) {
            return;
        }

        socket = new Socket(host, port);

        reader = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream(),
                        StandardCharsets.UTF_8
                )
        );

        writer = new PrintWriter(
                new OutputStreamWriter(
                        socket.getOutputStream(),
                        StandardCharsets.UTF_8
                ), true
        );
    }

    public synchronized JSONObject sendRequest(JSONObject request)
            throws IOException {

        if (socket == null || socket.isClosed()) {
            throw new IOException("Chưa kết nối đến Server");
        }

        writer.println(request.toString());

        if (writer.checkError()) {
            throw new IOException("Không gửi được request");
        }

        String response = reader.readLine();

        if (response == null) {
            throw new EOFException("Server đã đóng kết nối");
        }

        return new JSONObject(response);
    }

    public JSONObject login(String email, String password)
            throws IOException {

        JSONObject data = new JSONObject();
        data.put("email", email);
        data.put("password", password);

        JSONObject request = Protocol.createRequest(
                Protocol.LOGIN, data
        );

        return sendRequest(request);
    }

    public JSONObject register(JSONObject data) throws IOException {
        return sendRequest(
                Protocol.createRequest(Protocol.REGISTER, data)
        );
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }

        socket = null;
        reader = null;
        writer = null;
    }
}

