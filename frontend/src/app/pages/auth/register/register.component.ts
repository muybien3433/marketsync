import {Component, inject, ViewEncapsulation} from '@angular/core';
import {InputText} from "primeng/inputtext";
import {ReactiveFormsModule} from "@angular/forms";
import {Button} from "primeng/button";
import { TranslateModule } from '@ngx-translate/core';
import {RegisterFormGroup, RegisterFormGroupService} from "./register-form-group-service";
import {UserService} from "../../../common/service/user.service";
import {LanguageType} from "../../../common/enum/language-type";
import {CurrencyType} from "../../../common/enum/currency-type";
import {DropdownModule} from "primeng/dropdown";
import {RouterLink} from "@angular/router";

@Component({
    selector: 'app-register',
    standalone: true,
    encapsulation: ViewEncapsulation.None,
    imports: [
        TranslateModule,
        InputText,
        Button,
        ReactiveFormsModule,
        DropdownModule,
        RouterLink,
    ],
    templateUrl: './register.component.html',
    styleUrl: './register.component.scss',
})
export class RegisterComponent {
    private readonly registerFormGroupService = inject(RegisterFormGroupService);
    private readonly userService = inject(UserService);

    protected readonly registerForm: RegisterFormGroup = this.registerFormGroupService.createEmpty();

    languageOptions = Object.values(LanguageType).map(v => ({
        label: v,
        value: v,
    }));

    currencyOptions = Object.values(CurrencyType).map(v => ({
        label: v,
        value: v,
    }));

    protected register() {
        if (this.registerForm.invalid) {
            this.registerForm.markAllAsTouched();
            return;
        }

        const request = this.registerFormGroupService.toRequest(this.registerForm);

        this.userService.register(request).subscribe();
    }
}
