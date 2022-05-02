import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SupportComponent } from './list/support.component';
import { SupportDetailComponent } from './detail/support-detail.component';
import { SupportUpdateComponent } from './update/support-update.component';
import { SupportDeleteDialogComponent } from './delete/support-delete-dialog.component';
import { SupportRoutingModule } from './route/support-routing.module';

@NgModule({
  imports: [SharedModule, SupportRoutingModule],
  declarations: [SupportComponent, SupportDetailComponent, SupportUpdateComponent, SupportDeleteDialogComponent],
  entryComponents: [SupportDeleteDialogComponent],
})
export class SupportModule {}
