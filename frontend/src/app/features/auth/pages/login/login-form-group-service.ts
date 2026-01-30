import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {KeycloakUserLoginRequest} from "../../models/request/keycloak-user-login-request";

export type LoginFormGroup = FormGroup<{
    email: FormControl<string>;
    password: FormControl<string>;
    rememberMe: FormControl<boolean>;
}>;

@Injectable({ providedIn: 'root' })
export class LoginFormGroupService {
    createEmpty(): LoginFormGroup {
        return new FormGroup({
            email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
            password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
            rememberMe: new FormControl(false, { nonNullable: true }),
        });
    }

    toRequest(form: LoginFormGroup): KeycloakUserLoginRequest {
        const v = form.getRawValue();

        return {
            username: v.email,
            password: v.password,
            rememberMe: v.rememberMe,
        };
    }
}