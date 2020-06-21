import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JhipsterSharedModule } from 'app/shared/shared.module';
import { OperationComponent } from './operation.component';
import { OperationDetailComponent } from './operation-detail.component';
import { OperationUpdateComponent } from './operation-update.component';
import { OperationDeleteDialogComponent } from './operation-delete-dialog.component';
import { operationRoute } from './operation.route';

@NgModule({
  imports: [JhipsterSharedModule, RouterModule.forChild(operationRoute)],
  declarations: [OperationComponent, OperationDetailComponent, OperationUpdateComponent, OperationDeleteDialogComponent],
  entryComponents: [OperationDeleteDialogComponent],
})
export class JhipsterOperationModule {}
