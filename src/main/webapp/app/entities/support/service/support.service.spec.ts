import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISupport, Support } from '../support.model';

import { SupportService } from './support.service';

describe('Support Service', () => {
  let service: SupportService;
  let httpMock: HttpTestingController;
  let elemDefault: ISupport;
  let expectedResult: ISupport | ISupport[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SupportService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      topic: 'AAAAAAA',
      email: 'AAAAAAA',
      phone: 'AAAAAAA',
      description: 'AAAAAAA',
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

    it('should create a Support', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Support()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Support', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          topic: 'BBBBBB',
          email: 'BBBBBB',
          phone: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Support', () => {
      const patchObject = Object.assign(
        {
          email: 'BBBBBB',
          phone: 'BBBBBB',
          description: 'BBBBBB',
        },
        new Support()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Support', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          topic: 'BBBBBB',
          email: 'BBBBBB',
          phone: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should delete a Support', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSupportToCollectionIfMissing', () => {
      it('should add a Support to an empty array', () => {
        const support: ISupport = { id: 123 };
        expectedResult = service.addSupportToCollectionIfMissing([], support);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(support);
      });

      it('should not add a Support to an array that contains it', () => {
        const support: ISupport = { id: 123 };
        const supportCollection: ISupport[] = [
          {
            ...support,
          },
          { id: 456 },
        ];
        expectedResult = service.addSupportToCollectionIfMissing(supportCollection, support);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Support to an array that doesn't contain it", () => {
        const support: ISupport = { id: 123 };
        const supportCollection: ISupport[] = [{ id: 456 }];
        expectedResult = service.addSupportToCollectionIfMissing(supportCollection, support);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(support);
      });

      it('should add only unique Support to an array', () => {
        const supportArray: ISupport[] = [{ id: 123 }, { id: 456 }, { id: 68010 }];
        const supportCollection: ISupport[] = [{ id: 123 }];
        expectedResult = service.addSupportToCollectionIfMissing(supportCollection, ...supportArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const support: ISupport = { id: 123 };
        const support2: ISupport = { id: 456 };
        expectedResult = service.addSupportToCollectionIfMissing([], support, support2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(support);
        expect(expectedResult).toContain(support2);
      });

      it('should accept null and undefined values', () => {
        const support: ISupport = { id: 123 };
        expectedResult = service.addSupportToCollectionIfMissing([], null, support, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(support);
      });

      it('should return initial array if no Support is added', () => {
        const supportCollection: ISupport[] = [{ id: 123 }];
        expectedResult = service.addSupportToCollectionIfMissing(supportCollection, undefined, null);
        expect(expectedResult).toEqual(supportCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
