import {Routes} from '@angular/router';
import {WalletComponent} from './pages/wallet/wallet.component';
import {SubscriptionComponent} from './pages/subscription/subscription.component';
import {AuthGuard} from './services/auth.guard';
import {AddAssetComponent} from './pages/wallet/add-asset/add-asset.component';
import {AssetHistoryComponent} from './pages/wallet/asset-history/asset-history.component';
import {EditAssetComponent} from './pages/wallet/edit-asset/edit-asset.component';
import {NavbarComponent} from "./pages/navbar/navbar.component";

export const routes: Routes = [
    {path: '', component: NavbarComponent},
    {path: 'wallet', component: WalletComponent, canActivate: [AuthGuard]},
    {path: 'add-asset', component: AddAssetComponent, canActivate: [AuthGuard]},
    {path: 'edit-asset', component: EditAssetComponent, canActivate: [AuthGuard]},
    {path: 'asset-history', component: AssetHistoryComponent, canActivate: [AuthGuard]},
    {path: 'subscription', component: SubscriptionComponent, canActivate: [AuthGuard]},
];
