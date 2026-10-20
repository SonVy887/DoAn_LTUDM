// ===== OtpService.java =====
/**
 * LÀM GÌ: generateAndSend(email) sinh mã OTP và gửi email; verify(email, code) kiểm tra, có hạn dùng.
 * GỌI TỚI: gửi email (JavaMail).
 * ĐƯỢC GỌI TỪ: AuthService, ClientHandler.cmdVerifyOtp.
 * YÊU CẦU ĐỀ: xác thực OTP khi tạo tài khoản. Bước 7.
 */
package server.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class OtpService {
    private static Long otpTime= Long.valueOf(600000);
    private static int limit=3;

    private static Map<String, String> otpCodes = new ConcurrentHashMap<>();
    private static Map<String, Long> otpExpiry = new ConcurrentHashMap<>();
    private static Map<String, Integer> otpAttempts = new ConcurrentHashMap<>();

    public void generateAndSend(String email)throws MessagingException{
        String email1=email.trim().toLowerCase();
        StringBuilder otpValue= new StringBuilder();
        SecureRandom secureRandom=new SecureRandom();
        for (int i=0;i<6;i++){
            int random=secureRandom.nextInt(10);
            otpValue.append(random);
        }
        String otp = otpValue.toString(); //tạo otp ngẫu nhiên
        sendMail(email1, "Xác thực tài khoản FTP",
                "Mã OTP của bạn là: " + otp + ". Mã có hiệu lực 10 phút.");
        otpCodes.put(email1,otp);
        otpExpiry.put(email1,System.currentTimeMillis()+otpTime);
        otpAttempts.put(email1,0);
    }

    public void sendMail( String to, String subject, String body) throws MessagingException {
        String sender = System.getenv("SMTP_EMAIL");
        String password = System.getenv("SMTP_APP_PASSWORD");

        if (sender == null || password == null) {
            throw new MessagingException("Server chưa cấu hình email");
        }
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to)
        );
        message.setSubject(subject, "UTF-8");
        message.setText(body,"UTF-8");
        Transport.send(message);
    }

    public void verifyOtp(String email, String code){
        String email1=email.trim().toLowerCase();
        if (otpCodes.get(email1)==null||otpExpiry.get(email1)<System.currentTimeMillis()){
            remove(email1);
            throw new IllegalArgumentException("Mã OTP không tồn tại hoặc đã hết hạn");
        }
        if(!otpCodes.get(email1).equals(code)){
            int count=otpAttempts.getOrDefault(email1,0)+1;
            otpAttempts.put(email1,count);
            if (count>=limit){
                remove(email1);
                throw new IllegalArgumentException("Nhập sai quá 3 lần, hãy gửi lại mã mới");
            }
            throw new IllegalArgumentException("Mã OTP không đúng");
        }
        remove(email1);
    }

    private void remove(String email) {
        otpCodes.remove(email);
        otpExpiry.remove(email);
        otpAttempts.remove(email);
    }

}
