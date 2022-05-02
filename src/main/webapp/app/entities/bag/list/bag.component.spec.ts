import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { BagService } from '../service/bag.service';

import { BagComponent } from './bag.component';

describe('Bag Management Component', () => {
  let comp: BagComponent;
  let fixture: ComponentFixture<BagComponent>;
  let service: BagService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [BagComponent],
    })
      .overrideTemplate(BagComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BagComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BagService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.bags?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
