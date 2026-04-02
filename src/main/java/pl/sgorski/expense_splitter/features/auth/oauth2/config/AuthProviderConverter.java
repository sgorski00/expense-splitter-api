package pl.sgorski.expense_splitter.features.auth.oauth2.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;

@Component
public final class AuthProviderConverter implements Converter<String, AuthProvider> {
    @Override
    public AuthProvider convert(String source) {
        return AuthProvider.fromString(source);
    }
}
