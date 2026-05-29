package gift.infrastructure;

import gift.domain.member.PasswordEncoder;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class Sha256PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "비밀번호 암호화 중 오류가 발생했습니다.");
        }
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
