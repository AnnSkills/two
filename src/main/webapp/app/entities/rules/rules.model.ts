export interface IRules {
  id?: number;
  name?: string;
  code?: string | null;
  requirementsContentType?: string | null;
  requirements?: string | null;
}

export class Rules implements IRules {
  constructor(
    public id?: number,
    public name?: string,
    public code?: string | null,
    public requirementsContentType?: string | null,
    public requirements?: string | null
  ) {}
}

export function getRulesIdentifier(rules: IRules): number | undefined {
  return rules.id;
}
