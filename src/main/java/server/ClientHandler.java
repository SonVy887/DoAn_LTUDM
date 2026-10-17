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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Bắt đầu xử lý client: " + clientSocket.getInetAddress().getHostAddress());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("[" + threadName + "] Nhận từ client " +
                        clientSocket.getInetAddress().getHostAddress() + ": " + clientMessage);
                writer.println("[" + threadName + " Server] nhận được: " + clientMessage);
                if ("bye".equalsIgnoreCase(clientMessage.trim())) {
                    System.out.println("[" + threadName + "] Client " +
                            clientSocket.getInetAddress().getHostAddress() + " yêu cầu ngắt kết nối.");
                    break;
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
    /*
    Viet cac method goi den cac class cac de xu ly lenh duoi day
    */
    public static void main(String[] args) {

    }
}
