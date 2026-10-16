import { describe, it, expect } from 'vitest'
import { normalizarCodigo, codigoValido, redondear2, aplicarCupon, type Cupon } from './cupones'

describe('modulo-cupones - Ronda 2 (Reglas de negocio y valores límite)', () => {
  describe('normalizarCodigo', () => {
    it('elimina espacios en blanco en ambos extremos y convierte a mayúsculas', () => {
      expect(normalizarCodigo('  usfx10  ')).toBe('USFX10')
      expect(normalizarCodigo('\tpromo20\n')).toBe('PROMO20')
      expect(normalizarCodigo('ABCD12')).toBe('ABCD12')
    })
  })

  describe('codigoValido', () => {
    it('acepta códigos válidos de 4 letras y 2 dígitos incluso en minúsculas y con espacios', () => {
      expect(codigoValido('USFX10')).toBe(true)
      expect(codigoValido('  usfx10  ')).toBe(true)
      expect(codigoValido('abcd99')).toBe(true)
    })

    it('rechaza variaciones de longitud en letras (límite inferior y superior)', () => {
      expect(codigoValido('ABC10')).toBe(false) // 3 letras (menos)
      expect(codigoValido('ABCDE10')).toBe(false) // 5 letras (más)
    })

    it('rechaza variaciones de longitud en dígitos numéricos', () => {
      expect(codigoValido('ABCD1')).toBe(false) // 1 dígito (menos)
      expect(codigoValido('ABCD123')).toBe(false) // 3 dígitos (más)
    })

    it('rechaza caracteres especiales, letras ñ o cadenas vacías', () => {
      expect(codigoValido('ABCD-1')).toBe(false)
      expect(codigoValido('ABCD 12')).toBe(false)
      expect(codigoValido('')).toBe(false)
    })
  })

  describe('redondear2', () => {
    it('redondea con precisión bancaria y resuelve casos de sesgo de punto flotante (Number.EPSILON)', () => {
      expect(redondear2(1.005)).toBe(1.01) // Falla típica sin EPSILON
      expect(redondear2(1.004)).toBe(1.0)
      expect(redondear2(10.555)).toBe(10.56)
      expect(redondear2(10.554)).toBe(10.55)
      expect(redondear2(0)).toBe(0)
    })
  })

  describe('aplicarCupon - Regla 1: Validación del precio', () => {
    const cuponValido: Cupon = {
      codigo: 'USFX10',
      porcentaje: 10,
      topeBs: 50,
      vence: '2026-12-31',
    }
    const fechaValida = new Date('2026-10-16T10:00:00.000Z')

    it('rechaza valores límite en frontera cero y negativos', () => {
      expect(aplicarCupon(0, cuponValido, fechaValida)).toEqual({
        ok: false,
        motivo: 'El precio original debe ser mayor que cero',
      })
      expect(aplicarCupon(-0.01, cuponValido, fechaValida)).toEqual({
        ok: false,
        motivo: 'El precio original debe ser mayor que cero',
      })
      expect(aplicarCupon(-100, cuponValido, fechaValida)).toEqual({
        ok: false,
        motivo: 'El precio original debe ser mayor que cero',
      })
    })

    it('rechaza números no finitos (NaN, Infinity)', () => {
      expect(aplicarCupon(NaN, cuponValido, fechaValida)).toEqual({
        ok: false,
        motivo: 'El precio original debe ser mayor que cero',
      })
      expect(aplicarCupon(Infinity, cuponValido, fechaValida)).toEqual({
        ok: false,
        motivo: 'El precio original debe ser mayor que cero',
      })
    })

    it('acepta el valor mínimo positivo de precio (0.01)', () => {
      const res = aplicarCupon(0.01, cuponValido, fechaValida)
      expect(res).toEqual({
        ok: true,
        descuentoBs: 0,
        precioFinal: 0.01,
      })
    })
  })

  describe('aplicarCupon - Regla 2 y validación de entrada del cupón', () => {
    const fechaValida = new Date('2026-10-16T10:00:00.000Z')

    it('retorna error cuando el cupón es null', () => {
      expect(aplicarCupon(100, null, fechaValida)).toEqual({
        ok: false,
        motivo: 'No hay cupón',
      })
    })

    it('retorna error cuando el formato del cupón es inválido', () => {
      const cuponInvalido: Cupon = {
        codigo: 'INVALIDO',
        porcentaje: 10,
        topeBs: 50,
        vence: '2026-12-31',
      }
      expect(aplicarCupon(100, cuponInvalido, fechaValida)).toEqual({
        ok: false,
        motivo: 'El código del cupón no es válido',
      })
    })
  })

  describe('aplicarCupon - Regla 3: Vigencia y límites de fecha (inclusive)', () => {
    const cupon: Cupon = {
      codigo: 'USFX10',
      porcentaje: 10,
      topeBs: 50,
      vence: '2026-10-16',
    }

    it('acepta el cupón el mismo día de su vencimiento (límite inclusive)', () => {
      // Fecha idéntica al vencimiento en formato AAAA-MM-DD
      const hoyMismoDia = new Date('2026-10-16T23:59:59.000Z')
      const res = aplicarCupon(100, cupon, hoyMismoDia)
      expect(res).toEqual({
        ok: true,
        descuentoBs: 10,
        precioFinal: 90,
      })
    })

    it('acepta el cupón un día antes de su vencimiento', () => {
      const hoyAntes = new Date('2026-10-15T08:00:00.000Z')
      const res = aplicarCupon(100, cupon, hoyAntes)
      expect(res).toEqual({
        ok: true,
        descuentoBs: 10,
        precioFinal: 90,
      })
    })

    it('rechaza el cupón un día después de su vencimiento (frontera exclusive)', () => {
      const hoyDespues = new Date('2026-10-17T00:00:00.000Z')
      const res = aplicarCupon(100, cupon, hoyDespues)
      expect(res).toEqual({
        ok: false,
        motivo: 'El cupón venció',
      })
    })
  })

  describe('aplicarCupon - Reglas 4 y 5: Cálculo, redondeo, tope en Bs y precio no negativo', () => {
    const fechaValida = new Date('2026-10-16T10:00:00.000Z')

    it('calcula descuento porcentual exacto sin llegar al tope', () => {
      const cupon: Cupon = {
        codigo: 'USFX15',
        porcentaje: 15,
        topeBs: 50,
        vence: '2026-12-31',
      }
      // 150 * 0.15 = 22.5 Bs -> Final: 127.50 Bs
      expect(aplicarCupon(150, cupon, fechaValida)).toEqual({
        ok: true,
        descuentoBs: 22.5,
        precioFinal: 127.5,
      })
    })

    it('aplica el tope cuando el cálculo porcentual es exactamente igual al tope (frontera exacto)', () => {
      const cupon: Cupon = {
        codigo: 'USFX20',
        porcentaje: 20,
        topeBs: 40,
        vence: '2026-12-31',
      }
      // 200 * 0.20 = 40.00 Bs (exactamente igual a topeBs)
      expect(aplicarCupon(200, cupon, fechaValida)).toEqual({
        ok: true,
        descuentoBs: 40,
        precioFinal: 160,
      })
    })

    it('limita el descuento al tope cuando el porcentaje lo excede', () => {
      const cupon: Cupon = {
        codigo: 'USFX50',
        porcentaje: 50,
        topeBs: 30,
        vence: '2026-12-31',
      }
      // 200 * 0.50 = 100 Bs -> Limitado a 30 Bs
      expect(aplicarCupon(200, cupon, fechaValida)).toEqual({
        ok: true,
        descuentoBs: 30,
        precioFinal: 170,
      })
    })

    it('garantiza que el precio final nunca sea negativo si el descuento supera el precio total', () => {
      const cupon: Cupon = {
        codigo: 'USFX99',
        porcentaje: 100,
        topeBs: 500,
        vence: '2026-12-31',
      }
      // Precio 50 Bs con 100% descuento -> 0 Bs (no negativo)
      expect(aplicarCupon(50, cupon, fechaValida)).toEqual({
        ok: true,
        descuentoBs: 50,
        precioFinal: 0,
      })
    })
  })
  describe('Ronda 3: Pruebas dirigidas de robustez y mutantes sutiles', () => {
    it('neutraliza mutante de Number.EPSILON frente a imprecisiones de coma flotante en IEEE 754', () => {
      // Sin EPSILON, 1.005 * 100 evalúa internamente a 100.49999999999999 y redondearía a 1.00 en vez de 1.01.
      expect(redondear2(1.005)).toBe(1.01)
      expect(redondear2(35.555)).toBe(35.56)
    })

    it('neutraliza mutante de slice(0, 10) en fechaIso ante marcas de tiempo con hora límite', () => {
      // Si slice(0, 10) se omite, la cadena ISO completa ("2026-10-16T23:59:59.000Z")
      // superaría alfanuméricamente a "2026-10-16", venciendo el cupón antes de que termine el día.
      const cuponVenceHoy: Cupon = {
        codigo: 'USFX10',
        porcentaje: 10,
        topeBs: 50,
        vence: '2026-10-16',
      }
      const finDelDia = new Date('2026-10-16T23:59:59.999Z')
      expect(aplicarCupon(100, cuponVenceHoy, finDelDia)).toEqual({
        ok: true,
        descuentoBs: 10,
        precioFinal: 90,
      })
    })
  })
})
