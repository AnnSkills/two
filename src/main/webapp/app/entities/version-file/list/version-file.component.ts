import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IVersionFile } from '../version-file.model';
import { VersionFileService } from '../service/version-file.service';
import { VersionFileDeleteDialogComponent } from '../delete/version-file-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'anna-version-file',
  templateUrl: './version-file.component.html',
})
export class VersionFileComponent implements OnInit {
  versionFiles?: IVersionFile[];
  isLoading = false;

  constructor(protected versionFileService: VersionFileService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.versionFileService.query().subscribe({
      next: (res: HttpResponse<IVersionFile[]>) => {
        this.isLoading = false;
        this.versionFiles = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IVersionFile): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(versionFile: IVersionFile): void {
    const modalRef = this.modalService.open(VersionFileDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.versionFile = versionFile;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
