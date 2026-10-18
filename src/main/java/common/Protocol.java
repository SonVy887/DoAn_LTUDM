// ===== Protocol.java =====
/**
 * LÀM GÌ: Chứa hằng số dùng chung: tên lệnh (REGISTER, LOGIN...), mã phản hồi (200, 230, 530...).
 * GỌI TỚI: không.
 * ĐƯỢC GỌI TỪ: ClientHandler (server), FTPClient (client).
 * YÊU CẦU ĐỀ: giúp client và server thống nhất giao thức. Bước 1.
 */

package common;

import org.json.JSONObject;

public final class Protocol {

    private Protocol() {
        // Không cho tạo đối tượng Protocol
    }

    // ===== LỆNH TÀI KHOẢN =====

    public static final String REGISTER = "REGISTER";
    public static final String VERIFY_OTP = "VERIFY_OTP";
    public static final String RESEND_OTP = "RESEND_OTP";
    public static final String LOGIN = "LOGIN";
    public static final String LOGIN_ANON = "LOGIN_ANON";

    // ===== LỆNH QUẢN LÝ FILE =====

    public static final String LIST = "LIST";
    public static final String CWD = "CWD";
    public static final String MKD = "MKD";
    public static final String UPLOAD = "UPLOAD";
    public static final String DOWNLOAD = "DOWNLOAD";

    // ===== LỆNH CHIA SẺ =====

    public static final String SHARE = "SHARE";
    public static final String SHARED = "SHARED";

    // ===== LỆNH HỆ THỐNG =====

    public static final String NOTIFY = "NOTIFY";
    public static final String QUIT = "QUIT";

    // ===== MÃ PHẢN HỒI =====

    public static final int OK = 200;
    public static final int LOGIN_SUCCESS = 230;
    public static final int GOODBYE = 221;

    public static final int TRANSFER_START = 150;
    public static final int TRANSFER_COMPLETE = 226;

    public static final int BAD_REQUEST = 400;
    public static final int SERVER_ERROR = 451;
    public static final int UNKNOWN_COMMAND = 500;
    public static final int INVALID_ARGUMENT = 501;
    public static final int NOT_LOGGED_IN = 530;
    public static final int ACCESS_DENIED = 550;

    // ===== TẠO REQUEST =====

    @SuppressWarnings("unchecked")
    public static JSONObject createRequest(
            String command,
            JSONObject data
    ) {
        JSONObject request = new JSONObject();

        request.put("command", command);
        request.put("data", data == null ? new JSONObject() : data);

        return request;
    }

    // ===== TẠO RESPONSE =====

    @SuppressWarnings("unchecked")
    public static JSONObject createResponse(int code, String message) {
        JSONObject response = new JSONObject();

        response.put("code", code);
        response.put("message", message);

        return response;
    }

    // ===== RESPONSE KÈM DỮ LIỆU =====

    @SuppressWarnings("unchecked")
    public static JSONObject createResponse(
            int code,
            String message,
            Object data
    ) {
        JSONObject response = createResponse(code, message);

        response.put("data", data);

        return response;
    }
}

