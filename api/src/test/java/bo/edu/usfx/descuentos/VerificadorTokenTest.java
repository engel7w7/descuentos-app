package bo.edu.usfx.descuentos;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VerificadorTokenTest {

    @Test
    void sinTokenConfiguradoNoEstaConfigurado() {
        VerificadorToken verificadorNull = new VerificadorToken(null);
        assertFalse(verificadorNull.estaConfigurado());
        assertFalse(verificadorNull.esValido("cualquier-token"));

        VerificadorToken verificadorVacio = new VerificadorToken("   ");
        assertFalse(verificadorVacio.estaConfigurado());
    }

    @Test
    void tokenDemasiadoCortoNoEstaConfigurado() {
        VerificadorToken verificadorCorto = new VerificadorToken("corto123");
        assertFalse(verificadorCorto.estaConfigurado());
        assertFalse(verificadorCorto.esValido("corto123"));
    }

    @Test
    void tokenConfiguradoValidaCorrectamente() {
        String token = "token-de-longitud-suficiente-123456";
        VerificadorToken verificador = new VerificadorToken(token);
        assertTrue(verificador.estaConfigurado());
        assertTrue(verificador.esValido(token));
        assertFalse(verificador.esValido("otro-token-diferente"));
        assertFalse(verificador.esValido(null));
    }
}
