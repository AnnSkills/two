import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRules, getRulesIdentifier } from '../rules.model';

export type EntityResponseType = HttpResponse<IRules>;
export type EntityArrayResponseType = HttpResponse<IRules[]>;

@Injectable({ providedIn: 'root' })
export class RulesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/rules');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(rules: IRules): Observable<EntityResponseType> {
    return this.http.post<IRules>(this.resourceUrl, rules, { observe: 'response' });
  }

  update(rules: IRules): Observable<EntityResponseType> {
    return this.http.put<IRules>(`${this.resourceUrl}/${getRulesIdentifier(rules) as number}`, rules, { observe: 'response' });
  }

  partialUpdate(rules: IRules): Observable<EntityResponseType> {
    return this.http.patch<IRules>(`${this.resourceUrl}/${getRulesIdentifier(rules) as number}`, rules, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRules>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRules[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRulesToCollectionIfMissing(rulesCollection: IRules[], ...rulesToCheck: (IRules | null | undefined)[]): IRules[] {
    const rules: IRules[] = rulesToCheck.filter(isPresent);
    if (rules.length > 0) {
      const rulesCollectionIdentifiers = rulesCollection.map(rulesItem => getRulesIdentifier(rulesItem)!);
      const rulesToAdd = rules.filter(rulesItem => {
        const rulesIdentifier = getRulesIdentifier(rulesItem);
        if (rulesIdentifier == null || rulesCollectionIdentifiers.includes(rulesIdentifier)) {
          return false;
        }
        rulesCollectionIdentifiers.push(rulesIdentifier);
        return true;
      });
      return [...rulesToAdd, ...rulesCollection];
    }
    return rulesCollection;
  }
}
