// ===== DBConnection.java =====
/**
 * LÀM GÌ: getConnection() trả về kết nối MySQL mới mỗi lần gọi (an toàn khi nhiều thread). Đã xong.
 * GỌI TỚI: Config (sau này).
 * ĐƯỢC GỌI TỪ: tất cả DAO.
 * YÊU CẦU ĐỀ: sử dụng CSDL.
 */
package server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL =
            "jdbc:mysql://localhost:3306/ftp_app?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    // Mỗi lần gọi trả về 1 kết nối MỚI (an toàn khi nhiều thread dùng cùng lúc)
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Chạy thử để kiểm tra kết nối
    public static void main(String[] args) {
        try (Connection c = getConnection()) {
            System.out.println("Ket noi MySQL thanh cong: " + c.getCatalog());
        } catch (SQLException e) {
            System.err.println("Ket noi that bai: " + e.getMessage());
        }
    }
}