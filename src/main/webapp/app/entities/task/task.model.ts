import dayjs from 'dayjs/esm';
import { IBag } from 'app/entities/bag/bag.model';

export interface ITask {
  id?: number;
  taskName?: string;
  description?: string | null;
  deadline?: dayjs.Dayjs | null;
  bag?: IBag | null;
}

export class Task implements ITask {
  constructor(
    public id?: number,
    public taskName?: string,
    public description?: string | null,
    public deadline?: dayjs.Dayjs | null,
    public bag?: IBag | null
  ) {}
}

export function getTaskIdentifier(task: ITask): number | undefined {
  return task.id;
}
