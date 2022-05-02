import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { SupportService } from '../service/support.service';

import { SupportComponent } from './support.component';

describe('Support Management Component', () => {
  let comp: SupportComponent;
  let fixture: ComponentFixture<SupportComponent>;
  let service: SupportService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [SupportComponent],
    })
      .overrideTemplate(SupportComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SupportComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SupportService);

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
    expect(comp.supports?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
