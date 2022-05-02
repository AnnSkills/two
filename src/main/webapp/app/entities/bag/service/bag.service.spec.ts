import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Status } from 'app/entities/enumerations/status.model';
import { IBag, Bag } from '../bag.model';

import { BagService } from './bag.service';

describe('Bag Service', () => {
  let service: BagService;
  let httpMock: HttpTestingController;
  let elemDefault: IBag;
  let expectedResult: IBag | IBag[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BagService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      bagName: 'AAAAAAA',
      description: 'AAAAAAA',
      status: Status.OPEN,
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

    it('should create a Bag', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Bag()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Bag', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          bagName: 'BBBBBB',
          description: 'BBBBBB',
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Bag', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
        },
        new Bag()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Bag', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          bagName: 'BBBBBB',
          description: 'BBBBBB',
          status: 'BBBBBB',
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

    it('should delete a Bag', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addBagToCollectionIfMissing', () => {
      it('should add a Bag to an empty array', () => {
        const bag: IBag = { id: 123 };
        expectedResult = service.addBagToCollectionIfMissing([], bag);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bag);
      });

      it('should not add a Bag to an array that contains it', () => {
        const bag: IBag = { id: 123 };
        const bagCollection: IBag[] = [
          {
            ...bag,
          },
          { id: 456 },
        ];
        expectedResult = service.addBagToCollectionIfMissing(bagCollection, bag);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Bag to an array that doesn't contain it", () => {
        const bag: IBag = { id: 123 };
        const bagCollection: IBag[] = [{ id: 456 }];
        expectedResult = service.addBagToCollectionIfMissing(bagCollection, bag);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bag);
      });

      it('should add only unique Bag to an array', () => {
        const bagArray: IBag[] = [{ id: 123 }, { id: 456 }, { id: 67925 }];
        const bagCollection: IBag[] = [{ id: 123 }];
        expectedResult = service.addBagToCollectionIfMissing(bagCollection, ...bagArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bag: IBag = { id: 123 };
        const bag2: IBag = { id: 456 };
        expectedResult = service.addBagToCollectionIfMissing([], bag, bag2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bag);
        expect(expectedResult).toContain(bag2);
      });

      it('should accept null and undefined values', () => {
        const bag: IBag = { id: 123 };
        expectedResult = service.addBagToCollectionIfMissing([], null, bag, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bag);
      });

      it('should return initial array if no Bag is added', () => {
        const bagCollection: IBag[] = [{ id: 123 }];
        expectedResult = service.addBagToCollectionIfMissing(bagCollection, undefined, null);
        expect(expectedResult).toEqual(bagCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
