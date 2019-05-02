/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { JhipsterSampleApplicationTestModule } from '../../../../test.module';
import { BankAccountMySuffixUpdateComponent } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix-update.component';
import { BankAccountMySuffixService } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix.service';
import { BankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';

describe('Component Tests', () => {
  describe('BankAccountMySuffix Management Update Component', () => {
    let comp: BankAccountMySuffixUpdateComponent;
    let fixture: ComponentFixture<BankAccountMySuffixUpdateComponent>;
    let service: BankAccountMySuffixService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhipsterSampleApplicationTestModule],
        declarations: [BankAccountMySuffixUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(BankAccountMySuffixUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BankAccountMySuffixUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BankAccountMySuffixService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new BankAccountMySuffix(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new BankAccountMySuffix();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
