import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IBankAccountMySuffix, BankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';
import { BankAccountMySuffixService } from './bank-account-my-suffix.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-bank-account-my-suffix-update',
  templateUrl: './bank-account-my-suffix-update.component.html',
})
export class BankAccountMySuffixUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  openingDayDp: any;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    bankNumber: [],
    agencyNumber: [],
    lastOperationDuration: [],
    meanOperationDuration: [],
    balance: [null, [Validators.required]],
    openingDay: [],
    lastOperationDate: [],
    active: [],
    accountType: [],
    attachment: [],
    attachmentContentType: [],
    description: [],
    userId: [],
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected bankAccountService: BankAccountMySuffixService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bankAccount }) => {
      if (!bankAccount.id) {
        const today = moment().startOf('day');
        bankAccount.lastOperationDate = today;
      }

      this.updateForm(bankAccount);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(bankAccount: IBankAccountMySuffix): void {
    this.editForm.patchValue({
      id: bankAccount.id,
      name: bankAccount.name,
      bankNumber: bankAccount.bankNumber,
      agencyNumber: bankAccount.agencyNumber,
      lastOperationDuration: bankAccount.lastOperationDuration,
      meanOperationDuration: bankAccount.meanOperationDuration,
      balance: bankAccount.balance,
      openingDay: bankAccount.openingDay,
      lastOperationDate: bankAccount.lastOperationDate ? bankAccount.lastOperationDate.format(DATE_TIME_FORMAT) : null,
      active: bankAccount.active,
      accountType: bankAccount.accountType,
      attachment: bankAccount.attachment,
      attachmentContentType: bankAccount.attachmentContentType,
      description: bankAccount.description,
      userId: bankAccount.userId,
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('jhipsterApp.error', { ...err, key: 'error.file.' + err.key })
      );
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bankAccount = this.createFromForm();
    if (bankAccount.id !== undefined) {
      this.subscribeToSaveResponse(this.bankAccountService.update(bankAccount));
    } else {
      this.subscribeToSaveResponse(this.bankAccountService.create(bankAccount));
    }
  }

  private createFromForm(): IBankAccountMySuffix {
    return {
      ...new BankAccountMySuffix(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      bankNumber: this.editForm.get(['bankNumber'])!.value,
      agencyNumber: this.editForm.get(['agencyNumber'])!.value,
      lastOperationDuration: this.editForm.get(['lastOperationDuration'])!.value,
      meanOperationDuration: this.editForm.get(['meanOperationDuration'])!.value,
      balance: this.editForm.get(['balance'])!.value,
      openingDay: this.editForm.get(['openingDay'])!.value,
      lastOperationDate: this.editForm.get(['lastOperationDate'])!.value
        ? moment(this.editForm.get(['lastOperationDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      active: this.editForm.get(['active'])!.value,
      accountType: this.editForm.get(['accountType'])!.value,
      attachmentContentType: this.editForm.get(['attachmentContentType'])!.value,
      attachment: this.editForm.get(['attachment'])!.value,
      description: this.editForm.get(['description'])!.value,
      userId: this.editForm.get(['userId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBankAccountMySuffix>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IUser): any {
    return item.id;
  }
}
