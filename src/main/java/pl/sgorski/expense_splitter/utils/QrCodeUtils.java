package pl.sgorski.expense_splitter.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

public final class QrCodeUtils {

  public static final String IMAGE_FORMAT = "PNG";

  public static Optional<byte[]> generate(String data, int size) {
    if (size < 1) return Optional.empty();

    try {
      var matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size);
      var outputStream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, outputStream);
      return Optional.of(outputStream.toByteArray());
    } catch (IOException | WriterException e) {
      return Optional.empty();
    }
  }
}
