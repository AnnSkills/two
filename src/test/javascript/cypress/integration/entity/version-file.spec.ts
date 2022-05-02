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

describe('VersionFile e2e test', () => {
  const versionFilePageUrl = '/version-file';
  const versionFilePageUrlPattern = new RegExp('/version-file(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const versionFileSample = { name: 'Cross-platform Berkshire leading' };

  let versionFile: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/version-files+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/version-files').as('postEntityRequest');
    cy.intercept('DELETE', '/api/version-files/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (versionFile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/version-files/${versionFile.id}`,
      }).then(() => {
        versionFile = undefined;
      });
    }
  });

  it('VersionFiles menu should load VersionFiles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('version-file');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('VersionFile').should('exist');
    cy.url().should('match', versionFilePageUrlPattern);
  });

  describe('VersionFile page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(versionFilePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create VersionFile page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/version-file/new$'));
        cy.getEntityCreateUpdateHeading('VersionFile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', versionFilePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/version-files',
          body: versionFileSample,
        }).then(({ body }) => {
          versionFile = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/version-files+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [versionFile],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(versionFilePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details VersionFile page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('versionFile');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', versionFilePageUrlPattern);
      });

      it('edit button click should load edit VersionFile page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('VersionFile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', versionFilePageUrlPattern);
      });

      it('last delete button click should delete instance of VersionFile', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('versionFile').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', versionFilePageUrlPattern);

        versionFile = undefined;
      });
    });
  });

  describe('new VersionFile page', () => {
    beforeEach(() => {
      cy.visit(`${versionFilePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('VersionFile');
    });

    it('should create an instance of VersionFile', () => {
      cy.get(`[data-cy="name"]`).type('специалист Неодимовый').should('have.value', 'специалист Неодимовый');

      cy.setFieldImageAsBytesOfEntity('sourceCode', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="creationDate"]`).type('2022-05-01T23:24').should('have.value', '2022-05-01T23:24');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        versionFile = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', versionFilePageUrlPattern);
    });
  });
});
