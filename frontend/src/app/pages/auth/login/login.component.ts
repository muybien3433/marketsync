import {Component, inject, ViewEncapsulation} from '@angular/core';
import {InputText} from "primeng/inputtext";
import {ReactiveFormsModule} from "@angular/forms";
import {RouterLink} from "@angular/router";
import {Button} from "primeng/button";
import { TranslateModule } from '@ngx-translate/core';
import {LoginFormGroup, LoginFormGroupService} from "./login-form-group-service";
import {UserService} from "../../../common/service/user.service";

@Component({
    selector: 'app-login',
    standalone: true,
    encapsulation: ViewEncapsulation.None,
    imports: [
        TranslateModule,
        InputText,
        RouterLink,
        Button,
        ReactiveFormsModule,
    ],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss',
})
export class LoginComponent {
    private readonly loginFormGroupService = inject(LoginFormGroupService);
    private readonly userService = inject(UserService);

    protected readonly loginForm: LoginFormGroup = this.loginFormGroupService.createEmpty();

    protected login() {
        if (this.loginForm.invalid) {
            this.loginForm.markAllAsTouched();
            return;
        }

        const request = this.loginFormGroupService.toRequest(this.loginForm);

        this.userService.login(request).subscribe();
    }
}
