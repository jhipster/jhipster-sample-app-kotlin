import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IBankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';

@Component({
  selector: 'jhi-bank-account-my-suffix-detail',
  templateUrl: './bank-account-my-suffix-detail.component.html',
})
export class BankAccountMySuffixDetailComponent implements OnInit {
  bankAccount: IBankAccountMySuffix | null = null;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bankAccount }) => (this.bankAccount = bankAccount));
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType = '', base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  previousState(): void {
    window.history.back();
  }
}
