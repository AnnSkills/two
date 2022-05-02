import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITask, Task } from '../task.model';
import { TaskService } from '../service/task.service';
import { IBag } from 'app/entities/bag/bag.model';
import { BagService } from 'app/entities/bag/service/bag.service';

@Component({
  selector: 'anna-task-update',
  templateUrl: './task-update.component.html',
})
export class TaskUpdateComponent implements OnInit {
  isSaving = false;

  bagsSharedCollection: IBag[] = [];

  editForm = this.fb.group({
    id: [],
    taskName: [null, [Validators.required]],
    description: [],
    deadline: [],
    bag: [],
  });

  constructor(
    protected taskService: TaskService,
    protected bagService: BagService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ task }) => {
      this.updateForm(task);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const task = this.createFromForm();
    if (task.id !== undefined) {
      this.subscribeToSaveResponse(this.taskService.update(task));
    } else {
      this.subscribeToSaveResponse(this.taskService.create(task));
    }
  }

  trackBagById(_index: number, item: IBag): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITask>>): void {
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

  protected updateForm(task: ITask): void {
    this.editForm.patchValue({
      id: task.id,
      taskName: task.taskName,
      description: task.description,
      deadline: task.deadline,
      bag: task.bag,
    });

    this.bagsSharedCollection = this.bagService.addBagToCollectionIfMissing(this.bagsSharedCollection, task.bag);
  }

  protected loadRelationshipsOptions(): void {
    this.bagService
      .query()
      .pipe(map((res: HttpResponse<IBag[]>) => res.body ?? []))
      .pipe(map((bags: IBag[]) => this.bagService.addBagToCollectionIfMissing(bags, this.editForm.get('bag')!.value)))
      .subscribe((bags: IBag[]) => (this.bagsSharedCollection = bags));
  }

  protected createFromForm(): ITask {
    return {
      ...new Task(),
      id: this.editForm.get(['id'])!.value,
      taskName: this.editForm.get(['taskName'])!.value,
      description: this.editForm.get(['description'])!.value,
      deadline: this.editForm.get(['deadline'])!.value,
      bag: this.editForm.get(['bag'])!.value,
    };
  }
}
