import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiDataUtils } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';
import { BankAccountMySuffixService } from './bank-account-my-suffix.service';
import { BankAccountMySuffixDeleteDialogComponent } from './bank-account-my-suffix-delete-dialog.component';

@Component({
  selector: 'jhi-bank-account-my-suffix',
  templateUrl: './bank-account-my-suffix.component.html',
})
export class BankAccountMySuffixComponent implements OnInit, OnDestroy {
  bankAccounts?: IBankAccountMySuffix[];
  eventSubscriber?: Subscription;

  constructor(
    protected bankAccountService: BankAccountMySuffixService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.bankAccountService.query().subscribe((res: HttpResponse<IBankAccountMySuffix[]>) => (this.bankAccounts = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInBankAccounts();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IBankAccountMySuffix): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType = '', base64String: string): void {
    return this.dataUtils.openFile(contentType, base64String);
  }

  registerChangeInBankAccounts(): void {
    this.eventSubscriber = this.eventManager.subscribe('bankAccountListModification', () => this.loadAll());
  }

  delete(bankAccount: IBankAccountMySuffix): void {
    const modalRef = this.modalService.open(BankAccountMySuffixDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.bankAccount = bankAccount;
  }
}
