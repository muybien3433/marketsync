import {Component} from '@angular/core';
import {RouterModule, RouterOutlet} from '@angular/router';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatButtonModule} from '@angular/material/button';
import {MatListModule} from '@angular/material/list';
import {MatCardModule} from '@angular/material/card';
import {ClipboardModule} from '@angular/cdk/clipboard';


@Component({
    selector: 'app-root',
    imports: [RouterOutlet,
        MatGridListModule,
        MatButtonModule,
        MatListModule,
        MatCardModule,
        ClipboardModule,
        RouterModule,
    ],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent {
}
