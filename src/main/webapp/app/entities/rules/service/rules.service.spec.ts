import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRules, Rules } from '../rules.model';

import { RulesService } from './rules.service';

describe('Rules Service', () => {
  let service: RulesService;
  let httpMock: HttpTestingController;
  let elemDefault: IRules;
  let expectedResult: IRules | IRules[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RulesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      code: 'AAAAAAA',
      requirementsContentType: 'image/png',
      requirements: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Rules', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Rules()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Rules', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          code: 'BBBBBB',
          requirements: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Rules', () => {
      const patchObject = Object.assign(
        {
          code: 'BBBBBB',
        },
        new Rules()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Rules', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          code: 'BBBBBB',
          requirements: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Rules', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRulesToCollectionIfMissing', () => {
      it('should add a Rules to an empty array', () => {
        const rules: IRules = { id: 123 };
        expectedResult = service.addRulesToCollectionIfMissing([], rules);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(rules);
      });

      it('should not add a Rules to an array that contains it', () => {
        const rules: IRules = { id: 123 };
        const rulesCollection: IRules[] = [
          {
            ...rules,
          },
          { id: 456 },
        ];
        expectedResult = service.addRulesToCollectionIfMissing(rulesCollection, rules);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Rules to an array that doesn't contain it", () => {
        const rules: IRules = { id: 123 };
        const rulesCollection: IRules[] = [{ id: 456 }];
        expectedResult = service.addRulesToCollectionIfMissing(rulesCollection, rules);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rules);
      });

      it('should add only unique Rules to an array', () => {
        const rulesArray: IRules[] = [{ id: 123 }, { id: 456 }, { id: 91274 }];
        const rulesCollection: IRules[] = [{ id: 123 }];
        expectedResult = service.addRulesToCollectionIfMissing(rulesCollection, ...rulesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const rules: IRules = { id: 123 };
        const rules2: IRules = { id: 456 };
        expectedResult = service.addRulesToCollectionIfMissing([], rules, rules2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rules);
        expect(expectedResult).toContain(rules2);
      });

      it('should accept null and undefined values', () => {
        const rules: IRules = { id: 123 };
        expectedResult = service.addRulesToCollectionIfMissing([], null, rules, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(rules);
      });

      it('should return initial array if no Rules is added', () => {
        const rulesCollection: IRules[] = [{ id: 123 }];
        expectedResult = service.addRulesToCollectionIfMissing(rulesCollection, undefined, null);
        expect(expectedResult).toEqual(rulesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
