import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVersionFile } from '../version-file.model';
import { VersionFileService } from '../service/version-file.service';

@Component({
  templateUrl: './version-file-delete-dialog.component.html',
})
export class VersionFileDeleteDialogComponent {
  versionFile?: IVersionFile;

  constructor(protected versionFileService: VersionFileService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.versionFileService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
