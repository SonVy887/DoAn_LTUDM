// ===== FTPServer.java =====
/**
 * LÀM GÌ: Mở ServerSocket, chờ client, giao mỗi client cho 1 thread hoac (ExecutorService).
 * GỌI TỚI: ClientHandler.
 * ĐƯỢC GỌI TỪ: main(), ServerGUI (nút Start/Stop).
 * YÊU CẦU ĐỀ: server đa luồng, phục vụ nhiều client cùng lúc. Bước 0.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer {
    private final int port=8386;
    public FTPServer(int port){}

    public void start() {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server dang lang nghe tai port " + port);
            while (true) {
                try {
                    Socket clientSocket = server.accept();
                    System.out.println("Client ket noi " + clientSocket.getInetAddress().getHostAddress());
                    new Thread(new ClientHandler(clientSocket)).start(); //Hien tai dang tao tung thread rieng le
                } catch (IOException e) {
                    System.err.println("Loi accept: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Khong mo duoc port " + port + ": " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        FTPServer ftpServer= new FTPServer(8386);
        ftpServer.start();
    }
}
