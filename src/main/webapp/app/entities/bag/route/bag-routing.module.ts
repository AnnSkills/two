import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BagComponent } from '../list/bag.component';
import { BagDetailComponent } from '../detail/bag-detail.component';
import { BagUpdateComponent } from '../update/bag-update.component';
import { BagRoutingResolveService } from './bag-routing-resolve.service';

const bagRoute: Routes = [
  {
    path: '',
    component: BagComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BagDetailComponent,
    resolve: {
      bag: BagRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BagUpdateComponent,
    resolve: {
      bag: BagRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BagUpdateComponent,
    resolve: {
      bag: BagRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(bagRoute)],
  exports: [RouterModule],
})
export class BagRoutingModule {}
