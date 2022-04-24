import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * Code generator for QR
 *
 * @author Nelson Victor Cruz Hernández
 */
@Slf4j
@Data
public class QRCodeGenerator {
    final private QRCodeWriter qrCodeWriter = new QRCodeWriter();

    public Either<Throwable, Boolean> qrCodeGeneration(final String content, final String fileName,
                                                       final String format, final int width, final int height,
                                                       final String destinationFolder,
                                                       final Map<EncodeHintType, ?> hints) {
        return qrCodeImageGeneration(content, width, height, hints)
            .flatMap(bufferedImage -> saveImage(bufferedImage, destinationFolder, fileName, format))
            .toEither();
    }

    public Either<Throwable, Boolean> qrCodeGenerationWithOverlapImage(final String content,
                                                                       final String overlapFilename,
                                                                       final String fileName, final String format,
                                                                       final int width, final int height,
                                                                       final String destinationFolder,
                                                                       final Map<EncodeHintType, ?> hints) {
        final var overLapImage = loadImage(overlapFilename);
        return qrCodeImageGeneration(content, width, height, hints)
            .toEither()
            .map(qrCodeImage -> {
                if (overLapImage.isEmpty()) {
                    throw new RuntimeException("Overlap image is not present");
                }
                return combineImages(qrCodeImage, overLapImage.get(), width, height);
            }).flatMap(bufferedImage -> saveImage(bufferedImage, destinationFolder, fileName, format).toEither());
    }

    private Try<BufferedImage> qrCodeImageGeneration(final String content, final int width, final int height,
                                                     final Map<EncodeHintType, ?> hints) {
        return Try.of(() -> qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints))
            .onFailure(throwable -> log.error("Not able to encode content: {}", content, throwable))
            .map(MatrixToImageWriter::toBufferedImage)
            .onFailure(throwable -> log.error("Not able to create QR code image for content: {}", content, throwable));
    }

    private Optional<BufferedImage> loadImage(final String path) {
        return Try.of(() -> new File(path))
            .onFailure(throwable -> {
                log.error("Not able to create file {}", path, throwable);
                System.out.println("Not able to create URL");
            })
            .mapTry(ImageIO::read)
            .onFailure(throwable -> {
                log.error("Not able to read image from  URL {} to load image", path, throwable);
                System.out.println("Not able to read from URL");
            })
            .toJavaOptional();
    }

    private Try<Boolean> saveImage(final BufferedImage bufferedImage, final String destinationFolder,
                                   final String fileName, final String format) {
        final var fileNameWithFormat = "%s.%s".formatted(fileName, format);
        final var imageFile = new File(destinationFolder, fileNameWithFormat);
        return Try.of(() -> ImageIO.write(bufferedImage, format, imageFile))
            .onFailure(throwable -> log.error("Not able to save image {}", fileNameWithFormat, throwable));
    }

    private BufferedImage combineImages(final BufferedImage image, final BufferedImage overLayerImage,
                                        final int width, final int height) {
        //Calculate the delta height and width
        final var deltaHeight = image.getHeight() - overLayerImage.getHeight();
        final var deltaWidth = image.getWidth() - overLayerImage.getHeight();

        // Draw the new image
        final var combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var graphics = (Graphics2D) combined.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        graphics.drawImage(overLayerImage, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);

        return combined;
    }
}
