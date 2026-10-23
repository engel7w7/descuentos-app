package bo.edu.usfx.descuentos;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminControllerTest {

    private static final String TOKEN_VALIDO = "token-secreto-para-pruebas-1234567890";
    private Estadisticas estadisticas;
    private MockMvc mvcConToken;
    private MockMvc mvcSinToken;

    @BeforeEach
    void setUp() {
        estadisticas = new Estadisticas();
        VerificadorToken verificadorValido = new VerificadorToken(TOKEN_VALIDO);
        VerificadorToken verificadorInvalido = new VerificadorToken(null);

        mvcConToken = MockMvcBuilders.standaloneSetup(new AdminController(estadisticas, verificadorValido)).build();
        mvcSinToken = MockMvcBuilders.standaloneSetup(new AdminController(estadisticas, verificadorInvalido)).build();
    }

    @Test
    void sinTokenConfiguradoResponde403InclusoConToken() throws Exception {
        mvcSinToken.perform(get("/api/admin/estadisticas")
                        .header("X-Admin-Token", TOKEN_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinTokenConfiguradoResponde403SinToken() throws Exception {
        mvcSinToken.perform(get("/api/admin/estadisticas"))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinTokenResponde401() throws Exception {
        mvcConToken.perform(get("/api/admin/estadisticas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenIncorrectoResponde401() throws Exception {
        mvcConToken.perform(get("/api/admin/estadisticas")
                        .header("X-Admin-Token", "token-completamente-invalido-000"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenCorrectoResponde200ConCantidadDeCalculos() throws Exception {
        estadisticas.registrarCalculo();
        estadisticas.registrarCalculo();

        mvcConToken.perform(get("/api/admin/estadisticas")
                        .header("X-Admin-Token", TOKEN_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calculos", is(2)));
    }
}
