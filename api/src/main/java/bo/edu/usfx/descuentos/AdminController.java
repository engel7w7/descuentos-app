package bo.edu.usfx.descuentos;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final Estadisticas estadisticas;
    private final VerificadorToken verificador;

    public AdminController(Estadisticas estadisticas, VerificadorToken verificador) {
        this.estadisticas = estadisticas;
        this.verificador = verificador;
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> estadisticas(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        if (!verificador.estaConfigurado()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Administración deshabilitada"));
        }
        if (token == null || !verificador.esValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token no proporcionado o inválido"));
        }
        return ResponseEntity.ok(Map.of("calculos", estadisticas.getCalculos()));
    }
}
