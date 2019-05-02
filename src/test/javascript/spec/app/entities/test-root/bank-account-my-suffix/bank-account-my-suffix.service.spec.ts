/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_FORMAT, DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { BankAccountMySuffixService } from 'app/entities/test-root/bank-account-my-suffix/bank-account-my-suffix.service';
import { IBankAccountMySuffix, BankAccountMySuffix, BankAccountType } from 'app/shared/model/test-root/bank-account-my-suffix.model';

describe('Service Tests', () => {
  describe('BankAccountMySuffix Service', () => {
    let injector: TestBed;
    let service: BankAccountMySuffixService;
    let httpMock: HttpTestingController;
    let elemDefault: IBankAccountMySuffix;
    let expectedResult;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(BankAccountMySuffixService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new BankAccountMySuffix(
        0,
        'AAAAAAA',
        0,
        0,
        0,
        0,
        0,
        currentDate,
        currentDate,
        false,
        BankAccountType.CHECKING,
        'image/png',
        'AAAAAAA',
        'AAAAAAA'
      );
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign(
          {
            openingDay: currentDate.format(DATE_FORMAT),
            lastOperationDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a BankAccountMySuffix', async () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            openingDay: currentDate.format(DATE_FORMAT),
            lastOperationDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            openingDay: currentDate,
            lastOperationDate: currentDate
          },
          returnedFromService
        );
        service
          .create(new BankAccountMySuffix(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a BankAccountMySuffix', async () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            bankNumber: 1,
            agencyNumber: 1,
            lastOperationDuration: 1,
            meanOperationDuration: 1,
            balance: 1,
            openingDay: currentDate.format(DATE_FORMAT),
            lastOperationDate: currentDate.format(DATE_TIME_FORMAT),
            active: true,
            accountType: 'BBBBBB',
            attachment: 'BBBBBB',
            description: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            openingDay: currentDate,
            lastOperationDate: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of BankAccountMySuffix', async () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            bankNumber: 1,
            agencyNumber: 1,
            lastOperationDuration: 1,
            meanOperationDuration: 1,
            balance: 1,
            openingDay: currentDate.format(DATE_FORMAT),
            lastOperationDate: currentDate.format(DATE_TIME_FORMAT),
            active: true,
            accountType: 'BBBBBB',
            attachment: 'BBBBBB',
            description: 'BBBBBB'
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            openingDay: currentDate,
            lastOperationDate: currentDate
          },
          returnedFromService
        );
        service
          .query(expected)
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a BankAccountMySuffix', async () => {
        const rxPromise = service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
