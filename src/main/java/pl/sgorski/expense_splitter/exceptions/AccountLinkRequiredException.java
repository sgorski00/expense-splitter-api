package pl.sgorski.expense_splitter.exceptions;

public final class AccountLinkRequiredException extends RuntimeException {
    public AccountLinkRequiredException() {
        super("Account link is required to perform this action");
    }
}
