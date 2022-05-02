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

describe('Rules e2e test', () => {
  const rulesPageUrl = '/rules';
  const rulesPageUrlPattern = new RegExp('/rules(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const rulesSample = { name: 'Bedfordshire' };

  let rules: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/rules+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/rules').as('postEntityRequest');
    cy.intercept('DELETE', '/api/rules/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (rules) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/rules/${rules.id}`,
      }).then(() => {
        rules = undefined;
      });
    }
  });

  it('Rules menu should load Rules page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('rules');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Rules').should('exist');
    cy.url().should('match', rulesPageUrlPattern);
  });

  describe('Rules page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(rulesPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Rules page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/rules/new$'));
        cy.getEntityCreateUpdateHeading('Rules');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', rulesPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/rules',
          body: rulesSample,
        }).then(({ body }) => {
          rules = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/rules+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [rules],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(rulesPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Rules page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('rules');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', rulesPageUrlPattern);
      });

      it('edit button click should load edit Rules page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Rules');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', rulesPageUrlPattern);
      });

      it('last delete button click should delete instance of Rules', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('rules').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', rulesPageUrlPattern);

        rules = undefined;
      });
    });
  });

  describe('new Rules page', () => {
    beforeEach(() => {
      cy.visit(`${rulesPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Rules');
    });

    it('should create an instance of Rules', () => {
      cy.get(`[data-cy="name"]`)
        .type('количества Региональный Интеллектуальный')
        .should('have.value', 'количества Региональный Интеллектуальный');

      cy.get(`[data-cy="code"]`).type('paradigms Большой').should('have.value', 'paradigms Большой');

      cy.setFieldImageAsBytesOfEntity('requirements', 'integration-test.png', 'image/png');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        rules = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', rulesPageUrlPattern);
    });
  });
});
