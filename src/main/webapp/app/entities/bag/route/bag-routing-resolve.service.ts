import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBag, Bag } from '../bag.model';
import { BagService } from '../service/bag.service';

@Injectable({ providedIn: 'root' })
export class BagRoutingResolveService implements Resolve<IBag> {
  constructor(protected service: BagService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBag> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((bag: HttpResponse<Bag>) => {
          if (bag.body) {
            return of(bag.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Bag());
  }
}
