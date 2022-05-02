import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISupport, Support } from '../support.model';
import { SupportService } from '../service/support.service';

@Injectable({ providedIn: 'root' })
export class SupportRoutingResolveService implements Resolve<ISupport> {
  constructor(protected service: SupportService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISupport> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((support: HttpResponse<Support>) => {
          if (support.body) {
            return of(support.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Support());
  }
}
