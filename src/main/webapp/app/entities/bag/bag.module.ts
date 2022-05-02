import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BagComponent } from './list/bag.component';
import { BagDetailComponent } from './detail/bag-detail.component';
import { BagUpdateComponent } from './update/bag-update.component';
import { BagDeleteDialogComponent } from './delete/bag-delete-dialog.component';
import { BagRoutingModule } from './route/bag-routing.module';

@NgModule({
  imports: [SharedModule, BagRoutingModule],
  declarations: [BagComponent, BagDetailComponent, BagUpdateComponent, BagDeleteDialogComponent],
  entryComponents: [BagDeleteDialogComponent],
})
export class BagModule {}
