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

describe('Support e2e test', () => {
  const supportPageUrl = '/support';
  const supportPageUrlPattern = new RegExp('/support(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const supportSample = { email: 'NMC-/0@\\SSSSSS', phone: '\\\\9757 1 321 5 129 4 68' };

  let support: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/supports+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/supports').as('postEntityRequest');
    cy.intercept('DELETE', '/api/supports/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (support) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/supports/${support.id}`,
      }).then(() => {
        support = undefined;
      });
    }
  });

  it('Supports menu should load Supports page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('support');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Support').should('exist');
    cy.url().should('match', supportPageUrlPattern);
  });

  describe('Support page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(supportPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Support page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/support/new$'));
        cy.getEntityCreateUpdateHeading('Support');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', supportPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/supports',
          body: supportSample,
        }).then(({ body }) => {
          support = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/supports+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [support],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(supportPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Support page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('support');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', supportPageUrlPattern);
      });

      it('edit button click should load edit Support page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Support');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', supportPageUrlPattern);
      });

      it('last delete button click should delete instance of Support', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('support').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', supportPageUrlPattern);

        support = undefined;
      });
    });
  });

  describe('new Support page', () => {
    beforeEach(() => {
      cy.visit(`${supportPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Support');
    });

    it('should create an instance of Support', () => {
      cy.get(`[data-cy="topic"]`).type('Клатч exuding Чувашская').should('have.value', 'Клатч exuding Чувашская');

      cy.get(`[data-cy="email"]`).type('&gt;@SSS').should('have.value', '&gt;@SSS');

      cy.get(`[data-cy="phone"]`).type('\\\\\2 98 4829 3 434 6').should('have.value', '\\\\\2 98 4829 3 434 6');

      cy.get(`[data-cy="description"]`).type('беспроводной Карачаево-Черкессия').should('have.value', 'беспроводной Карачаево-Черкессия');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        support = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', supportPageUrlPattern);
    });
  });
});
