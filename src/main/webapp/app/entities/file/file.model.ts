import dayjs from 'dayjs/esm';
import { IVersionFile } from 'app/entities/version-file/version-file.model';
import { IUser } from 'app/entities/user/user.model';

export interface IFile {
  id?: number;
  name?: string;
  sourceCodeContentType?: string | null;
  sourceCode?: string | null;
  creationDate?: dayjs.Dayjs | null;
  versionFiles?: IVersionFile[] | null;
  user?: IUser | null;
}

export class File implements IFile {
  constructor(
    public id?: number,
    public name?: string,
    public sourceCodeContentType?: string | null,
    public sourceCode?: string | null,
    public creationDate?: dayjs.Dayjs | null,
    public versionFiles?: IVersionFile[] | null,
    public user?: IUser | null
  ) {}
}

export function getFileIdentifier(file: IFile): number | undefined {
  return file.id;
}
