import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JhipsterSharedModule } from 'app/shared/shared.module';
import { BankAccountMySuffixComponent } from './bank-account-my-suffix.component';
import { BankAccountMySuffixDetailComponent } from './bank-account-my-suffix-detail.component';
import { BankAccountMySuffixUpdateComponent } from './bank-account-my-suffix-update.component';
import {
  BankAccountMySuffixDeletePopupComponent,
  BankAccountMySuffixDeleteDialogComponent
} from './bank-account-my-suffix-delete-dialog.component';
import { bankAccountRoute, bankAccountPopupRoute } from './bank-account-my-suffix.route';

const ENTITY_STATES = [...bankAccountRoute, ...bankAccountPopupRoute];

@NgModule({
  imports: [JhipsterSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    BankAccountMySuffixComponent,
    BankAccountMySuffixDetailComponent,
    BankAccountMySuffixUpdateComponent,
    BankAccountMySuffixDeleteDialogComponent,
    BankAccountMySuffixDeletePopupComponent
  ],
  entryComponents: [
    BankAccountMySuffixComponent,
    BankAccountMySuffixUpdateComponent,
    BankAccountMySuffixDeleteDialogComponent,
    BankAccountMySuffixDeletePopupComponent
  ]
})
export class JhipsterBankAccountMySuffixModule {}
