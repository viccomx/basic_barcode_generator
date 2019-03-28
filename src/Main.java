import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final int width = 1;
    private static final int height = 3;

    public static void main(String args[]) throws IOException {
        List<String> files = new ArrayList<>();
        files.add("single.csv");
        files.add("double.csv");
        files.add("triple.csv");

        for (String fileName : files) {
            processFile(fileName);
        }
    }

    public static void processFile(String fileName) throws IOException {
        BarcodeGenerator barcodeGenerator = new BarcodeGenerator();

        List<String> barCodeExceptions = new ArrayList<>();
        List<String> outputExceptions = new ArrayList<>();

        int totalElements = 0;
        int totalCorrectlyProcessed = 0;

        FileInputStream inputStream = null;
        Scanner scanner = null;
        try {
            inputStream = new FileInputStream(fileName);
            scanner = new Scanner(inputStream, "UTF-8");

            System.out.println("Start processing file " + fileName + "...");
            System.out.println();

            String fileNameNoExtension = fileName.replaceAll(".csv", "");
            while (scanner.hasNextLine()) {
                String readData = scanner.nextLine();

                totalElements++;

                // Get the data according to the excel format
                String data[] = readData.split(",");
                String code = data[0];

                try {
                    String desiredeData = code + (char) 13;
                    Barcode barcode = barcodeGenerator.createBarCode(desiredeData, width, height);
                    String barCodeName = fileNameNoExtension + "_" + code;
                    barcodeGenerator.exportBarcodeToPNG(barcode, barCodeName);
                    totalCorrectlyProcessed++;
                } catch (BarcodeException e) {
                    barCodeExceptions.add(code);
                } catch (OutputException e) {
                    outputExceptions.add(code);
                }

                if (totalElements % 1000 == 0) {
                    System.out.println(totalElements + " are done");
                    if (barCodeExceptions.size() > 0)
                        System.out.println("Error creating the bar code image till now: " + barCodeExceptions.size());
                    if (outputExceptions.size() > 0)
                        System.out.println("Error saving the image till now: " + outputExceptions.size());
                    System.out.println();
                }
            }

            // note that Scanner suppresses exceptions
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

        // Check the results
        if (totalCorrectlyProcessed == totalElements) {
            System.out.println("Yey! We have finished processing file " + fileName);
            System.out.println();
        } else {
            int errorCreatingBarCode = barCodeExceptions.size();
            System.out.println("Not able to create the bar code: " + errorCreatingBarCode);
            for (String code : barCodeExceptions) {
                System.out.println(code);
            }

            System.out.println();

            int errorOutput = outputExceptions.size();
            System.out.println("Not able to export to image: " + errorOutput);
            for (String code : outputExceptions) {
                System.out.println(code);
            }
        }
    }
}
