import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBag } from '../bag.model';
import { BagService } from '../service/bag.service';

@Component({
  templateUrl: './bag-delete-dialog.component.html',
})
export class BagDeleteDialogComponent {
  bag?: IBag;

  constructor(protected bagService: BagService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bagService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
