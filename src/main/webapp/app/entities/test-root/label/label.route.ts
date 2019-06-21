import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Label } from 'app/shared/model/test-root/label.model';
import { LabelService } from './label.service';
import { LabelComponent } from './label.component';
import { LabelDetailComponent } from './label-detail.component';
import { LabelUpdateComponent } from './label-update.component';
import { LabelDeletePopupComponent } from './label-delete-dialog.component';
import { ILabel } from 'app/shared/model/test-root/label.model';

@Injectable({ providedIn: 'root' })
export class LabelResolve implements Resolve<ILabel> {
  constructor(private service: LabelService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ILabel> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Label>) => response.ok),
        map((label: HttpResponse<Label>) => label.body)
      );
    }
    return of(new Label());
  }
}

export const labelRoute: Routes = [
  {
    path: '',
    component: LabelComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'jhipsterApp.testRootLabel.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: LabelDetailComponent,
    resolve: {
      label: LabelResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'jhipsterApp.testRootLabel.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: LabelUpdateComponent,
    resolve: {
      label: LabelResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'jhipsterApp.testRootLabel.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: LabelUpdateComponent,
    resolve: {
      label: LabelResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'jhipsterApp.testRootLabel.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const labelPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: LabelDeletePopupComponent,
    resolve: {
      label: LabelResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'jhipsterApp.testRootLabel.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
