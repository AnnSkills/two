import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVersionFile, getVersionFileIdentifier } from '../version-file.model';

export type EntityResponseType = HttpResponse<IVersionFile>;
export type EntityArrayResponseType = HttpResponse<IVersionFile[]>;

@Injectable({ providedIn: 'root' })
export class VersionFileService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/version-files');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(versionFile: IVersionFile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(versionFile);
    return this.http
      .post<IVersionFile>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(versionFile: IVersionFile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(versionFile);
    return this.http
      .put<IVersionFile>(`${this.resourceUrl}/${getVersionFileIdentifier(versionFile) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(versionFile: IVersionFile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(versionFile);
    return this.http
      .patch<IVersionFile>(`${this.resourceUrl}/${getVersionFileIdentifier(versionFile) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IVersionFile>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVersionFile[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addVersionFileToCollectionIfMissing(
    versionFileCollection: IVersionFile[],
    ...versionFilesToCheck: (IVersionFile | null | undefined)[]
  ): IVersionFile[] {
    const versionFiles: IVersionFile[] = versionFilesToCheck.filter(isPresent);
    if (versionFiles.length > 0) {
      const versionFileCollectionIdentifiers = versionFileCollection.map(versionFileItem => getVersionFileIdentifier(versionFileItem)!);
      const versionFilesToAdd = versionFiles.filter(versionFileItem => {
        const versionFileIdentifier = getVersionFileIdentifier(versionFileItem);
        if (versionFileIdentifier == null || versionFileCollectionIdentifiers.includes(versionFileIdentifier)) {
          return false;
        }
        versionFileCollectionIdentifiers.push(versionFileIdentifier);
        return true;
      });
      return [...versionFilesToAdd, ...versionFileCollection];
    }
    return versionFileCollection;
  }

  protected convertDateFromClient(versionFile: IVersionFile): IVersionFile {
    return Object.assign({}, versionFile, {
      creationDate: versionFile.creationDate?.isValid() ? versionFile.creationDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.creationDate = res.body.creationDate ? dayjs(res.body.creationDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((versionFile: IVersionFile) => {
        versionFile.creationDate = versionFile.creationDate ? dayjs(versionFile.creationDate) : undefined;
      });
    }
    return res;
  }
}
