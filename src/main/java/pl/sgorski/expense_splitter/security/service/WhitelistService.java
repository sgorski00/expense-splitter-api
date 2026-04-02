package pl.sgorski.expense_splitter.security.service;

public interface WhitelistService {
  boolean isWhitelisted(String path);
}
