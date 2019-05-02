import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { IBankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';
import { AccountService } from 'app/core';
import { BankAccountMySuffixService } from './bank-account-my-suffix.service';

@Component({
  selector: 'jhi-bank-account-my-suffix',
  templateUrl: './bank-account-my-suffix.component.html'
})
export class BankAccountMySuffixComponent implements OnInit, OnDestroy {
  bankAccounts: IBankAccountMySuffix[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected bankAccountService: BankAccountMySuffixService,
    protected jhiAlertService: JhiAlertService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.bankAccountService
      .query()
      .pipe(
        filter((res: HttpResponse<IBankAccountMySuffix[]>) => res.ok),
        map((res: HttpResponse<IBankAccountMySuffix[]>) => res.body)
      )
      .subscribe(
        (res: IBankAccountMySuffix[]) => {
          this.bankAccounts = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInBankAccounts();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IBankAccountMySuffix) {
    return item.id;
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  registerChangeInBankAccounts() {
    this.eventSubscriber = this.eventManager.subscribe('bankAccountListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
