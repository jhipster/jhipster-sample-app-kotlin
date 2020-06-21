import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { JhipsterTestModule } from '../../../../test.module';
import { BankAccountMySuffixComponent } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix.component';
import { BankAccountMySuffixService } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix.service';
import { BankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';

describe('Component Tests', () => {
  describe('BankAccountMySuffix Management Component', () => {
    let comp: BankAccountMySuffixComponent;
    let fixture: ComponentFixture<BankAccountMySuffixComponent>;
    let service: BankAccountMySuffixService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhipsterTestModule],
        declarations: [BankAccountMySuffixComponent],
      })
        .overrideTemplate(BankAccountMySuffixComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BankAccountMySuffixComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BankAccountMySuffixService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new BankAccountMySuffix(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.bankAccounts && comp.bankAccounts[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
