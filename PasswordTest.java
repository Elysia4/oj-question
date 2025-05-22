import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;

/**
 * Password Test Utility
 * Used to generate and compare passwords with different encryption methods
 */
public class PasswordTest {

    public static void main(String[] args) {
        // Password stored in database
        String dbPassword = "e64b78fc3bc91bcbc7dc232ba8ec59e0";
        
        System.out.println("Database Password: " + dbPassword);
        System.out.println("==========================");
        
        // Test common password combinations with different encodings
        String[] passwords = {"admin", "admin123", "123456", "password", "Admin123", "a123456", "admin@123"};
        
        for (String password : passwords) {
            System.out.println("\nTesting Password: " + password);
            System.out.println("--------------------------");
            
            // Try standard MD5
            testMD5(password, dbPassword);
            
            // Try MD5 with UTF-16 encoding
            testMD5WithEncoding(password, StandardCharsets.UTF_16.name(), dbPassword);
            
            // Try MD5 with ISO-8859-1 encoding
            testMD5WithEncoding(password, "ISO-8859-1", dbPassword);
            
            // Try MD5 with different output formats
            testMD5HexFormat(password, dbPassword);
        }
    }
    
    private static void testMD5(String rawPassword, String dbPassword) {
        try {
            // Standard MD5 with UTF-8
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            
            String md5Password = sb.toString();
            boolean isMatch = md5Password.equalsIgnoreCase(dbPassword);
            
            System.out.println("Standard MD5 (UTF-8): " + md5Password);
            System.out.println("Matches: " + isMatch);
            
            if (isMatch) {
                System.out.println("FOUND MATCH! Password is: " + rawPassword);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void testMD5WithEncoding(String rawPassword, String encoding, String dbPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(rawPassword.getBytes(encoding));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            
            String md5Password = sb.toString();
            boolean isMatch = md5Password.equalsIgnoreCase(dbPassword);
            
            System.out.println("MD5 with " + encoding + ": " + md5Password);
            System.out.println("Matches: " + isMatch);
            
            if (isMatch) {
                System.out.println("FOUND MATCH! Password is: " + rawPassword);
            }
        } catch (Exception e) {
            System.out.println("Error with " + encoding + ": " + e.getMessage());
        }
    }
    
    private static void testMD5HexFormat(String rawPassword, String dbPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            
            // Try different hex output format
            String md5Hex = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
            boolean isMatch = md5Hex.equalsIgnoreCase(dbPassword);
            
            System.out.println("MD5 Hex (alternate): " + md5Hex);
            System.out.println("Matches: " + isMatch);
            
            if (isMatch) {
                System.out.println("FOUND MATCH! Password is: " + rawPassword);
            }
        } catch (Exception e) {
            System.out.println("Error with hex format: " + e.getMessage());
        }
    }
} 