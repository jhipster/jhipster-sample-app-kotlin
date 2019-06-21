import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { JhipsterSharedModule } from 'app/shared';
import {
  BankAccountMySuffixComponent,
  BankAccountMySuffixDetailComponent,
  BankAccountMySuffixUpdateComponent,
  BankAccountMySuffixDeletePopupComponent,
  BankAccountMySuffixDeleteDialogComponent,
  bankAccountRoute,
  bankAccountPopupRoute
} from './';

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
  ],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JhipsterBankAccountMySuffixModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey !== undefined) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
