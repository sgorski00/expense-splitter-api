package pl.sgorski.expense_splitter.exceptions.authentication.two_fa;

public class CodeGenerationException extends RuntimeException {
  public CodeGenerationException(String message) {
    super(message);
  }
}
