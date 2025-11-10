import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewModel } from './preview-model';

describe('PreviewModel', () => {
  let component: PreviewModel;
  let fixture: ComponentFixture<PreviewModel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreviewModel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreviewModel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
