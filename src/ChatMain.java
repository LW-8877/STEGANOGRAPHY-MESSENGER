/**
 * CLI interface for Encrypted Steganography Messenger.
 * send: java ChatMain send <input.png> <output.png> <message> <password>
 * read: java ChatMain read <encoded.png> <password>
 */
public class ChatMain {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                usage();
                return;
            }
            String cmd = args[0].toLowerCase();
            if ("send".equals(cmd)) {
                if (args.length < 5) {
                    usage();
                    return;
                }
                String in = args[1];
                String out = args[2];
                String msg = args[3];
                String pwd = args[4];

                String encrypted = AESUtil.encrypt(msg, pwd);
                int cap = SteganographyUtil.getCapacity(in);
                if (encrypted.length() + 1 > cap) {
                    System.err.println("Encrypted message too large for carrier image. Capacity: " + cap + " bytes.");
                    return;
                }
                SteganographyUtil.encodeMessage(in, out, encrypted);
                System.out.println("Encrypted message hidden in " + out);
            } else if ("read".equals(cmd)) {
                if (args.length < 3) {
                    usage();
                    return;
                }
                String encoded = args[1];
                String pwd = args[2];
                String hidden = SteganographyUtil.decodeMessage(encoded);
                String decrypted = AESUtil.decrypt(hidden, pwd);
                System.out.println("Decrypted message: " + decrypted);
            } else {
                usage();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println("  java ChatMain send <input.png> <output.png> <message> <password>");
        System.out.println("  java ChatMain read <encoded.png> <password>");
    }
}
