import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VersionFileService } from '../service/version-file.service';
import { IVersionFile, VersionFile } from '../version-file.model';
import { IFile } from 'app/entities/file/file.model';
import { FileService } from 'app/entities/file/service/file.service';

import { VersionFileUpdateComponent } from './version-file-update.component';

describe('VersionFile Management Update Component', () => {
  let comp: VersionFileUpdateComponent;
  let fixture: ComponentFixture<VersionFileUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let versionFileService: VersionFileService;
  let fileService: FileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VersionFileUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(VersionFileUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VersionFileUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    versionFileService = TestBed.inject(VersionFileService);
    fileService = TestBed.inject(FileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call File query and add missing value', () => {
      const versionFile: IVersionFile = { id: 456 };
      const file: IFile = { id: 11459 };
      versionFile.file = file;

      const fileCollection: IFile[] = [{ id: 7129 }];
      jest.spyOn(fileService, 'query').mockReturnValue(of(new HttpResponse({ body: fileCollection })));
      const additionalFiles = [file];
      const expectedCollection: IFile[] = [...additionalFiles, ...fileCollection];
      jest.spyOn(fileService, 'addFileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ versionFile });
      comp.ngOnInit();

      expect(fileService.query).toHaveBeenCalled();
      expect(fileService.addFileToCollectionIfMissing).toHaveBeenCalledWith(fileCollection, ...additionalFiles);
      expect(comp.filesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const versionFile: IVersionFile = { id: 456 };
      const file: IFile = { id: 49569 };
      versionFile.file = file;

      activatedRoute.data = of({ versionFile });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(versionFile));
      expect(comp.filesSharedCollection).toContain(file);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<VersionFile>>();
      const versionFile = { id: 123 };
      jest.spyOn(versionFileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ versionFile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: versionFile }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(versionFileService.update).toHaveBeenCalledWith(versionFile);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<VersionFile>>();
      const versionFile = new VersionFile();
      jest.spyOn(versionFileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ versionFile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: versionFile }));
      saveSubject.complete();

      // THEN
      expect(versionFileService.create).toHaveBeenCalledWith(versionFile);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<VersionFile>>();
      const versionFile = { id: 123 };
      jest.spyOn(versionFileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ versionFile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(versionFileService.update).toHaveBeenCalledWith(versionFile);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackFileById', () => {
      it('Should return tracked File primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackFileById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
