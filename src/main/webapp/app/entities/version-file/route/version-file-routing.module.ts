import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { VersionFileComponent } from '../list/version-file.component';
import { VersionFileDetailComponent } from '../detail/version-file-detail.component';
import { VersionFileUpdateComponent } from '../update/version-file-update.component';
import { VersionFileRoutingResolveService } from './version-file-routing-resolve.service';

const versionFileRoute: Routes = [
  {
    path: '',
    component: VersionFileComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VersionFileDetailComponent,
    resolve: {
      versionFile: VersionFileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VersionFileUpdateComponent,
    resolve: {
      versionFile: VersionFileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VersionFileUpdateComponent,
    resolve: {
      versionFile: VersionFileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(versionFileRoute)],
  exports: [RouterModule],
})
export class VersionFileRoutingModule {}
