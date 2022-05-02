import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BagDetailComponent } from './bag-detail.component';

describe('Bag Management Detail Component', () => {
  let comp: BagDetailComponent;
  let fixture: ComponentFixture<BagDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BagDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ bag: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BagDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BagDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load bag on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.bag).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
