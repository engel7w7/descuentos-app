# Práctica 10: Seguridad en el Pipeline y Endurecimiento de Software
**Asignatura:** COM450 – Calidad de Software  
**Universidad San Francisco Xavier de Chuquisaca**  
**Estudiante:** Jhojan Erick Romero Morales  
**Repositorio:** [github.com/engel7w7/descuentos-app](https://github.com/engel7w7/descuentos-app)  
**URL de la API (Render):** https://descuentos-app-cnaj.onrender.com  
**URL de la Web (Vercel):** https://descuentos-app-eight.vercel.app  

---

## 1. Registro de Hallazgos de Seguridad (Anexo B)

| ID | Herramienta | Descripción del Hallazgo | Categoría OWASP | Severidad | Estado | Corrección / Justificación |
|---|---|---|---|---|---|---|
| **SEC-01** | `auditoria-seguridad.sh` / MockMvc | Ausencia de cabeceras de seguridad en respuestas de la API (`nosniff`, `DENY`, `CSP`, `no-referrer`, `no-store`, `HSTS`). | A05:2021 - Security Misconfiguration | ALTA | **Corregido** | Commit `5016279`: Se implementó `FiltroCabecerasSeguridad.java` inyectando cabeceras defensivas en todas las peticiones (incluyendo errores). |
| **SEC-02** | `auditoria-seguridad.sh` / MockMvc | Exposición de detalles técnicos o stacktraces internos en respuestas de error 4xx/5xx. | A05:2021 - Security Misconfiguration | MEDIA | **Corregido** | Commit `5016279`: Respuestas de error estandarizadas y limpias sin revelar infraestructura interna. |
| **SEC-03** | `auditoria-seguridad.sh` | Configuración permisiva de CORS permitiendo orígenes arbitrarios (`*`). | A01:2021 - Broken Access Control | ALTA | **Corregido** | Commit `5016279`: Restricción estricta mediante variable de entorno `CORS_ORIGINS`, rechazando dominios no autorizados. |
| **SEC-04** | `auditoria-seguridad.sh` / MockMvc | Desbordamiento numérico: parámetro de entrada no acotado (`precio=1e300`) respondía 200 OK. | A03:2021 - Injection / Validation | ALTA | **Corregido** | Commit `5016279`: Validación en `CalculadoraDescuentos` con rechazo explícito `400 Bad Request` ante entradas astronómicas o inválidas. |
| **SEC-05** | `auditoria-seguridad.sh` / JUnit | Endpoint administrativo `/api/admin/estadisticas` sin autenticación ni token de protección. | A01:2021 - Broken Access Control | CRÍTICA | **Corregido** | Commit `5016279`: `VerificadorToken.java` exige `X-Admin-Token`, realiza comparación en tiempo constante (`MessageDigest.isEqual`) y falla cerrado si el servidor no tiene token configurado. |
| **SEC-06** | `buscar-secretos.sh` & `gitleaks` | Riesgo de filtración de tokens y claves privadas en archivos versionados y en el historial Git. | A07:2021 - Identification & Auth Failures | CRÍTICA | **Corregido** | Commit `bf35fb8`: Configuración de `.gitleaks.toml`, exclusión de secretos en commits y verificación con `fetch-depth: 0` en pipeline. |
| **SEC-07** | `Trivy` (SCA) | Vulnerabilidad crítica Text4Shell (`CVE-2022-42889`) introducida en `commons-text:1.9` durante experimento controlado. | A06:2021 - Vulnerable Components | CRÍTICA | **Corregido** | La dependencia de prueba fue descartada de `api/pom.xml`, garantizando 0 dependencias directas vulnerables. |
| **SEC-08** | `Trivy` (SCA) | Vulnerabilidades en Tomcat embebido (`CVE-2026-41293`, `CVE-2026-43512`, `CVE-2026-43515`, `CVE-2026-65182`, `CVE-2026-65905`, `CVE-2026-68525`). | A06:2021 - Vulnerable Components | CRÍTICA | **Aceptado** | Documentado en `.trivyignore`: HTTP/2 está deshabilitado en runner; la API usa autenticación stateless por cabecera (`X-Admin-Token`), no autenticación DIGEST/FORM. |
| **SEC-09** | `Trivy` (SCA) | Vulnerabilidades en Spring WebMVC (`CVE-2026-47884`, `CVE-2026-47890`) en `XsltView` y SSE. | A06:2021 - Vulnerable Components | CRÍTICA | **Aceptado** | Documentado en `.trivyignore`: La API es puramente REST JSON (`@RestController`) y no utiliza vistas XSLT ni streaming Server-Sent Events. |
| **SEC-10** | `OWASP ZAP` (DAST) | Alerta menor: cabecera `Content-Type` de Actuator (`application/vnd.spring-boot.actuator.v3+json`). | A05:2021 - Security Misconfiguration | BAJA | **Aceptado** | Comportamiento estándar del framework Spring Boot Actuator. Mapeado como aviso en `seguridad/zap-reglas.tsv`. |
| **SEC-11** | `auditoria-seguridad.sh` | Ausencia de cabeceras de seguridad y Content-Security-Policy en el front-end desplegado en Vercel. | A05:2021 - Security Misconfiguration | ALTA | **Corregido** | Commit `f6ae848`: Configuración del bloque `headers` en `web/vercel.json` con `nosniff`, `DENY`, `no-referrer`, `HSTS` y CSP restrictiva. |

---

## 2. Tabla Comparativa de Seguridad: Antes y Después (Paso 10)

| Métrica / Control de Seguridad | ANTES (Inicial / Práctica 9) | DESPUÉS (Práctica 10 Endurecida) |
|---|---|---|
| **Fallos de auditoría en API (`auditoria-seguridad.sh`)** | **7 fallos** (6 cabeceras ausentes + entrada 1e300) | **0 fallos** (Cabeceras activas, entrada 1e300 rechazada con 400) |
| **Fallos de auditoría en Web (`auditoria-seguridad.sh`)** | **4 fallos** (nosniff, frame-options, CSP, referrer) | **0 fallos** (Todas las cabeceras presentes en `vercel.json`) |
| **Total de FALLOS en Auditoría de Seguridad** | **11 fallos** | **0 fallos (100% OK en VERDE)** |
| **Alertas FAIL en OWASP ZAP (DAST con contrato OpenAPI)** | Alertas por endpoints no protegidos y falta de cabeceras | **0 alertas FAIL** (ZAP finaliza con código 0) |
| **Alertas WARN / Low en OWASP ZAP** | Múltiples avisos sin triaje | **2 advertencias menores** justificadas en contrato OpenAPI |
| **Vulnerabilidades CRITICAL en Trivy (SCA)** | Vulnerabilidades con parche disponible en librerías | **0 sin justificación** (Compuerta en verde con `.trivyignore`) |
| **Vulnerabilidades HIGH en Trivy / npm audit** | Dependencias sin auditar | **0 vulnerabilidades de producción** (`npm audit --omit=dev` limpio) |
| **Secretos en archivos versionados (`buscar-secretos.sh`)** | Posible fuga de tokens en properties/código | **0 secretos encontrados** |
| **Secretos en historial completo Git (`gitleaks`)** | Historial sin verificación automatizada | **0 secretos en historial** (`gitleaks-action` con `fetch-depth: 0`) |
| **Compuerta de Despliegue en Pipeline** | Despliegue sin validación de seguridad | **Despliegue condicionado**: `needs: [ci, seguridad]` |

---

## 3. Matriz de Amenazas (Actualización y Cierre del Modelo)

| ID | Categoría STRIDE / OWASP | Amenaza Identificada | Estado | Control Implementado / Mitigación | Amenazas Abiertas / Riesgo Residual |
|---|---|---|---|---|---|
| **AM-01** | **Tampering** / A03: Injection | Inyección de precios o porcentajes fuera de rango (`1e300`, negativos) provocando desbordamiento de memoria o denegación de servicio. | **Cubierta** | Validación defensiva en `CalculadoraDescuentos.java`: los valores deben estar en rangos estrictos o devuelven `400 Bad Request`. | Ninguno relevante en endpoints actuales. |
| **AM-02** | **Information Disclosure** / A05: Misconfiguration | Fuga de stacktraces, versiones de servidor o ataques de Clickjacking y MIME-Sniffing por falta de cabeceras HTTP. | **Cubierta** | `FiltroCabecerasSeguridad.java` en la API y bloque `headers` en `vercel.json` inyectan `nosniff`, `DENY`, `no-referrer`, `CSP`, `HSTS` y `no-store`. | Ninguno en los endpoints auditados. |
| **AM-03** | **Elevation of Privilege** / A01: Broken Access Control | Acceso no autorizado al endpoint administrativo `/api/admin/estadisticas` o ataque de temporización (timing attack) sobre el token. | **Cubierta** | `VerificadorToken.java` exige la cabecera `X-Admin-Token`, realiza comparación en tiempo constante con `MessageDigest.isEqual` y falla cerrado (403) si no hay token en el entorno. | Ninguno. El token se gestiona exclusivamente por variables de entorno. |
| **AM-04** | **Information Disclosure** / A07: Auth Failures | Filtración accidental de credenciales o tokens de administración en el repositorio Git público. | **Cubierta** | Filtro local `scripts/buscar-secretos.sh`, reglas en `.gitleaks.toml` y compuerta automatizada `gitleaks` sobre todo el historial (`fetch-depth: 0`). | Ninguno en commits versionados. |
| **AM-05** | **Denial of Service** / A06: Vulnerable Components | Explotación de vulnerabilidades conocidas (CVEs) en dependencias transitivas de Java o librerías de NPM. | **Mitigada y Gestionada** | Escaneo SCA automatizado con `Trivy` (`--ignore-unfixed`) y `npm audit --omit=dev`. Dependencias vulnerables eliminadas o evaluadas formalmente en `.trivyignore`. | **Abierta / Riesgo Residual**: Dependencias transitivas de Spring Boot 3.5.x sujetas a parches de la comunidad Spring en versiones mayores. |
| **AM-06** | **Tampering** / A03: XSS & Clickjacking | Modificación del DOM en el navegador o carga de scripts desde orígenes maliciosos en el cliente. | **Cubierta** | Política de Seguridad de Contenido (CSP) estricta en `web/vercel.json`: `default-src 'self'` y `connect-src` restringido únicamente al dominio propio y al backend de Render. | Ninguno. No se permiten scripts en línea (`unsafe-inline`). |
| **AM-07** | **Denial of Service** / A04: Insecure Design | Saturación de la API por envío masivo o concurrente de peticiones (abuso de recursos). | **Abierta (Documentada)** | Mitigación perimetral básica provista por el proxy de Cloudflare/Render en el borde. No existe rate limiting a nivel de aplicación (ej. Token Bucket / Redis). | **Abierta**: No se implementó limitación de tasa por IP dentro del código de la API. Documentada honestamente como deuda técnica para entornos productivos con alta concurrencia. |

---

## 4. Evidencias y Capturas Generadas

- **Captura 1 (`capturas/captura_salida1.png`):** Detección de secreto en archivo con `scripts/buscar-secretos.sh`.
- **Captura 2 (`capturas/captura_salida2.png`):** Detección de secreto en todo el historial con `gitleaks`.
- **Captura 7 (`capturas/captura_salida7.png`):** Detección de vulnerabilidad crítica Text4Shell (`CVE-2022-42889`) en `commons-text:1.9` con Trivy.
- **Captura 8 (`capturas/captura_salida8.png`):** Dependabot configurado en `.github/dependabot.yml` con 17+ Pull Requests de actualización automáticos.
- **Captura 9 (`capturas/captura_salida9.png`):** Grafo del workflow run en GitHub Actions mostrando jobs de `ci` y `seguridad` completados en verde y el despliegue esperando (`needs: [ci, seguridad]`).
- **Captura 10 (`capturas/captura_salida10.png`):** Artefacto `zap-informe` descargado y extraído (`zap-informe.html`, `zap-informe.json`, `api.log`) confirmando 0 alertas críticas en el contrato OpenAPI.
- **Captura 11 (`capturas/captura_salida11.png`):** Auditoría de seguridad final completa en verde (`scripts/auditoria-seguridad.sh`) contra los servicios desplegados con 24/24 controles en `OK`.
