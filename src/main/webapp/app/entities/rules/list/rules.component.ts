import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRules } from '../rules.model';
import { RulesService } from '../service/rules.service';
import { RulesDeleteDialogComponent } from '../delete/rules-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'anna-rules',
  templateUrl: './rules.component.html',
})
export class RulesComponent implements OnInit {
  rules?: IRules[];
  isLoading = false;

  constructor(protected rulesService: RulesService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.rulesService.query().subscribe({
      next: (res: HttpResponse<IRules[]>) => {
        this.isLoading = false;
        this.rules = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IRules): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(rules: IRules): void {
    const modalRef = this.modalService.open(RulesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.rules = rules;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
