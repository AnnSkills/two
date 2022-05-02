import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SupportComponent } from '../list/support.component';
import { SupportDetailComponent } from '../detail/support-detail.component';
import { SupportUpdateComponent } from '../update/support-update.component';
import { SupportRoutingResolveService } from './support-routing-resolve.service';

const supportRoute: Routes = [
  {
    path: '',
    component: SupportComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SupportDetailComponent,
    resolve: {
      support: SupportRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SupportUpdateComponent,
    resolve: {
      support: SupportRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SupportUpdateComponent,
    resolve: {
      support: SupportRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(supportRoute)],
  exports: [RouterModule],
})
export class SupportRoutingModule {}
