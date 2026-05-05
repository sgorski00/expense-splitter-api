package pl.sgorski.expense_splitter.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class QrCodeUtilsTest {

  @Test
  void generate_shouldReturnByteArray_whenDataIsValid() {
    var data =
        "otpauth://totp/ExpenseSplitter:user@example.com?secret=JBSWY3DPEHPK3PXP&issuer=ExpenseSplitter";
    var size = 200;

    var result = QrCodeUtils.generate(data, size);

    assertTrue(result.isPresent());
    var qrCodeBytes = result.get();
    assertTrue(qrCodeBytes.length > 0);
  }

  @Test
  void generate_shouldReturnEmpty_whenSizeIsNotPositive() {
    var data = "some data";
    var size = 0;

    var result = QrCodeUtils.generate(data, size);

    assertTrue(result.isEmpty());
  }
}
