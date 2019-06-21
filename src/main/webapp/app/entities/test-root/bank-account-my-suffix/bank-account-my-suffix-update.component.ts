import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IBankAccountMySuffix, BankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';
import { BankAccountMySuffixService } from './bank-account-my-suffix.service';
import { IUser, UserService } from 'app/core';

@Component({
  selector: 'jhi-bank-account-my-suffix-update',
  templateUrl: './bank-account-my-suffix-update.component.html'
})
export class BankAccountMySuffixUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];
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
    userId: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected bankAccountService: BankAccountMySuffixService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ bankAccount }) => {
      this.updateForm(bankAccount);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(bankAccount: IBankAccountMySuffix) {
    this.editForm.patchValue({
      id: bankAccount.id,
      name: bankAccount.name,
      bankNumber: bankAccount.bankNumber,
      agencyNumber: bankAccount.agencyNumber,
      lastOperationDuration: bankAccount.lastOperationDuration,
      meanOperationDuration: bankAccount.meanOperationDuration,
      balance: bankAccount.balance,
      openingDay: bankAccount.openingDay,
      lastOperationDate: bankAccount.lastOperationDate != null ? bankAccount.lastOperationDate.format(DATE_TIME_FORMAT) : null,
      active: bankAccount.active,
      accountType: bankAccount.accountType,
      attachment: bankAccount.attachment,
      attachmentContentType: bankAccount.attachmentContentType,
      description: bankAccount.description,
      userId: bankAccount.userId
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file = event.target.files[0];
        if (isImage && !/^image\//.test(file.type)) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      () => console.log('blob added'), // sucess
      this.onError
    );
  }

  previousState() {
    window.history.back();
  }

  save() {
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
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      bankNumber: this.editForm.get(['bankNumber']).value,
      agencyNumber: this.editForm.get(['agencyNumber']).value,
      lastOperationDuration: this.editForm.get(['lastOperationDuration']).value,
      meanOperationDuration: this.editForm.get(['meanOperationDuration']).value,
      balance: this.editForm.get(['balance']).value,
      openingDay: this.editForm.get(['openingDay']).value,
      lastOperationDate:
        this.editForm.get(['lastOperationDate']).value != null
          ? moment(this.editForm.get(['lastOperationDate']).value, DATE_TIME_FORMAT)
          : undefined,
      active: this.editForm.get(['active']).value,
      accountType: this.editForm.get(['accountType']).value,
      attachmentContentType: this.editForm.get(['attachmentContentType']).value,
      attachment: this.editForm.get(['attachment']).value,
      description: this.editForm.get(['description']).value,
      userId: this.editForm.get(['userId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBankAccountMySuffix>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }
}
