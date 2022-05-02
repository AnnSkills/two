import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IPost {
  id?: number;
  title?: string;
  content?: string;
  pictureContentType?: string | null;
  picture?: string | null;
  date?: dayjs.Dayjs;
  user?: IUser | null;
}

export class Post implements IPost {
  constructor(
    public id?: number,
    public title?: string,
    public content?: string,
    public pictureContentType?: string | null,
    public picture?: string | null,
    public date?: dayjs.Dayjs,
    public user?: IUser | null
  ) {}
}

export function getPostIdentifier(post: IPost): number | undefined {
  return post.id;
}
