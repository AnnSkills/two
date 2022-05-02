import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISupport, getSupportIdentifier } from '../support.model';

export type EntityResponseType = HttpResponse<ISupport>;
export type EntityArrayResponseType = HttpResponse<ISupport[]>;

@Injectable({ providedIn: 'root' })
export class SupportService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/supports');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(support: ISupport): Observable<EntityResponseType> {
    return this.http.post<ISupport>(this.resourceUrl, support, { observe: 'response' });
  }

  update(support: ISupport): Observable<EntityResponseType> {
    return this.http.put<ISupport>(`${this.resourceUrl}/${getSupportIdentifier(support) as number}`, support, { observe: 'response' });
  }

  partialUpdate(support: ISupport): Observable<EntityResponseType> {
    return this.http.patch<ISupport>(`${this.resourceUrl}/${getSupportIdentifier(support) as number}`, support, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISupport>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISupport[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSupportToCollectionIfMissing(supportCollection: ISupport[], ...supportsToCheck: (ISupport | null | undefined)[]): ISupport[] {
    const supports: ISupport[] = supportsToCheck.filter(isPresent);
    if (supports.length > 0) {
      const supportCollectionIdentifiers = supportCollection.map(supportItem => getSupportIdentifier(supportItem)!);
      const supportsToAdd = supports.filter(supportItem => {
        const supportIdentifier = getSupportIdentifier(supportItem);
        if (supportIdentifier == null || supportCollectionIdentifiers.includes(supportIdentifier)) {
          return false;
        }
        supportCollectionIdentifiers.push(supportIdentifier);
        return true;
      });
      return [...supportsToAdd, ...supportCollection];
    }
    return supportCollection;
  }
}
