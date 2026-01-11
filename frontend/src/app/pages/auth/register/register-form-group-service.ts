import {Injectable} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {CurrencyType} from "../../../common/enum/currency-type";
import {LanguageType} from "../../../common/enum/language-type";
import {KeycloakUserRegisterRequest} from "../../../common/model/iam/request/keycloak-user-register-request";

export type KeycloakUserCreateFormGroup = FormGroup<{
    email: FormControl<string>;
    firstName: FormControl<string>;
    lastName: FormControl<string>;
}>;

export type RegisterFormGroup = FormGroup<{
    language: FormControl<LanguageType.PL>;
    currency: FormControl<CurrencyType.PLN>;
    userCreateRequest: KeycloakUserCreateFormGroup;
}>;

@Injectable({ providedIn: 'root' })
export class RegisterFormGroupService {
    createEmpty(): RegisterFormGroup {
        return new FormGroup({
            language: new FormControl<LanguageType.PL>(null, { validators: [Validators.required] }),
            currency: new FormControl<CurrencyType.PLN>(null, { validators: [Validators.required] }),
            userCreateRequest: new FormGroup({
                email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
                firstName: new FormControl('', {
                    nonNullable: true,
                    validators: [Validators.required, Validators.maxLength(50)],
                }),
                lastName: new FormControl('', {
                    nonNullable: true,
                    validators: [Validators.required, Validators.maxLength(50)],
                }),
            }),
        });
    }

    toRequest(form: RegisterFormGroup): KeycloakUserRegisterRequest {
        const v = form.getRawValue();

        return {
            language: v.language,
            currency: v.currency,
            userCreateRequest: {
                email: v.userCreateRequest.email,
                firstName: v.userCreateRequest.firstName,
                lastName: v.userCreateRequest.lastName,
            },
        };
    }
}
