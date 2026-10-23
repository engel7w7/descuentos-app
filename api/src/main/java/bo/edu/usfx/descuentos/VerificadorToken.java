package bo.edu.usfx.descuentos;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VerificadorToken {

    public static final int LONGITUD_MINIMA = 16;
    private final String tokenEsperado;

    public VerificadorToken(@Value("${ADMIN_TOKEN:#{null}}") String tokenConfig) {
        this.tokenEsperado = tokenConfig;
    }

    public boolean estaConfigurado() {
        return tokenEsperado != null && tokenEsperado.trim().length() >= LONGITUD_MINIMA;
    }

    public boolean esValido(String tokenEntrante) {
        if (!estaConfigurado() || tokenEntrante == null) {
            return false;
        }
        byte[] a = tokenEntrante.getBytes(StandardCharsets.UTF_8);
        byte[] b = tokenEsperado.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }
}
