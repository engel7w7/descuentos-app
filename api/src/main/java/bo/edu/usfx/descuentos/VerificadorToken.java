package bo.edu.usfx.descuentos;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VerificadorToken {

    public static final int LONGITUD_MINIMA = 16;
    private final String tokenConfigurado;

    public VerificadorToken(@Value("${ADMIN_TOKEN:#{null}}") String tokenConfigurado) {
        this.tokenConfigurado = tokenConfigurado;
    }

    public boolean estaConfigurado() {
        return tokenConfigurado != null && tokenConfigurado.trim().length() >= LONGITUD_MINIMA;
    }

    public boolean esValido(String tokenEntrante) {
        if (!estaConfigurado() || tokenEntrante == null) {
            return false;
        }
        byte[] a = tokenEntrante.getBytes(StandardCharsets.UTF_8);
        byte[] b = tokenConfigurado.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }
}
