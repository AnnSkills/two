import dayjs from 'dayjs/esm';
import { IFile } from 'app/entities/file/file.model';

export interface IVersionFile {
  id?: number;
  name?: string;
  sourceCodeContentType?: string | null;
  sourceCode?: string | null;
  creationDate?: dayjs.Dayjs | null;
  file?: IFile | null;
}

export class VersionFile implements IVersionFile {
  constructor(
    public id?: number,
    public name?: string,
    public sourceCodeContentType?: string | null,
    public sourceCode?: string | null,
    public creationDate?: dayjs.Dayjs | null,
    public file?: IFile | null
  ) {}
}

export function getVersionFileIdentifier(versionFile: IVersionFile): number | undefined {
  return versionFile.id;
}
