import com.hostelmate.util.PasswordUtil;

public class TestLogin {
    public static void main(String[] args) {
        String hash = PasswordUtil.hashPassword("password123");
        System.out.println("New hash: " + hash);
        boolean match = PasswordUtil.verifyPassword("password123", hash);
        System.out.println("Match? " + match);
    }
}
