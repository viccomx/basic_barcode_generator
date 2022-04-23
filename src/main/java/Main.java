import com.github.javaparser.utils.Log;
import groovy.util.logging.Slf4j;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Main {
    // Do not change the filesExtension, as this is the expected for all the program. Otherwise maybe the logic should be changed
    // a bit.
    private static final String filesExtension = "csv";
    private static final String imageFormat = "png";

    public static void main(String[] args) throws IOException {
        String containerFolder = "src/main/resources";
        String destinationFolder = "src/main/resources/barcodes";

        Log.info(String.format("Getting files to process from folder %1$s", containerFolder));
        ArrayList<String> fileNamesToProcess = getFileNamesToProcess(containerFolder, filesExtension);
        int totalFilesToProcess = fileNamesToProcess.size();
        if (totalFilesToProcess == 0) {
            System.out.println("Not found files to process");
            return;
        }

        // Create general destination folder
        boolean folderCreated = folderCreation(destinationFolder);
        if (!folderCreated) {
            System.out.println("ERROR - Not able to create destination folder");
            return;
        }

        int barcodeWidth = 1;
        int barcodeHeight = 3;
        Log.info(String.format("About to process %1$s files", fileNamesToProcess.size()));
        BarcodeGenerator barcodeGenerator = new BarcodeGenerator();
        for (String fileName : fileNamesToProcess) {
            String filePath = String.format("%1$s/%2$s", containerFolder, fileName);
            boolean fileCorrectlyProcessed = getBarCodesFromFile(filePath, fileName, destinationFolder, "UTF-8",
                    0, imageFormat, barcodeWidth, barcodeHeight, barcodeGenerator, true);
            if (fileCorrectlyProcessed) {
                System.out.println(String.format("Finished processing file - %1$s", fileName));
                System.out.println();
            }
        }
    }

    private static ArrayList<String> getFileNamesToProcess(String path, String requiredExtension) {
        ArrayList<String> fileNamesToProcess = new ArrayList<>();

        File containerFolder = new File(path);
        File[] files = containerFolder.listFiles();
        if (files == null || files.length == 0) {
            return fileNamesToProcess;
        }

        for (File file : files) {
            if (!file.isFile())
                continue;

            String fileName = file.getName();
            if (fileName.contains(requiredExtension))
                fileNamesToProcess.add(fileName);
        }

        return fileNamesToProcess;
    }

    private static boolean folderCreation(String folderName) {
        File directory = new File(folderName);
        if (!directory.exists()) {
            boolean folderCreated = directory.mkdir();
            if (!folderCreated) {
                System.out.println(String.format("ERROR - Not able to create the folder: %1$s", folderName));
                return false;
            }
            Log.info(String.format("%1$s folder was created", folderName));
        }
        return true;
    }

    /**
     * @param fileName:                filename of the file that contains the data that needs to be encoded
     * @param chartSet:                file charset
     * @param dataToEncodeColumnIndex: which column of the file has the data that needs to be encoded
     * @param imageFormat:             format of the desired image
     * @param width:                   width of the image
     * @param height:                  height of the image
     * @param barcodeGenerator:        Object
     * @param showErrors:              is we want to show the raised errors
     * @return if the file was processed correctly
     * @throws IOException: if not file was found
     */
    private static boolean getBarCodesFromFile(String filePath, String fileName, String destinationPath, String chartSet,
                                               int dataToEncodeColumnIndex, String imageFormat, int width, int height,
                                               BarcodeGenerator barcodeGenerator, boolean showErrors) throws IOException {
        List<String> barCodeExceptions = new ArrayList<>();
        List<String> outputExceptions = new ArrayList<>();

        int totalElements = 0;
        int totalCorrectlyProcessed = 0;

        FileInputStream inputStream = null;
        Scanner scanner = null;
        try {
            inputStream = new FileInputStream(filePath);
            scanner = new Scanner(inputStream, chartSet);

            Log.info(String.format("Start processing file %1$s...", fileName));

            // Create a folder if needed to group all the bar codes under the same specific folder.
            String regex = String.format(".%1$s", filesExtension);
            String folderName = fileName.replaceAll(regex, "");
            String destinationFolderPath = String.format("%1$s/%2$s", destinationPath, folderName);
            boolean folderCreated = folderCreation(destinationFolderPath);
            if (!folderCreated) {
                return false;
            }

            while (scanner.hasNextLine()) {
                String readData = scanner.nextLine();

                totalElements++;

                String data[] = readData.split(",");
                String code = data[dataToEncodeColumnIndex];

                try {
                    // Keep in mind that 13 is the <ENTER> special character and to use it the encoding should be 128A
                    String dataToEncode = code + (char) 13;
                    Barcode barcode = barcodeGenerator.createBarCode(dataToEncode, width, height);
                    String barcodePath = destinationFolderPath + "/" + code + "." + imageFormat;
                    barcodeGenerator.exportBarcodeToPNG(barcode, barcodePath);
                    totalCorrectlyProcessed++;
                } catch (BarcodeException e) {
                    barCodeExceptions.add(code);
                } catch (OutputException e) {
                    outputExceptions.add(code);
                }

                if (totalElements % 1000 == 0) {
                    Log.info(String.format("%1$s are done", totalElements));
                    if (barCodeExceptions.size() > 0)
                        Log.info(String.format("EROR - Creating the bar code image till now: %1$s", barCodeExceptions.size()));
                    if (outputExceptions.size() > 0)
                        Log.info(String.format("ERROR - Saving the image till now: %1$s", outputExceptions.size()));
                }
            }

            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        }

        if (totalCorrectlyProcessed == totalElements) {
            return true;
        }

        if (showErrors) {
            System.out.println(String.format("Not able to create the bar code:  %1$s", barCodeExceptions.size()));
            for (String code : barCodeExceptions) {
                System.out.println(code);
            }
            System.out.println(String.format("Not able to export to image: %1$s", outputExceptions.size()));
            for (String code : outputExceptions) {
                System.out.println(code);
            }
        }

        return false;
    }
}
