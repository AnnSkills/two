import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBag, Bag } from '../bag.model';
import { BagService } from '../service/bag.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  selector: 'anna-bag-update',
  templateUrl: './bag-update.component.html',
})
export class BagUpdateComponent implements OnInit {
  isSaving = false;
  statusValues = Object.keys(Status);

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    bagName: [null, [Validators.required]],
    description: [],
    status: [],
    user: [],
  });

  constructor(
    protected bagService: BagService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bag }) => {
      this.updateForm(bag);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bag = this.createFromForm();
    if (bag.id !== undefined) {
      this.subscribeToSaveResponse(this.bagService.update(bag));
    } else {
      this.subscribeToSaveResponse(this.bagService.create(bag));
    }
  }

  trackUserById(_index: number, item: IUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBag>>): void {
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

  protected updateForm(bag: IBag): void {
    this.editForm.patchValue({
      id: bag.id,
      bagName: bag.bagName,
      description: bag.description,
      status: bag.status,
      user: bag.user,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, bag.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IBag {
    return {
      ...new Bag(),
      id: this.editForm.get(['id'])!.value,
      bagName: this.editForm.get(['bagName'])!.value,
      description: this.editForm.get(['description'])!.value,
      status: this.editForm.get(['status'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }
}
