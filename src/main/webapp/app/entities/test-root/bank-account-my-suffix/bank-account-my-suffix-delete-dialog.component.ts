import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IBankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';
import { BankAccountMySuffixService } from './bank-account-my-suffix.service';

@Component({
  selector: 'jhi-bank-account-my-suffix-delete-dialog',
  templateUrl: './bank-account-my-suffix-delete-dialog.component.html'
})
export class BankAccountMySuffixDeleteDialogComponent {
  bankAccount: IBankAccountMySuffix;

  constructor(
    protected bankAccountService: BankAccountMySuffixService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.bankAccountService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'bankAccountListModification',
        content: 'Deleted an bankAccount'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-bank-account-my-suffix-delete-popup',
  template: ''
})
export class BankAccountMySuffixDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ bankAccount }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(BankAccountMySuffixDeleteDialogComponent as Component, {
          size: 'lg',
          backdrop: 'static'
        });
        this.ngbModalRef.componentInstance.bankAccount = bankAccount;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/bank-account-my-suffix', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/bank-account-my-suffix', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
