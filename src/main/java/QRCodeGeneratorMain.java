import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Generates a QR code for the given string
 *
 * @author Nelson Victor Cruz Hernández
 */
public class QRCodeGeneratorMain {
    final static QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();

    final static String destinationFolder = "src/main/resources";

    public static void main(String args[]) {
        qrCodeGenerator.qrCodeGenerationWithFixedCorrectionLevel("something", "wedding", "png",
            100, 100, destinationFolder, ErrorCorrectionLevel.H);
    }
}
