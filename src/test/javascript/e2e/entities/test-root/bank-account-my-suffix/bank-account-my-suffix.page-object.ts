import { element, by, ElementFinder } from 'protractor';

export class BankAccountComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-bank-account-my-suffix div table .btn-danger'));
  title = element.all(by.css('jhi-bank-account-my-suffix div h2#page-heading span')).first();

  async clickOnCreateButton(timeout?: number) {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(timeout?: number) {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons() {
    return this.deleteButtons.count();
  }

  async getTitle() {
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

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return await this.nameInput.getAttribute('value');
  }

  async setBankNumberInput(bankNumber) {
    await this.bankNumberInput.sendKeys(bankNumber);
  }

  async getBankNumberInput() {
    return await this.bankNumberInput.getAttribute('value');
  }

  async setAgencyNumberInput(agencyNumber) {
    await this.agencyNumberInput.sendKeys(agencyNumber);
  }

  async getAgencyNumberInput() {
    return await this.agencyNumberInput.getAttribute('value');
  }

  async setLastOperationDurationInput(lastOperationDuration) {
    await this.lastOperationDurationInput.sendKeys(lastOperationDuration);
  }

  async getLastOperationDurationInput() {
    return await this.lastOperationDurationInput.getAttribute('value');
  }

  async setMeanOperationDurationInput(meanOperationDuration) {
    await this.meanOperationDurationInput.sendKeys(meanOperationDuration);
  }

  async getMeanOperationDurationInput() {
    return await this.meanOperationDurationInput.getAttribute('value');
  }

  async setBalanceInput(balance) {
    await this.balanceInput.sendKeys(balance);
  }

  async getBalanceInput() {
    return await this.balanceInput.getAttribute('value');
  }

  async setOpeningDayInput(openingDay) {
    await this.openingDayInput.sendKeys(openingDay);
  }

  async getOpeningDayInput() {
    return await this.openingDayInput.getAttribute('value');
  }

  async setLastOperationDateInput(lastOperationDate) {
    await this.lastOperationDateInput.sendKeys(lastOperationDate);
  }

  async getLastOperationDateInput() {
    return await this.lastOperationDateInput.getAttribute('value');
  }

  getActiveInput(timeout?: number) {
    return this.activeInput;
  }
  async setAccountTypeSelect(accountType) {
    await this.accountTypeSelect.sendKeys(accountType);
  }

  async getAccountTypeSelect() {
    return await this.accountTypeSelect.element(by.css('option:checked')).getText();
  }

  async accountTypeSelectLastOption(timeout?: number) {
    await this.accountTypeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setAttachmentInput(attachment) {
    await this.attachmentInput.sendKeys(attachment);
  }

  async getAttachmentInput() {
    return await this.attachmentInput.getAttribute('value');
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return await this.descriptionInput.getAttribute('value');
  }

  async userSelectLastOption(timeout?: number) {
    await this.userSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async userSelectOption(option) {
    await this.userSelect.sendKeys(option);
  }

  getUserSelect(): ElementFinder {
    return this.userSelect;
  }

  async getUserSelectedOption() {
    return await this.userSelect.element(by.css('option:checked')).getText();
  }

  async save(timeout?: number) {
    await this.saveButton.click();
  }

  async cancel(timeout?: number) {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class BankAccountDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-bankAccount-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-bankAccount'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
