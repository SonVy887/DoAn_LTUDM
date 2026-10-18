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

import common.Protocol;
import org.json.JSONObject;
import org.json.JSONException;
import server.model.User;
import server.service.AuthService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private final AuthService authService = new AuthService();
    private User currentUser; //user da dang nhap

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Bắt đầu xử lý client: " +
                clientSocket.getInetAddress().getHostAddress());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String clientMessage;

            while ((clientMessage = reader.readLine()) != null) {

                try {
                    JSONObject request = new JSONObject(clientMessage);

                    String command = request.optString("command", "")
                            .toUpperCase(Locale.ROOT);

                    JSONObject data = request.optJSONObject("data");

                    if (data == null) {
                        data = new JSONObject();
                    }

                    boolean keepRunning = handleCommand(command, data, writer);

                    if (!keepRunning) {
                        break;
                    }

                } catch (JSONException e) {
                    sendResponse(writer, Protocol.BAD_REQUEST, "JSON không hợp lệ");
                }
            }
        }catch(IOException ex) {
            System.err.println("[" + threadName + "] Lỗi I/O khi giao tiếp với client " +
                    clientSocket.getInetAddress().getHostAddress() + ": " + ex.getMessage());
        }finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                    System.out.println("[" + threadName + "] Đã đóng kết nối với client: " +
                            clientSocket.getInetAddress().getHostAddress());
                }
            } catch (IOException e) {
                System.err.println("[" + threadName + "] Lỗi khi đóng socket của client " +
                        clientSocket.getInetAddress().getHostAddress() + ": " + e.getMessage());
            }
            System.out.println("[" + threadName + "] Kết thúc xử lý client: " +
                    clientSocket.getInetAddress().getHostAddress());
        }
    }

    private boolean handleCommand(String command, JSONObject args, PrintWriter writer){
        switch (command) {
            // ===== TÀI KHOẢN =====
            case "REGISTER":
                cmdRegister(args, writer);
                break;

            case "VERIFY_OTP":
                //cmdVerifyOtp(args, writer);
                break;

            case "LOGIN":
                cmdLogin(args, writer);
                break;

            case "LOGIN_ANON":
                //cmdLoginAnon(writer);
                break;

            // ===== QUẢN LÝ FILE =====

            case "LIST":
                //cmdList(writer);
                break;

            case "CWD":
                //cmdCwd(args, writer);
                break;

            case "MKD":
                //cmdMkd(args, writer);
                break;

            case "UPLOAD":
                //cmdUpload(args, writer);
                break;

            case "DOWNLOAD":
                //cmdDownload(args, writer);
                break;

            // ===== CHIA SẺ FILE =====

            case "SHARE":
                //cmdShare(args, writer);
                break;

            case "SHARED":
                //cmdShared(writer);
                break;

            // ===== NGẮT KẾT NỐI =====

            case "QUIT":
                writer.println("221 Goodbye");
                return false;

            default:
                writer.println("500 Unknown command");
                break;
        }
        return true;
    }

    private void cmdRegister(JSONObject data, PrintWriter writer) {
        try {
            String email = data.optString("email", "");
            String password = data.optString("password", "");
            String fullName = data.optString("fullName", "");
            String gender = data.optString("gender", "");
            String birthDateStr = data.optString("birthDate", "");

            LocalDate birthDate = birthDateStr.isBlank()
                    ? null
                    : LocalDate.parse(birthDateStr);

            String result = authService.register(
                    email, password, fullName, gender, birthDate
            );

            if (result == null) {
                sendResponse(writer, Protocol.OK,
                        "Đã tạo tài khoản, chưa xác thực OTP");
            } else {
                sendResponse(writer, Protocol.BAD_REQUEST, result);
            }

        } catch (DateTimeParseException e) {
            sendResponse(writer, Protocol.INVALID_ARGUMENT,
                    "Ngày sinh không hợp lệ");

        } catch (SQLException e) {
            System.err.println("REGISTER SQL Error: " + e.getMessage());
            sendResponse(writer, Protocol.SERVER_ERROR,
                    "Lỗi cơ sở dữ liệu");
        }
    }

    private void cmdLogin(JSONObject data, PrintWriter writer) {

        String email = data.optString("email", "");
        String password = data.optString("password", "");

        try {
            User user = authService.login(email, password);

            if (user == null) {
                sendResponse(
                        writer,
                        Protocol.NOT_LOGGED_IN,
                        "Đăng nhập thất bại. Kiểm tra thông tin hoặc trạng thái tài khoản."
                );
                return;
            }

            // Lưu User vào phiên kết nối hiện tại
            currentUser = user;

            sendResponse(
                    writer,
                    Protocol.LOGIN_SUCCESS,
                    "Đăng nhập thành công"
            );

        } catch (SQLException e) {
            e.printStackTrace();

            sendResponse(
                    writer,
                    Protocol.SERVER_ERROR,
                    "Lỗi cơ sở dữ liệu"
            );
        }
    }
    private void sendResponse(PrintWriter writer, int code, String message) {
        JSONObject response = Protocol.createResponse(code, message);
        writer.println(response.toString());
    }
}
