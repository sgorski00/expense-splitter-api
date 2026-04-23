package pl.sgorski.expense_splitter.exceptions;

/** Exception thrown when a user exceeds the allowed number of requests in a given time frame. */
public final class TooManyRequestsException extends RuntimeException {
  public TooManyRequestsException() {
    super("Too many requests. Please try again later.");
  }
}
