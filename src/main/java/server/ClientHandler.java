// ===== ClientHandler.java =====
/**
 * LÀM GÌ: Xử lý 1 client trong 1 thread: đọc lệnh, gọi hàm cmdXxx tương ứng, gửi phản hồi.
 *         KHÔNG viết logic nghiệp vụ ở đây.
 * GỌI TỚI: AuthService, OtpService, FileService, PermissionService, QuotaService,
 *          ShareService, LogService, NotificationService, SettingService.
 * ĐƯỢC GỌI TỪ: FTPServer.
 * YÊU CẦU ĐỀ: mọi chức năng phía server đi qua đây. Thêm dần từng lệnh theo từng bước.
 */
package server;

import common.Channel;
import common.PlainChannel;
import common.Protocol;
import org.json.JSONException;
import org.json.JSONObject;
import server.service.AuthService;

import javax.security.auth.login.FailedLoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ClientHandler implements Runnable {
    private Channel channel;
    @FunctionalInterface
    private interface Command {
        JSONObject execute(JSONObject data) throws Exception;
    }

    // Các lệnh được dùng khi chưa đăng nhập
    private static final Set<String> PUBLIC_COMMANDS = Set.of(
            Protocol.REGISTER, Protocol.VERIFY_OTP, Protocol.RESEND_OTP,
            Protocol.LOGIN, Protocol.LOGIN_ANON, Protocol.QUIT);

    private final Socket socket;
    private final Session session = new Session();
    private final AuthService authService = new AuthService();
    private final Map<String, Command> commands = new HashMap<>(); // Cac lenh
    private boolean running = true;

    public ClientHandler(Socket socket) {
        this.socket = socket;

        // Thêm lệnh mới = thêm 1 dòng ở đây + 1 hàm cmdXxx bên dưới
        commands.put(Protocol.REGISTER, this::cmdRegister);
        commands.put(Protocol.LOGIN, this::cmdLogin);
        commands.put(Protocol.QUIT, this::cmdQuit);
    }

    @Override
    public void run() {
        String ip = socket.getInetAddress().getHostAddress();
        System.out.println("[+] Client kết nối: " + ip);

        try (Channel ch = new PlainChannel(socket)) {
            this.channel = ch;
            String line;
            while (running && (line = ch.readLine()) != null) {
                if (line.isBlank()) continue;
                ch.sendLine(process(line).toString());   // chỗ DUY NHẤT gửi phản hồi
            }
        } catch (IOException e) {
            System.err.println("[!] Mất kết nối " + ip + ": " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("[-] Client ngắt: " + ip);
        }
    }

    // Nhận 1 dòng, luôn trả về đúng 1 phản hồi (kể cả khi lỗi)
    private JSONObject process(String line) {
        try {
            JSONObject request = new JSONObject(line);
            String command = request.optString("command", "").toUpperCase(Locale.ROOT);
            JSONObject data = request.optJSONObject("data");
            if (data == null) data = new JSONObject();

            Command cmd = commands.get(command);
            if (cmd == null) {
                return Protocol.error(Protocol.UNKNOWN_COMMAND, "Lệnh không hỗ trợ: " + command);
            }
            if (!session.isLoggedIn() && !PUBLIC_COMMANDS.contains(command)) {
                return Protocol.error(Protocol.NOT_LOGGED_IN, "Chưa đăng nhập");
            }
            return cmd.execute(data);

        } catch (JSONException e) {
            return Protocol.error(Protocol.BAD_REQUEST, "JSON không hợp lệ");
        } catch (IllegalArgumentException e) {      // dữ liệu sai, email trùng
            return Protocol.error(Protocol.INVALID_ARGUMENT, e.getMessage());
        } catch (FailedLoginException e) {
            return Protocol.error(Protocol.NOT_LOGGED_IN, e.getMessage());
        } catch (SecurityException e) {
            return Protocol.error(Protocol.ACCESS_DENIED, e.getMessage());
        } catch (IllegalStateException e) {         // chưa kích hoạt
            return Protocol.error(Protocol.NOT_ACTIVATED, e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL lỗi: " + e.getMessage());
            return Protocol.error(Protocol.SERVER_ERROR, "Lỗi cơ sở dữ liệu");
        } catch (Exception e) {
            e.printStackTrace();
            return Protocol.error(Protocol.SERVER_ERROR, "Lỗi xử lý");
        }
    }

    // ===== Các lệnh: chỉ lấy dữ liệu, gọi service, trả phản hồi =====

    private JSONObject cmdRegister(JSONObject d) throws Exception {
        String birth = d.optString("birthDate", "");
        LocalDate birthDate;
        try {
            birthDate = birth.isBlank() ? null : LocalDate.parse(birth);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ");
        }

        authService.register(
                d.optString("email", ""), d.optString("password", ""),
                d.optString("fullName", ""), d.optString("gender", ""), birthDate);

        return Protocol.ok("Đã tạo tài khoản, chưa xác thực OTP");
    }

    private JSONObject cmdLogin(JSONObject d) throws Exception {
        session.user = authService.login(
                d.optString("email", ""), d.optString("password", ""));
        return Protocol.createResponse(Protocol.LOGIN_SUCCESS, "Đăng nhập thành công");
    }

    private JSONObject cmdQuit(JSONObject d) {
        running = false;
        return Protocol.createResponse(Protocol.GOODBYE, "Tạm biệt");
    }
}