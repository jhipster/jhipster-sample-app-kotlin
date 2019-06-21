import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'bank-account-my-suffix',
        loadChildren: './test-root/bank-account-my-suffix/bank-account-my-suffix.module#JhipsterBankAccountMySuffixModule'
      },
      {
        path: 'label',
        loadChildren: './test-root/label/label.module#JhipsterLabelModule'
      },
      {
        path: 'operation',
        loadChildren: './test-root/operation/operation.module#JhipsterOperationModule'
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ],
  declarations: [],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JhipsterEntityModule {}
