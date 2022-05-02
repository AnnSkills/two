import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVersionFile, VersionFile } from '../version-file.model';
import { VersionFileService } from '../service/version-file.service';

@Injectable({ providedIn: 'root' })
export class VersionFileRoutingResolveService implements Resolve<IVersionFile> {
  constructor(protected service: VersionFileService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVersionFile> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((versionFile: HttpResponse<VersionFile>) => {
          if (versionFile.body) {
            return of(versionFile.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new VersionFile());
  }
}
