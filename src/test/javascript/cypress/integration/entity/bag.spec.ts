import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Bag e2e test', () => {
  const bagPageUrl = '/bag';
  const bagPageUrlPattern = new RegExp('/bag(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const bagSample = { bagName: 'Галантерея Меховой' };

  let bag: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/bags+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/bags').as('postEntityRequest');
    cy.intercept('DELETE', '/api/bags/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (bag) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/bags/${bag.id}`,
      }).then(() => {
        bag = undefined;
      });
    }
  });

  it('Bags menu should load Bags page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('bag');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Bag').should('exist');
    cy.url().should('match', bagPageUrlPattern);
  });

  describe('Bag page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(bagPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Bag page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/bag/new$'));
        cy.getEntityCreateUpdateHeading('Bag');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', bagPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/bags',
          body: bagSample,
        }).then(({ body }) => {
          bag = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/bags+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [bag],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(bagPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Bag page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('bag');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', bagPageUrlPattern);
      });

      it('edit button click should load edit Bag page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Bag');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', bagPageUrlPattern);
      });

      it('last delete button click should delete instance of Bag', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('bag').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', bagPageUrlPattern);

        bag = undefined;
      });
    });
  });

  describe('new Bag page', () => {
    beforeEach(() => {
      cy.visit(`${bagPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Bag');
    });

    it('should create an instance of Bag', () => {
      cy.get(`[data-cy="bagName"]`).type('Malagasy visualize Ботинок').should('have.value', 'Malagasy visualize Ботинок');

      cy.get(`[data-cy="description"]`).type('Областной мятный Кот-д&#39;Ивуар').should('have.value', 'Областной мятный Кот-д&#39;Ивуар');

      cy.get(`[data-cy="status"]`).select('CLOSED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        bag = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', bagPageUrlPattern);
    });
  });
});
