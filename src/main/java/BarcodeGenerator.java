import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;

import java.io.File;

public class BarcodeGenerator {

    public Barcode createBarCode(String text, int width, int height)
            throws BarcodeException {
        Barcode barcode = BarcodeFactory.createCode128A(text);
        barcode.setBarWidth(width);
        barcode.setBarHeight(height);
        return barcode;
    }

    public void exportBarcodeToPNG(Barcode barcode, String fileName)
            throws OutputException {
        File f = new File(fileName);
        BarcodeImageHandler.savePNG(barcode, f);
    }
}
