import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IBankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';

type EntityResponseType = HttpResponse<IBankAccountMySuffix>;
type EntityArrayResponseType = HttpResponse<IBankAccountMySuffix[]>;

@Injectable({ providedIn: 'root' })
export class BankAccountMySuffixService {
  public resourceUrl = SERVER_API_URL + 'api/bank-accounts';

  constructor(protected http: HttpClient) {}

  create(bankAccount: IBankAccountMySuffix): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bankAccount);
    return this.http
      .post<IBankAccountMySuffix>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(bankAccount: IBankAccountMySuffix): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bankAccount);
    return this.http
      .put<IBankAccountMySuffix>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBankAccountMySuffix>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBankAccountMySuffix[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(bankAccount: IBankAccountMySuffix): IBankAccountMySuffix {
    const copy: IBankAccountMySuffix = Object.assign({}, bankAccount, {
      openingDay: bankAccount.openingDay && bankAccount.openingDay.isValid() ? bankAccount.openingDay.format(DATE_FORMAT) : undefined,
      lastOperationDate:
        bankAccount.lastOperationDate && bankAccount.lastOperationDate.isValid() ? bankAccount.lastOperationDate.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.openingDay = res.body.openingDay ? moment(res.body.openingDay) : undefined;
      res.body.lastOperationDate = res.body.lastOperationDate ? moment(res.body.lastOperationDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((bankAccount: IBankAccountMySuffix) => {
        bankAccount.openingDay = bankAccount.openingDay ? moment(bankAccount.openingDay) : undefined;
        bankAccount.lastOperationDate = bankAccount.lastOperationDate ? moment(bankAccount.lastOperationDate) : undefined;
      });
    }
    return res;
  }
}
