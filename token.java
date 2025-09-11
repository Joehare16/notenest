import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class token {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("note"));
    }
}
