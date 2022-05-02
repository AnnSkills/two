import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SupportDetailComponent } from './support-detail.component';

describe('Support Management Detail Component', () => {
  let comp: SupportDetailComponent;
  let fixture: ComponentFixture<SupportDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SupportDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ support: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SupportDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SupportDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load support on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.support).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
