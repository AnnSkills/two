import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISupport } from '../support.model';
import { SupportService } from '../service/support.service';
import { SupportDeleteDialogComponent } from '../delete/support-delete-dialog.component';

@Component({
  selector: 'anna-support',
  templateUrl: './support.component.html',
})
export class SupportComponent implements OnInit {
  supports?: ISupport[];
  isLoading = false;

  constructor(protected supportService: SupportService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.supportService.query().subscribe({
      next: (res: HttpResponse<ISupport[]>) => {
        this.isLoading = false;
        this.supports = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ISupport): number {
    return item.id!;
  }

  delete(support: ISupport): void {
    const modalRef = this.modalService.open(SupportDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.support = support;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
