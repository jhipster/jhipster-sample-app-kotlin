/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { JhipsterTestModule } from '../../../../test.module';
import { BankAccountMySuffixDetailComponent } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix-detail.component';
import { BankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';

describe('Component Tests', () => {
  describe('BankAccountMySuffix Management Detail Component', () => {
    let comp: BankAccountMySuffixDetailComponent;
    let fixture: ComponentFixture<BankAccountMySuffixDetailComponent>;
    const route = ({ data: of({ bankAccount: new BankAccountMySuffix(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhipsterTestModule],
        declarations: [BankAccountMySuffixDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(BankAccountMySuffixDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BankAccountMySuffixDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.bankAccount).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
