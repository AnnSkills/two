import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IVersionFile } from '../version-file.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'anna-version-file-detail',
  templateUrl: './version-file-detail.component.html',
})
export class VersionFileDetailComponent implements OnInit {
  versionFile: IVersionFile | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ versionFile }) => {
      this.versionFile = versionFile;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
