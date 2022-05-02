import { ITask } from 'app/entities/task/task.model';
import { IUser } from 'app/entities/user/user.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IBag {
  id?: number;
  bagName?: string;
  description?: string | null;
  status?: Status | null;
  tasks?: ITask[] | null;
  user?: IUser | null;
}

export class Bag implements IBag {
  constructor(
    public id?: number,
    public bagName?: string,
    public description?: string | null,
    public status?: Status | null,
    public tasks?: ITask[] | null,
    public user?: IUser | null
  ) {}
}

export function getBagIdentifier(bag: IBag): number | undefined {
  return bag.id;
}
