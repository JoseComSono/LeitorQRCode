import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.StringJoiner;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class QRCodeReaderExample {

    public static void main(String[] args) throws ChecksumException, FormatException {
        String filePath = "C:\\Users\\seze9\\Desktop\\LeitorQRCode\\qrcodereader\\QRCODES\\1.png"; 
        
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            
            Reader reader = new QRCodeReader();
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            
            Result result = reader.decode(bitmap, hints);
            
            String[] data = result.getText().split(";"); 
            
            writeToCSV(data);
            
            System.out.println("Dados do QR Code foram organizados em um arquivo CSV com sucesso.");
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private static void writeToCSV(String[] data) {
        String csvFilePath = "qrcode.csv";
        
        try (FileWriter writer = new FileWriter(csvFilePath)) {
            StringJoiner joiner = new StringJoiner(",");
            
            for (String datum : data) {
                joiner.add(datum);
            }
            
            writer.append(joiner.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
