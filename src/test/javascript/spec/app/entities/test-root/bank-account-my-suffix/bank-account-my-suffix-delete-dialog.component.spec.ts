import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { JhipsterTestModule } from '../../../../test.module';
import { MockEventManager } from '../../../../helpers/mock-event-manager.service';
import { MockActiveModal } from '../../../../helpers/mock-active-modal.service';
import { BankAccountMySuffixDeleteDialogComponent } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix-delete-dialog.component';
import { BankAccountMySuffixService } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix.service';

describe('Component Tests', () => {
  describe('BankAccountMySuffix Management Delete Component', () => {
    let comp: BankAccountMySuffixDeleteDialogComponent;
    let fixture: ComponentFixture<BankAccountMySuffixDeleteDialogComponent>;
    let service: BankAccountMySuffixService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhipsterTestModule],
        declarations: [BankAccountMySuffixDeleteDialogComponent],
      })
        .overrideTemplate(BankAccountMySuffixDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BankAccountMySuffixDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BankAccountMySuffixService);
      mockEventManager = TestBed.get(JhiEventManager);
      mockActiveModal = TestBed.get(NgbActiveModal);
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
          expect(mockActiveModal.closeSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));

      it('Should not call delete service on clear', () => {
        // GIVEN
        spyOn(service, 'delete');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.delete).not.toHaveBeenCalled();
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
      });
    });
  });
});
