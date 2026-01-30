import {Component, inject, ViewEncapsulation} from '@angular/core';
import {InputText} from "primeng/inputtext";
import {ReactiveFormsModule} from "@angular/forms";
import {Button} from "primeng/button";
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {RegisterFormGroup, RegisterFormGroupService} from "./register-form-group-service";
import {AuthApi} from "../../data-access/auth.api";
import {LanguageType} from "../../../../shared/enum/language-type";
import {CurrencyType} from "../../../../shared/enum/currency-type";
import {DropdownModule} from "primeng/dropdown";
import {RouterLink} from "@angular/router";
import {MessageService} from "primeng/api";
import {Toast} from "primeng/toast";
import {HttpErrorResponse} from "@angular/common/module.d-CnjH8Dlt";

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
        Toast,
    ],
    providers: [MessageService],
    templateUrl: './register.component.html',
    styleUrl: './register.component.scss',
})
export class RegisterComponent {
    private readonly registerFormGroupService = inject(RegisterFormGroupService);
    private readonly userService = inject(AuthApi);
    private readonly messageService = inject(MessageService);
    private readonly translate = inject(TranslateService);

    protected readonly registerForm: RegisterFormGroup = this.registerFormGroupService.createEmpty();

    languageOptions = Object.values(LanguageType).map(v => ({
        label: v,
        value: v,
    }));

    currencyOptions = Object.values(CurrencyType).map(v => ({
        label: v,
        value: v,
    }));

    protected register(): void {
        if (this.registerForm.invalid) {
            this.registerForm.markAllAsTouched();
            return;
        }

        const request = this.registerFormGroupService.toRequest(this.registerForm);

        this.userService.register(request).subscribe({
            next: () => {
               // TODO: route to register success page

            },
            error: (err: HttpErrorResponse) => {
                const mapped = this.mapBackendErrorToI18n(err);
                this.toast('error', mapped.summaryKey, mapped.detailKey, mapped.params);
            }
        });
    }

    private toast(
        severity: 'success' | 'error' | 'warn' | 'info',
        summaryKey: string,
        detailKey: string,
        params?: Record<string, any>
    ): void {
        this.messageService.add({
            severity,
            summary: this.translate.instant(summaryKey, params),
            detail: this.translate.instant(detailKey, params)
        });
    }

    private mapBackendErrorToI18n(err: HttpErrorResponse): {
        summaryKey: string;
        detailKey: string;
        params?: Record<string, any>;
    } {
        const status = err.status;

        if (status === 409) {
            return {
                summaryKey: 'error.register.summary',
                detailKey: 'error.register.http.409'
            };
        }
        if (status === 404) {
            return {
                summaryKey: 'error.register.summary',
                detailKey: 'error.register.http.400'
            };
        }

        return {
            summaryKey: 'error.register.summary',
            detailKey: 'error.register.other'
        };
    }
}
