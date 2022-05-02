import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { VersionFileComponent } from './list/version-file.component';
import { VersionFileDetailComponent } from './detail/version-file-detail.component';
import { VersionFileUpdateComponent } from './update/version-file-update.component';
import { VersionFileDeleteDialogComponent } from './delete/version-file-delete-dialog.component';
import { VersionFileRoutingModule } from './route/version-file-routing.module';

@NgModule({
  imports: [SharedModule, VersionFileRoutingModule],
  declarations: [VersionFileComponent, VersionFileDetailComponent, VersionFileUpdateComponent, VersionFileDeleteDialogComponent],
  entryComponents: [VersionFileDeleteDialogComponent],
})
export class VersionFileModule {}
