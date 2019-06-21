/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { JhipsterTestModule } from '../../../../test.module';
import { BankAccountMySuffixDeleteDialogComponent } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix-delete-dialog.component';
import { BankAccountMySuffixService } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix.service';

describe('Component Tests', () => {
  describe('BankAccountMySuffix Management Delete Component', () => {
    let comp: BankAccountMySuffixDeleteDialogComponent;
    let fixture: ComponentFixture<BankAccountMySuffixDeleteDialogComponent>;
    let service: BankAccountMySuffixService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhipsterTestModule],
        declarations: [BankAccountMySuffixDeleteDialogComponent]
      })
        .overrideTemplate(BankAccountMySuffixDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BankAccountMySuffixDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BankAccountMySuffixService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
