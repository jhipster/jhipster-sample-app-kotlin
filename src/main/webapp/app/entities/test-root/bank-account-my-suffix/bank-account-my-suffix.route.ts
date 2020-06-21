import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IBankAccountMySuffix, BankAccountMySuffix } from 'app/shared/model/test-root/bank-account-my-suffix.model';
import { BankAccountMySuffixService } from './bank-account-my-suffix.service';
import { BankAccountMySuffixComponent } from './bank-account-my-suffix.component';
import { BankAccountMySuffixDetailComponent } from './bank-account-my-suffix-detail.component';
import { BankAccountMySuffixUpdateComponent } from './bank-account-my-suffix-update.component';

@Injectable({ providedIn: 'root' })
export class BankAccountMySuffixResolve implements Resolve<IBankAccountMySuffix> {
  constructor(private service: BankAccountMySuffixService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBankAccountMySuffix> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((bankAccount: HttpResponse<BankAccountMySuffix>) => {
          if (bankAccount.body) {
            return of(bankAccount.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BankAccountMySuffix());
  }
}

export const bankAccountRoute: Routes = [
  {
    path: '',
    component: BankAccountMySuffixComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhipsterApp.testRootBankAccount.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BankAccountMySuffixDetailComponent,
    resolve: {
      bankAccount: BankAccountMySuffixResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhipsterApp.testRootBankAccount.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BankAccountMySuffixUpdateComponent,
    resolve: {
      bankAccount: BankAccountMySuffixResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhipsterApp.testRootBankAccount.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BankAccountMySuffixUpdateComponent,
    resolve: {
      bankAccount: BankAccountMySuffixResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'jhipsterApp.testRootBankAccount.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
