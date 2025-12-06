import { Component, Input } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
    selector: 'app-loading-spinner',
    standalone: true,
    imports: [NgIf],
    templateUrl: './loading-spinner.component.html',
    styleUrls: ['./loading-spinner.component.scss']
})
export class LoadingSpinnerComponent {
    @Input() isLoading: boolean = false;
}
