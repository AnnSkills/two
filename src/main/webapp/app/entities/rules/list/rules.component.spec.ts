import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { RulesService } from '../service/rules.service';

import { RulesComponent } from './rules.component';

describe('Rules Management Component', () => {
  let comp: RulesComponent;
  let fixture: ComponentFixture<RulesComponent>;
  let service: RulesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [RulesComponent],
    })
      .overrideTemplate(RulesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RulesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RulesService);

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
    expect(comp.rules?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
