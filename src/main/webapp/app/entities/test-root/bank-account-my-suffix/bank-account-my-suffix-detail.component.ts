import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IBankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';

@Component({
  selector: 'jhi-bank-account-my-suffix-detail',
  templateUrl: './bank-account-my-suffix-detail.component.html'
})
export class BankAccountMySuffixDetailComponent implements OnInit {
  bankAccount: IBankAccountMySuffix;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ bankAccount }) => {
      this.bankAccount = bankAccount;
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }
  previousState() {
    window.history.back();
  }
}
