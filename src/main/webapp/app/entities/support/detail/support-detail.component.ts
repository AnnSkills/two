import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISupport } from '../support.model';

@Component({
  selector: 'anna-support-detail',
  templateUrl: './support-detail.component.html',
})
export class SupportDetailComponent implements OnInit {
  support: ISupport | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ support }) => {
      this.support = support;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
