package bo.edu.usfx.descuentos;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SeguridadApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void todasLasRespuestasLlevanLasCabecerasDeSeguridad() throws Exception {
        mvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Content-Security-Policy", containsString("default-src 'none'")))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("Strict-Transport-Security", containsString("max-age=")));
    }

    @Test
    void cuerpoIlegibleDa400SinRevelarDetallesInternos() throws Exception {
        mvc.perform(post("/api/descuento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("La solicitud no es válida")))
                .andExpect(content().string(not(containsString("Exception"))))
                .andExpect(content().string(not(containsString("bo.edu"))));
    }

    @Test
    void rutaInexistenteDa404SinRevelarDetallesInternos() throws Exception {
        mvc.perform(get("/api/ruta-que-no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("No encontrado")))
                .andExpect(content().string(not(containsString("Exception"))))
                .andExpect(content().string(not(containsString("bo.edu"))));
    }

    @Test
    void reglaDeNegocioDevuelveMensajeExacto() throws Exception {
        mvc.perform(post("/api/descuento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":0,\"porcentaje\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El precio original debe ser mayor que cero")));
    }

    @Test
    void entradaAcotadaRechazaPrecioAbsurdo() throws Exception {
        mvc.perform(post("/api/descuento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":1e300,\"porcentaje\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("Exception"))))
                .andExpect(content().string(not(containsString("bo.edu"))));
    }
}
