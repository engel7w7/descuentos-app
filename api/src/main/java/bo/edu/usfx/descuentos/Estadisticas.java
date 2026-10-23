package bo.edu.usfx.descuentos;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class Estadisticas {

    private final AtomicLong calculos = new AtomicLong();

    public void registrarCalculo() {
        calculos.incrementAndGet();
    }

    public long getCalculos() {
        return calculos.get();
    }
}
