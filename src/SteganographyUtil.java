import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Basic LSB (least significant bit) steganography for educational use.
 * Stores 1 bit in each color channel (R,G,B) per pixel.
 * Uses a NULL ('\0') terminator to mark message end when decoding.
 * Assumes UTF-8-safe payload (e.g., Base64 encrypted text).
 */
public class SteganographyUtil {

    /** Compute max characters we can embed (rough ASCII/UTF-8 char = 1 byte). */
    public static int getCapacity(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        int totalPixels = image.getWidth() * image.getHeight();
        return (totalPixels * 3) / 8; // 3 bits per pixel -> /8 to get bytes/characters
    }

    /** Encode message (string) into PNG. */
    public static void encodeMessage(String inputImagePath, String outputImagePath, String secretMessage) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputImagePath));
        int totalPixels = image.getWidth() * image.getHeight();
        int maxBytes = (totalPixels * 3) / 8;  // Each pixel contributes 3 bits

        // +1 for NULL terminator
        if (secretMessage.getBytes().length + 1 > maxBytes) {
            throw new IOException("Message too long! Max capacity: " + maxBytes + " bytes/characters.");
        }

        // Append NULL terminator
        secretMessage += '\0';
        byte[] msgBytes = secretMessage.getBytes();

        int msgIndex = 0;
        int bitIndex = 0;

        outerLoop:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (msgIndex >= msgBytes.length) break outerLoop;

                int rgb = image.getRGB(x, y);
                int[] colors = { (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF };

                for (int i = 0; i < 3; i++) {
                    if (msgIndex < msgBytes.length) {
                        colors[i] = (colors[i] & 0xFE) | ((msgBytes[msgIndex] >> (7 - bitIndex)) & 1);
                        bitIndex++;
                        if (bitIndex == 8) {
                            bitIndex = 0;
                            msgIndex++;
                        }
                    }
                }

                int newRGB = (colors[0] << 16) | (colors[1] << 8) | colors[2];
                image.setRGB(x, y, newRGB);
            }
        }

        ImageIO.write(image, "png", new File(outputImagePath));
    }

    /** Decode message from PNG (reads until NULL). */
    public static String decodeMessage(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        StringBuilder message = new StringBuilder();
        int currentByte = 0;
        int bitIndex = 0;

        outerLoop:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int rgb = image.getRGB(x, y);
                int[] colors = { (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF };

                for (int i = 0; i < 3; i++) {
                    currentByte = (currentByte << 1) | (colors[i] & 1);
                    bitIndex++;
                    if (bitIndex == 8) {
                        if (currentByte == 0) break outerLoop; // NULL term
                        message.append((char) currentByte);
                        bitIndex = 0;
                        currentByte = 0;
                    }
                }
            }
        }
        return message.toString();
    }
}
