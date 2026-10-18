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

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Tìm tài khoản bằng username (email đăng nhập)
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

    // Tìm tài khoản bằng ID
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    // Lấy danh sách tài khoản
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(map(rs));
            }
        }

        return users;
    }

    // Đăng ký tài khoản - mặc định chưa kích hoạt
    public void insert(
            String username,
            String passwordHash,
            String fullName,
            String gender,
            LocalDate birthDate
    ) throws SQLException {

        String sql = """
                INSERT INTO users
                (username, email, password_hash,
                 full_name, gender, birth_date, is_active)
                VALUES (?, ?, ?, ?, ?, ?, FALSE)
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, username); // Email chính là username
            ps.setString(3, passwordHash);
            ps.setString(4, fullName);
            ps.setString(5, gender);
            ps.setDate(6, Date.valueOf(birthDate));

            ps.executeUpdate();
        }
    }

    // Kích hoạt tài khoản sau khi xác thực OTP
    public boolean activateUser(String username) throws SQLException {
        String sql = """
                UPDATE users
                SET is_active = TRUE
                WHERE username = ?
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        }
    }

    // Cập nhật dung lượng đã sử dụng
    public void updateUsedBytes(int userId, long usedBytes)
            throws SQLException {

        String sql = "UPDATE users SET used_bytes = ? WHERE id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, usedBytes);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // Cập nhật hạn mức lưu trữ
    public void updateQuota(int userId, long quotaBytes)
            throws SQLException {

        String sql = "UPDATE users SET quota_bytes = ? WHERE id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, quotaBytes);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // Khóa / mở khóa tài khoản
    public void setBlocked(int userId, boolean blocked)
            throws SQLException {

        String sql = "UPDATE users SET is_blocked = ? WHERE id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, blocked);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // Cho phép / cấm upload
    public void setCanUpload(int userId, boolean canUpload)
            throws SQLException {

        String sql = "UPDATE users SET can_upload = ? WHERE id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, canUpload);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // Cho phép / cấm download
    public void setCanDownload(int userId, boolean canDownload)
            throws SQLException {

        String sql = "UPDATE users SET can_download = ? WHERE id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, canDownload);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // Chuyển ResultSet thành User
    private User map(ResultSet rs) throws SQLException {
        User u = new User();

        u.id = rs.getInt("id");
        u.username = rs.getString("username");
        u.email = rs.getString("email");
        u.passwordHash = rs.getString("password_hash");

        u.fullName = rs.getString("full_name");
        u.gender = rs.getString("gender");

        Date birthDate = rs.getDate("birth_date");
        u.birthDate = birthDate != null
                ? birthDate.toLocalDate()
                : null;

        u.active = rs.getBoolean("is_active");

        u.quotaBytes = rs.getLong("quota_bytes");
        u.usedBytes = rs.getLong("used_bytes");
        u.canUpload = rs.getBoolean("can_upload");
        u.canDownload = rs.getBoolean("can_download");
        u.blocked = rs.getBoolean("is_blocked");

        return u;
    }
}