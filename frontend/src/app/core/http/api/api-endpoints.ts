import {environment} from "../../../../environments/environment";

export const API_ENDPOINTS = {
    WALLET: `${environment.baseUrl}/wallets/assets`,
    FINANCE: `${environment.baseUrl}/finances`,
    SUBSCRIPTION: `${environment.baseUrl}/subscriptions`,
    IAM: `${environment.baseUrl}/iam`,
    USER: `${environment.baseUrl}/users`
}
