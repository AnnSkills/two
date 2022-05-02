import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBag } from '../bag.model';
import { BagService } from '../service/bag.service';
import { BagDeleteDialogComponent } from '../delete/bag-delete-dialog.component';

@Component({
  selector: 'anna-bag',
  templateUrl: './bag.component.html',
})
export class BagComponent implements OnInit {
  bags?: IBag[];
  isLoading = false;

  constructor(protected bagService: BagService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.bagService.query().subscribe({
      next: (res: HttpResponse<IBag[]>) => {
        this.isLoading = false;
        this.bags = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IBag): number {
    return item.id!;
  }

  delete(bag: IBag): void {
    const modalRef = this.modalService.open(BagDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.bag = bag;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
