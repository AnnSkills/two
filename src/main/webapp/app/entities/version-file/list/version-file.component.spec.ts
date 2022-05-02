import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { VersionFileService } from '../service/version-file.service';

import { VersionFileComponent } from './version-file.component';

describe('VersionFile Management Component', () => {
  let comp: VersionFileComponent;
  let fixture: ComponentFixture<VersionFileComponent>;
  let service: VersionFileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [VersionFileComponent],
    })
      .overrideTemplate(VersionFileComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VersionFileComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(VersionFileService);

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
    expect(comp.versionFiles?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
