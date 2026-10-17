// ===== UserDAO.java =====
/**
 * LÀM GÌ: Thao tác bảng users: findByUsername, findById, findAll, insert, updateUsedBytes,
 *         updateQuota, setBlocked, setCanUpload, setCanDownload.
 * GỌI TỚI: DBConnection.
 * ĐƯỢC GỌI TỪ: AuthService, QuotaService, UserPanel (qua service). Bước 1.
 * YÊU CẦU ĐỀ: lưu tài khoản + hạn mức; server khóa quyền upload/download.
 */
package server.dao;

import server.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void insert(String username, String passwordHash, String email) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, email);
            ps.executeUpdate();
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.id = rs.getInt("id");
        u.username = rs.getString("username");
        u.passwordHash = rs.getString("password_hash");
        u.email = rs.getString("email");
        u.quotaBytes = rs.getLong("quota_bytes");
        u.usedBytes = rs.getLong("used_bytes");
        u.canUpload = rs.getBoolean("can_upload");
        u.canDownload = rs.getBoolean("can_download");
        u.blocked = rs.getBoolean("is_blocked");
        return u;
    }
}