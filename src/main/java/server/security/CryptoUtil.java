// ===== CryptoUtil.java =====
/**
 * LÀM GÌ: Các hàm mã hóa dùng chung: generateRsaKeyPair() (RSA 2048 bit), rsaEncrypt / rsaDecrypt
 *         (chỉ để trao khóa AES), generateAesKey() (AES 256 bit), aesEncrypt / aesDecrypt
 *         (AES/GCM/NoPadding, mỗi gói một IV ngẫu nhiên 12 byte đặt đầu gói).
 * GỌI TỚI: không (chỉ dùng thư viện có sẵn của Java).
 * ĐƯỢC GỌI TỪ: SecureChannel (cả phía server lẫn client).
 * YÊU CẦU ĐỀ: mã hóa lai, bất đối xứng (RSA) để trao khóa phiên, đối xứng (AES) để mã nội dung.
 *             Bước 8.
 */
package server.security;

/**
 * YÊU CẦU ĐỀ: "Mã hóa lai: bất đối xứng (vd RSA) để trao khóa phiên,
 * đối xứng (vd AES) để mã nội dung; khóa phiên khác nhau cho mỗi client".
 *
 * TODO:
 *  - generateRsaKeyPair(): server sinh cặp khóa RSA 2048 bit khi khởi động
 *  - rsaEncrypt(byte[] data, PublicKey key) / rsaDecrypt(byte[] data, PrivateKey key)
 *  - generateAesKey(): client sinh khóa AES 256 bit ngẫu nhiên cho mỗi phiên
 *  - aesEncrypt(byte[] data, SecretKey key) / aesDecrypt(...)
 *
 * GHI CHÚ:
 *  - RSA chỉ dùng để trao đổi khóa AES (vì RSA chậm, chỉ mã được dữ liệu ngắn).
 *  - AES dùng chế độ GCM ("AES/GCM/NoPadding"), mỗi gói một IV ngẫu nhiên 12 byte,
 *    đặt IV ở đầu gói rồi đến ciphertext.
 *  - Đây chỉ là các hàm tiện ích, việc bọc kênh truyền nằm ở SecureChannel.
 *  - Làm ở bước gần cuối, sau khi các chức năng chạy được dạng text thường.
 */
public class CryptoUtil {

}