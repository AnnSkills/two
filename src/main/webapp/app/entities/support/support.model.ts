import { IUser } from 'app/entities/user/user.model';

export interface ISupport {
  id?: number;
  topic?: string | null;
  email?: string;
  phone?: string;
  description?: string | null;
  user?: IUser | null;
}

export class Support implements ISupport {
  constructor(
    public id?: number,
    public topic?: string | null,
    public email?: string,
    public phone?: string,
    public description?: string | null,
    public user?: IUser | null
  ) {}
}

export function getSupportIdentifier(support: ISupport): number | undefined {
  return support.id;
}
