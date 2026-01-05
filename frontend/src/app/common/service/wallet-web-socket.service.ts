import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Observable, Subject, from } from 'rxjs';
import { AssetAggregate } from '../model/asset-aggregate.model';
import { environment } from '../../../environments/environment';
import { CurrencyType } from '../enum/currency-type';
import {WsTokenService} from "./wws-token.service";

@Injectable({ providedIn: 'root' })
export class WalletWebsocketService {
    private client: Client | null = null;
    private assetsSubject = new Subject<AssetAggregate[]>();
    assets$: Observable<AssetAggregate[]> = this.assetsSubject.asObservable();
    private currency: CurrencyType | null = null;

    constructor(private wsTokenService: WsTokenService) {}

    connect(currency: CurrencyType): void {
        this.currency = currency;

        if (this.client && this.client.active) {
            return;
        }

        this.activateClient();
    }

    disconnect(): void {
        if (this.client && this.client.active) {
            this.client.deactivate();
        }
        this.client = null;
    }

    private activateClient(): void {
        const currency = this.currency;
        if (!currency) {
            return;
        }

        from(this.wsTokenService.buildAuthenticatedUrl(environment.wsWalletUrl)).subscribe(urlWithToken => {
            this.client = new Client({
                webSocketFactory: () => new SockJS(urlWithToken),
                reconnectDelay: 2500
            });

            this.client.onConnect = () => {
                const destination = `/topic/wallet/${currency}`;
                this.client!.subscribe(destination, (message: IMessage) => {
                    const body = JSON.parse(message.body) as AssetAggregate[];
                    this.assetsSubject.next(body);
                });
            };

            this.client.onWebSocketClose = () => {
                this.client = null;
                this.activateClient();
            };

            this.client.onStompError = frame => {
                console.error('STOMP error', frame.headers['message'], frame.body);
            };

            this.client.activate();
        });
    }
}
