# Manual de usuario — Calculadora de Descuentos

_Versión corregida y verificada contra la aplicación real (Paso 6)._

## 1. ¿Para qué sirve?

La Calculadora de Descuentos permite conocer rápidamente el monto descontado y el precio final de un producto después de aplicarle un porcentaje de rebaja. Está pensada para vendedores, comerciantes y clientes de cualquier tienda.

## 2. Cómo ingresar

La aplicación es de acceso libre y directo desde cualquier navegador web moderno (Chrome, Edge o Firefox). No requiere iniciar sesión, crear cuentas ni ingresar correos o contraseñas.

## 3. Cómo calcular un descuento

1. Escriba el **Precio original (Bs)** en el primer campo. Debe ser un número mayor que cero (admite centavos con punto decimal, por ejemplo: `200` o `0.05`).
2. Escriba el **Porcentaje de descuento** en el segundo campo. Puede ir de `0` a `100` inclusive (no escriba el símbolo `%`).
3. Pulse el botón **Calcular descuento**. Mientras el sistema procesa la solicitud, el botón mostrará temporalmente el texto **Calculando...** y se mantendrá deshabilitado.
4. El resultado aparece debajo del formulario en una caja verde con dos líneas de información desglosada:
   - **Descuento aplicado:** `Bs XX,XX` (ejemplo: para 200 con 25 % es `Bs 50,00`; para 0,05 con 10 % es `Bs 0,01`)
   - **Precio final:** `Bs XX,XX` (ejemplo: para 200 con 25 % es `Bs 150,00`; para 0,05 con 10 % es `Bs 0,04`)

El redondeo aplicado es aritmético estándar (a la mitad más cercana, con mitad hacia arriba al segundo decimal). Las cifras con tres o más decimales como 16,999 se redondean a `Bs 17,00`.

## 4. Si algo sale mal

Si algún dato no cumple las validaciones de negocio, la aplicación muestra un recuadro rojo en la parte superior con el mensaje literal correspondiente:

- **«El precio original debe ser mayor que cero»**: si el precio se deja vacío, es 0 o es un número negativo.
- **«El porcentaje de descuento debe estar entre 0 y 100»**: si el porcentaje ingresado es menor a 0 o mayor a 100.

Corrija el valor señalado y vuelva a pulsar **Calcular descuento**.

## 5. Historial y almacenamiento

La herramienta no cuenta con pestaña de historial ni guarda operaciones anteriores. Los cálculos se procesan en tiempo real y no se registran en ninguna base de datos para proteger la privacidad del usuario.

## 6. Pie de página

Al final de la pantalla se muestra el estado de conexión con el motor de cálculo:

- **`Conectado a la API en: [ambiente]`** (en verde): indica que el servicio backend está disponible y operando con normalidad.
- **`Sin conexión con el servicio de cálculo`** (en rojo): indica que el servidor no está accesible; verifique su red o intente recargar el sitio.
