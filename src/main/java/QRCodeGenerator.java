import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.vavr.control.Either;
import io.vavr.control.Try;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Map;

/**
 * Code generator for QR
 *
 * @author Nelson Victor Cruz Hernández
 */
public class QRCodeGenerator {
    final private QRCodeWriter qrCodeWriter = new QRCodeWriter();

    public Either<Throwable, Boolean> qrCodeGenerationWithFixedCorrectionLevel(final String content,
                                                                                final String fileName,
                                                                                final String format,
                                                                                final int width,
                                                                                final int height,
                                                                                final String destinationFolder,
                                                                                final ErrorCorrectionLevel errorCorrectionLevel) {
        final var hints = Map.of(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        return qrCodeGeneration(content, fileName, format, width, height, destinationFolder, hints);
    }

    private Either<Throwable, Boolean> qrCodeGeneration(final String content, final String fileName,
                                                        final String format, final int width, final int height,
                                                        final String destinationFolder,
                                                        final Map<EncodeHintType, ?> hints) {
        return Try.of(() -> qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints))
            .map(MatrixToImageWriter::toBufferedImage)
            .mapTry(image -> {
                final var fileNameWithFormat = "%s.%s".formatted(fileName, format);
                final var imageFile = new File(destinationFolder, fileNameWithFormat);
                return ImageIO.write(image, format, imageFile);
            }).toEither();
    }
}
