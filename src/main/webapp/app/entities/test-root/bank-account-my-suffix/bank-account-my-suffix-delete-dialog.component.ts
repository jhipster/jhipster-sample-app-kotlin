import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IBankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';
import { BankAccountMySuffixService } from './bank-account-my-suffix.service';

@Component({
  templateUrl: './bank-account-my-suffix-delete-dialog.component.html',
})
export class BankAccountMySuffixDeleteDialogComponent {
  bankAccount?: IBankAccountMySuffix;

  constructor(
    protected bankAccountService: BankAccountMySuffixService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bankAccountService.delete(id).subscribe(() => {
      this.eventManager.broadcast('bankAccountListModification');
      this.activeModal.close();
    });
  }
}
