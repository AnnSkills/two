import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISupport } from '../support.model';
import { SupportService } from '../service/support.service';

@Component({
  templateUrl: './support-delete-dialog.component.html',
})
export class SupportDeleteDialogComponent {
  support?: ISupport;

  constructor(protected supportService: SupportService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.supportService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
