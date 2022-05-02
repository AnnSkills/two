import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRules } from '../rules.model';
import { RulesService } from '../service/rules.service';

@Component({
  templateUrl: './rules-delete-dialog.component.html',
})
export class RulesDeleteDialogComponent {
  rules?: IRules;

  constructor(protected rulesService: RulesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.rulesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
