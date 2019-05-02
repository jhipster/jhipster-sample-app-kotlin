import { Moment } from 'moment';
import { IBankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';
import { ILabel } from 'app/shared/model/test-root/label.model';

export interface IOperation {
  id?: number;
  date?: Moment;
  description?: string;
  amount?: number;
  bankAccount?: IBankAccountMySuffix;
  labels?: ILabel[];
}

export class Operation implements IOperation {
  constructor(
    public id?: number,
    public date?: Moment,
    public description?: string,
    public amount?: number,
    public bankAccount?: IBankAccountMySuffix,
    public labels?: ILabel[]
  ) {}
}
