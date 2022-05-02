import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBag } from '../bag.model';

@Component({
  selector: 'anna-bag-detail',
  templateUrl: './bag-detail.component.html',
})
export class BagDetailComponent implements OnInit {
  bag: IBag | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bag }) => {
      this.bag = bag;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
