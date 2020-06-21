import { element, by, ElementFinder } from 'protractor';

export class BankAccountComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-bank-account-my-suffix div table .btn-danger'));
  title = element.all(by.css('jhi-bank-account-my-suffix div h2#page-heading span')).first();
  noResult = element(by.id('no-result'));
  entities = element(by.id('entities'));

  async clickOnCreateButton(): Promise<void> {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(): Promise<void> {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons(): Promise<number> {
    return this.deleteButtons.count();
  }

  async getTitle(): Promise<string> {
    return this.title.getAttribute('jhiTranslate');
  }
}

export class BankAccountUpdatePage {
  pageTitle = element(by.id('jhi-bank-account-my-suffix-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  nameInput = element(by.id('field_name'));
  bankNumberInput = element(by.id('field_bankNumber'));
  agencyNumberInput = element(by.id('field_agencyNumber'));
  lastOperationDurationInput = element(by.id('field_lastOperationDuration'));
  meanOperationDurationInput = element(by.id('field_meanOperationDuration'));
  balanceInput = element(by.id('field_balance'));
  openingDayInput = element(by.id('field_openingDay'));
  lastOperationDateInput = element(by.id('field_lastOperationDate'));
  activeInput = element(by.id('field_active'));
  accountTypeSelect = element(by.id('field_accountType'));
  attachmentInput = element(by.id('file_attachment'));
  descriptionInput = element(by.id('field_description'));

  userSelect = element(by.id('field_user'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setNameInput(name: string): Promise<void> {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput(): Promise<string> {
    return await this.nameInput.getAttribute('value');
  }

  async setBankNumberInput(bankNumber: string): Promise<void> {
    await this.bankNumberInput.sendKeys(bankNumber);
  }

  async getBankNumberInput(): Promise<string> {
    return await this.bankNumberInput.getAttribute('value');
  }

  async setAgencyNumberInput(agencyNumber: string): Promise<void> {
    await this.agencyNumberInput.sendKeys(agencyNumber);
  }

  async getAgencyNumberInput(): Promise<string> {
    return await this.agencyNumberInput.getAttribute('value');
  }

  async setLastOperationDurationInput(lastOperationDuration: string): Promise<void> {
    await this.lastOperationDurationInput.sendKeys(lastOperationDuration);
  }

  async getLastOperationDurationInput(): Promise<string> {
    return await this.lastOperationDurationInput.getAttribute('value');
  }

  async setMeanOperationDurationInput(meanOperationDuration: string): Promise<void> {
    await this.meanOperationDurationInput.sendKeys(meanOperationDuration);
  }

  async getMeanOperationDurationInput(): Promise<string> {
    return await this.meanOperationDurationInput.getAttribute('value');
  }

  async setBalanceInput(balance: string): Promise<void> {
    await this.balanceInput.sendKeys(balance);
  }

  async getBalanceInput(): Promise<string> {
    return await this.balanceInput.getAttribute('value');
  }

  async setOpeningDayInput(openingDay: string): Promise<void> {
    await this.openingDayInput.sendKeys(openingDay);
  }

  async getOpeningDayInput(): Promise<string> {
    return await this.openingDayInput.getAttribute('value');
  }

  async setLastOperationDateInput(lastOperationDate: string): Promise<void> {
    await this.lastOperationDateInput.sendKeys(lastOperationDate);
  }

  async getLastOperationDateInput(): Promise<string> {
    return await this.lastOperationDateInput.getAttribute('value');
  }

  getActiveInput(): ElementFinder {
    return this.activeInput;
  }

  async setAccountTypeSelect(accountType: string): Promise<void> {
    await this.accountTypeSelect.sendKeys(accountType);
  }

  async getAccountTypeSelect(): Promise<string> {
    return await this.accountTypeSelect.element(by.css('option:checked')).getText();
  }

  async accountTypeSelectLastOption(): Promise<void> {
    await this.accountTypeSelect.all(by.tagName('option')).last().click();
  }

  async setAttachmentInput(attachment: string): Promise<void> {
    await this.attachmentInput.sendKeys(attachment);
  }

  async getAttachmentInput(): Promise<string> {
    return await this.attachmentInput.getAttribute('value');
  }

  async setDescriptionInput(description: string): Promise<void> {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput(): Promise<string> {
    return await this.descriptionInput.getAttribute('value');
  }

  async userSelectLastOption(): Promise<void> {
    await this.userSelect.all(by.tagName('option')).last().click();
  }

  async userSelectOption(option: string): Promise<void> {
    await this.userSelect.sendKeys(option);
  }

  getUserSelect(): ElementFinder {
    return this.userSelect;
  }

  async getUserSelectedOption(): Promise<string> {
    return await this.userSelect.element(by.css('option:checked')).getText();
  }

  async save(): Promise<void> {
    await this.saveButton.click();
  }

  async cancel(): Promise<void> {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class BankAccountDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-bankAccount-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-bankAccount'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
