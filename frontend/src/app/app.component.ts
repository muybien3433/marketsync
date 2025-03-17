import {Component} from '@angular/core';
import {RouterModule, RouterOutlet} from '@angular/router';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatButtonModule} from '@angular/material/button';
import {MatListModule} from '@angular/material/list';
import {MatCardModule} from '@angular/material/card';
import {ClipboardModule} from '@angular/cdk/clipboard';
import {HomeComponent} from "./pages/home/home.component";


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,
    MatGridListModule,
    MatButtonModule,
    MatListModule,
    MatCardModule,
    ClipboardModule,
    RouterModule, HomeComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
}
