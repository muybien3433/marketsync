import { bootstrapApplication } from "@angular/platform-browser";
import { enableProdMode } from "@angular/core";
import { AppComponent } from "./app/app.component";
import { environmentProd } from "./environments/environment.prod";
import { appConfig } from './app/app.config';

if (environmentProd) {
    enableProdMode();
}

bootstrapApplication(AppComponent, appConfig)
    .catch((err) => console.error());
