import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IRules, Rules } from '../rules.model';
import { RulesService } from '../service/rules.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'anna-rules-update',
  templateUrl: './rules-update.component.html',
})
export class RulesUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    code: [],
    requirements: [],
    requirementsContentType: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected rulesService: RulesService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rules }) => {
      this.updateForm(rules);
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
    const rules = this.createFromForm();
    if (rules.id !== undefined) {
      this.subscribeToSaveResponse(this.rulesService.update(rules));
    } else {
      this.subscribeToSaveResponse(this.rulesService.create(rules));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRules>>): void {
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

  protected updateForm(rules: IRules): void {
    this.editForm.patchValue({
      id: rules.id,
      name: rules.name,
      code: rules.code,
      requirements: rules.requirements,
      requirementsContentType: rules.requirementsContentType,
    });
  }

  protected createFromForm(): IRules {
    return {
      ...new Rules(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      code: this.editForm.get(['code'])!.value,
      requirementsContentType: this.editForm.get(['requirementsContentType'])!.value,
      requirements: this.editForm.get(['requirements'])!.value,
    };
  }
}
