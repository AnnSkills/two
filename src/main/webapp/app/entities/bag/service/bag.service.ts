import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBag, getBagIdentifier } from '../bag.model';

export type EntityResponseType = HttpResponse<IBag>;
export type EntityArrayResponseType = HttpResponse<IBag[]>;

@Injectable({ providedIn: 'root' })
export class BagService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/bags');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(bag: IBag): Observable<EntityResponseType> {
    return this.http.post<IBag>(this.resourceUrl, bag, { observe: 'response' });
  }

  update(bag: IBag): Observable<EntityResponseType> {
    return this.http.put<IBag>(`${this.resourceUrl}/${getBagIdentifier(bag) as number}`, bag, { observe: 'response' });
  }

  partialUpdate(bag: IBag): Observable<EntityResponseType> {
    return this.http.patch<IBag>(`${this.resourceUrl}/${getBagIdentifier(bag) as number}`, bag, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBag>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBag[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBagToCollectionIfMissing(bagCollection: IBag[], ...bagsToCheck: (IBag | null | undefined)[]): IBag[] {
    const bags: IBag[] = bagsToCheck.filter(isPresent);
    if (bags.length > 0) {
      const bagCollectionIdentifiers = bagCollection.map(bagItem => getBagIdentifier(bagItem)!);
      const bagsToAdd = bags.filter(bagItem => {
        const bagIdentifier = getBagIdentifier(bagItem);
        if (bagIdentifier == null || bagCollectionIdentifiers.includes(bagIdentifier)) {
          return false;
        }
        bagCollectionIdentifiers.push(bagIdentifier);
        return true;
      });
      return [...bagsToAdd, ...bagCollection];
    }
    return bagCollection;
  }
}
