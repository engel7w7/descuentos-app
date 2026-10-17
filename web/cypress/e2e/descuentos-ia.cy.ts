describe('Pruebas E2E Asistidas por IA - Calculadora de Descuentos', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('Flujo feliz: calcula el descuento y precio final correctamente', () => {
    cy.get('[data-cy="precio"]').clear().type('200')
    cy.get('[data-cy="porcentaje"]').clear().type('25')
    cy.get('[data-cy="calcular"]').click()

    cy.get('[data-cy="resultado"]').should('be.visible')
    cy.get('[data-cy="resultado"]').should('contain.text', 'Bs 150,00')
  })

  it('Error de precio: rechaza precio cero o negativo', () => {
    cy.get('[data-cy="precio"]').clear().type('0')
    cy.get('[data-cy="porcentaje"]').clear().type('15')
    cy.get('[data-cy="calcular"]').click()

    cy.get('[data-cy="error"]').should('be.visible')
    cy.get('[data-cy="error"]').should('have.text', 'El precio original debe ser mayor que cero')
  })

  it('Error de porcentaje: rechaza porcentaje mayor a 100', () => {
    cy.get('[data-cy="precio"]').clear().type('100')
    cy.get('[data-cy="porcentaje"]').clear().type('150')
    cy.get('[data-cy="calcular"]').click()

    cy.get('[data-cy="error"]').should('be.visible')
    cy.get('[data-cy="error"]').should(
      'have.text',
      'El porcentaje de descuento debe estar entre 0 y 100',
    )
  })
})
