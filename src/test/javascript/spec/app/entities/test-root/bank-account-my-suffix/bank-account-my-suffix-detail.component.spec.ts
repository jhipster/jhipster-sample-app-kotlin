import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { JhipsterTestModule } from '../../../../test.module';
import { BankAccountMySuffixDetailComponent } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix-detail.component';
import { BankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';

describe('Component Tests', () => {
  describe('BankAccountMySuffix Management Detail Component', () => {
    let comp: BankAccountMySuffixDetailComponent;
    let fixture: ComponentFixture<BankAccountMySuffixDetailComponent>;
    let dataUtils: JhiDataUtils;
    const route = ({ data: of({ bankAccount: new BankAccountMySuffix(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhipsterTestModule],
        declarations: [BankAccountMySuffixDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(BankAccountMySuffixDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BankAccountMySuffixDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load bankAccount on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.bankAccount).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });

    describe('byteSize', () => {
      it('Should call byteSize from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'byteSize');
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.byteSize(fakeBase64);

        // THEN
        expect(dataUtils.byteSize).toBeCalledWith(fakeBase64);
      });
    });

    describe('openFile', () => {
      it('Should call openFile from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'openFile');
        const fakeContentType = 'fake content type';
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.openFile(fakeContentType, fakeBase64);

        // THEN
        expect(dataUtils.openFile).toBeCalledWith(fakeContentType, fakeBase64);
      });
    });
  });
});
