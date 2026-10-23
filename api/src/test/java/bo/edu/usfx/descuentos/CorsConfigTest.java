package bo.edu.usfx.descuentos;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CorsConfigTest {

    @Test
    void comodinLanzaIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> new CorsConfig(new String[]{"*"}));
        assertThrows(IllegalStateException.class, () -> new CorsConfig(new String[]{"http://localhost:5173", "*"}));
        assertThrows(IllegalStateException.class, () -> new CorsConfig(new String[]{"https://*.example.com"}));
    }

    @Test
    void origenesExplicitosNoLanzanExcepcion() {
        assertDoesNotThrow(() -> new CorsConfig(new String[]{"http://localhost:5173", "http://localhost:4173"}));
    }
}
