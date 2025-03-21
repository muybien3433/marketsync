import {Routes} from '@angular/router';
import {AuthGuard} from './services/auth.guard';
import {HomeComponent} from "./pages/home/home.component";
import {WalletComponent} from "./pages/wallet/wallet.component";
import {WalletAddAssetComponent} from "./pages/wallet/wallet-add-asset/wallet-add-asset.component";
import {WalletEditAssetComponent} from "./pages/wallet/wallet-edit-asset/wallet-edit-asset.component";
import {WalletAssetHistoryComponent} from "./pages/wallet/wallet-asset-history/wallet-asset-history.component";
import {SubscriptionComponent} from "./pages/subscription/subscription.component";
import {SubscriptionAddComponent} from "./pages/subscription/subscription-add/subscription-add.component";


export const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'wallet', component: WalletComponent, canActivate: [AuthGuard]},
    {path: 'wallet-add-asset', component: WalletAddAssetComponent, canActivate: [AuthGuard]},
    {path: 'wallet-edit-asset', component: WalletEditAssetComponent, canActivate: [AuthGuard]},
    {path: 'wallet-asset-history', component: WalletAssetHistoryComponent, canActivate: [AuthGuard]},
    {path: 'subscription', component: SubscriptionComponent, canActivate: [AuthGuard]},
    {path: 'subscription-add', component: SubscriptionAddComponent, canActivate: [AuthGuard]},
];
