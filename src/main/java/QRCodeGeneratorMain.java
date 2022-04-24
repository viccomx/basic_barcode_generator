import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Map;

/**
 * Generates a QR code for the given string
 *
 * @author Nelson Victor Cruz Hernández
 */
public class QRCodeGeneratorMain {
    final static QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();

    public static void main(String args[]) {
        weddingQrCodesWithMonogram();
    }

    private static void authorGithubProfileQrCode() {
        final var destinationFolder = "src/main/resources";
        qrCodeGenerator.qrCodeGeneration("https://github.com/viccomx", "viccomx", "png", 100, 100, destinationFolder, Map.of());
    }

    private static void weddingQrCodesWithMonogram() {
        // Set the highest error correction possible
        final var hints = Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        // Get the monogram as overlap image
        final var monogramFileName = "src/main/resources/wedding/monogram_cgam_nvch.png";

        // Destination folder
        final var destinationFolder = "src/main/resources/wedding/qr_codes";

        // Required file format
        final var format = "png";

        // QR code width
        final var width = 5000;

        // QR code height
        final var height = 5000;

        Map.of("btc_address", "3FMARipCrZH5vXGeabWtj6VRVGVDDA3bnv",
                "eth_address", "0x3d8843740254C1CAc484C34ddbF4c6072990f4BB",
                "venue", "https://goo.gl/maps/kCRtex66f8T9TySG7")
            .forEach((key, value) -> qrCodeGenerator.qrCodeGenerationWithOverlapImage(value, monogramFileName,
                key, format, width, height, destinationFolder, hints));
    }
}
