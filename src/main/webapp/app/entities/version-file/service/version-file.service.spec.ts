import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IVersionFile, VersionFile } from '../version-file.model';

import { VersionFileService } from './version-file.service';

describe('VersionFile Service', () => {
  let service: VersionFileService;
  let httpMock: HttpTestingController;
  let elemDefault: IVersionFile;
  let expectedResult: IVersionFile | IVersionFile[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(VersionFileService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      sourceCodeContentType: 'image/png',
      sourceCode: 'AAAAAAA',
      creationDate: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          creationDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a VersionFile', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          creationDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          creationDate: currentDate,
        },
        returnedFromService
      );

      service.create(new VersionFile()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a VersionFile', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          sourceCode: 'BBBBBB',
          creationDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          creationDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a VersionFile', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          creationDate: currentDate.format(DATE_TIME_FORMAT),
        },
        new VersionFile()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          creationDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of VersionFile', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          sourceCode: 'BBBBBB',
          creationDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          creationDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a VersionFile', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addVersionFileToCollectionIfMissing', () => {
      it('should add a VersionFile to an empty array', () => {
        const versionFile: IVersionFile = { id: 123 };
        expectedResult = service.addVersionFileToCollectionIfMissing([], versionFile);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(versionFile);
      });

      it('should not add a VersionFile to an array that contains it', () => {
        const versionFile: IVersionFile = { id: 123 };
        const versionFileCollection: IVersionFile[] = [
          {
            ...versionFile,
          },
          { id: 456 },
        ];
        expectedResult = service.addVersionFileToCollectionIfMissing(versionFileCollection, versionFile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a VersionFile to an array that doesn't contain it", () => {
        const versionFile: IVersionFile = { id: 123 };
        const versionFileCollection: IVersionFile[] = [{ id: 456 }];
        expectedResult = service.addVersionFileToCollectionIfMissing(versionFileCollection, versionFile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(versionFile);
      });

      it('should add only unique VersionFile to an array', () => {
        const versionFileArray: IVersionFile[] = [{ id: 123 }, { id: 456 }, { id: 22581 }];
        const versionFileCollection: IVersionFile[] = [{ id: 123 }];
        expectedResult = service.addVersionFileToCollectionIfMissing(versionFileCollection, ...versionFileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const versionFile: IVersionFile = { id: 123 };
        const versionFile2: IVersionFile = { id: 456 };
        expectedResult = service.addVersionFileToCollectionIfMissing([], versionFile, versionFile2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(versionFile);
        expect(expectedResult).toContain(versionFile2);
      });

      it('should accept null and undefined values', () => {
        const versionFile: IVersionFile = { id: 123 };
        expectedResult = service.addVersionFileToCollectionIfMissing([], null, versionFile, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(versionFile);
      });

      it('should return initial array if no VersionFile is added', () => {
        const versionFileCollection: IVersionFile[] = [{ id: 123 }];
        expectedResult = service.addVersionFileToCollectionIfMissing(versionFileCollection, undefined, null);
        expect(expectedResult).toEqual(versionFileCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
