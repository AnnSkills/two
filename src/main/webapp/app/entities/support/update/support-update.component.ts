import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISupport, Support } from '../support.model';
import { SupportService } from '../service/support.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'anna-support-update',
  templateUrl: './support-update.component.html',
})
export class SupportUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    topic: [],
    email: [null, [Validators.required, Validators.pattern('^(.+)@(\\\\S+)$')]],
    phone: [null, [Validators.required, Validators.pattern('^\\\\+(?:[0-9] ?){6,14}[0-9]$')]],
    description: [],
    user: [],
  });

  constructor(
    protected supportService: SupportService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ support }) => {
      this.updateForm(support);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const support = this.createFromForm();
    if (support.id !== undefined) {
      this.subscribeToSaveResponse(this.supportService.update(support));
    } else {
      this.subscribeToSaveResponse(this.supportService.create(support));
    }
  }

  trackUserById(_index: number, item: IUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISupport>>): void {
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

  protected updateForm(support: ISupport): void {
    this.editForm.patchValue({
      id: support.id,
      topic: support.topic,
      email: support.email,
      phone: support.phone,
      description: support.description,
      user: support.user,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, support.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): ISupport {
    return {
      ...new Support(),
      id: this.editForm.get(['id'])!.value,
      topic: this.editForm.get(['topic'])!.value,
      email: this.editForm.get(['email'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      description: this.editForm.get(['description'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }
}
