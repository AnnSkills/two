import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IVersionFile, VersionFile } from '../version-file.model';
import { VersionFileService } from '../service/version-file.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IFile } from 'app/entities/file/file.model';
import { FileService } from 'app/entities/file/service/file.service';

@Component({
  selector: 'anna-version-file-update',
  templateUrl: './version-file-update.component.html',
})
export class VersionFileUpdateComponent implements OnInit {
  isSaving = false;

  filesSharedCollection: IFile[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    sourceCode: [],
    sourceCodeContentType: [],
    creationDate: [],
    file: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected versionFileService: VersionFileService,
    protected fileService: FileService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ versionFile }) => {
      if (versionFile.id === undefined) {
        const today = dayjs().startOf('day');
        versionFile.creationDate = today;
      }

      this.updateForm(versionFile);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('linterApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const versionFile = this.createFromForm();
    if (versionFile.id !== undefined) {
      this.subscribeToSaveResponse(this.versionFileService.update(versionFile));
    } else {
      this.subscribeToSaveResponse(this.versionFileService.create(versionFile));
    }
  }

  trackFileById(_index: number, item: IFile): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVersionFile>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(versionFile: IVersionFile): void {
    this.editForm.patchValue({
      id: versionFile.id,
      name: versionFile.name,
      sourceCode: versionFile.sourceCode,
      sourceCodeContentType: versionFile.sourceCodeContentType,
      creationDate: versionFile.creationDate ? versionFile.creationDate.format(DATE_TIME_FORMAT) : null,
      file: versionFile.file,
    });

    this.filesSharedCollection = this.fileService.addFileToCollectionIfMissing(this.filesSharedCollection, versionFile.file);
  }

  protected loadRelationshipsOptions(): void {
    this.fileService
      .query()
      .pipe(map((res: HttpResponse<IFile[]>) => res.body ?? []))
      .pipe(map((files: IFile[]) => this.fileService.addFileToCollectionIfMissing(files, this.editForm.get('file')!.value)))
      .subscribe((files: IFile[]) => (this.filesSharedCollection = files));
  }

  protected createFromForm(): IVersionFile {
    return {
      ...new VersionFile(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      sourceCodeContentType: this.editForm.get(['sourceCodeContentType'])!.value,
      sourceCode: this.editForm.get(['sourceCode'])!.value,
      creationDate: this.editForm.get(['creationDate'])!.value
        ? dayjs(this.editForm.get(['creationDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      file: this.editForm.get(['file'])!.value,
    };
  }
}
