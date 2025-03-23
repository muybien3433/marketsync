import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslatePipe, TranslateService} from "@ngx-translate/core";
import {AssetAggregate} from "../../common/model/asset-aggregate";
import {HttpClient} from "@angular/common/http";
import {CurrencyType} from "../../common/model/currency-type";
import {environmentProd} from "../../../environments/environment.prod";
import {API_ENDPOINTS} from "../../common/service/api-endpoints";
import {PreferenceService} from "../../common/service/preference-service";
import {Router} from "@angular/router";
import {ApexOptions, ChartComponent} from "ng-apexcharts";
import {CardComponent} from "../../common/components/card/card.component";

@Component({
    selector: 'app-wallet',
    imports: [CommonModule, CardComponent, ChartComponent, TranslatePipe],
    templateUrl: './wallet.component.html',
    styleUrls: ['./wallet.component.scss']
})
export class WalletComponent implements OnInit {
    protected readonly Object = Object;
    private _assets: AssetAggregate[] = [];
    groupedAssets: { [key: string]: AssetAggregate[] } = {};
    selectedCurrency: CurrencyType;
    donutChart: Partial<ApexOptions>;

    constructor(
        private http: HttpClient,
        private preferenceService: PreferenceService,
        private router: Router,
        private translate: TranslateService,
    ) {
        this.donutChart = {
            chart: {
                type: 'donut',
                width: '100%',
                height: 350
            },
            dataLabels: {
                enabled: false
            },
            plotOptions: {
                pie: {
                    customScale: 0.8,
                    donut: {
                        size: '75%'
                    },
                    offsetY: 20
                }
            },
            colors: ['#775DD0', '#008FFB'],
            series: [],
            labels: [],
            legend: {
                position: 'left',
                offsetY: 80
            }
        };
    }

    ngOnInit() {
        setTimeout(() => {
            this.selectedCurrency = this.preferenceService.getPreferredCurrency();
            this.fetchWalletAssets();
        }, 500);
    }

    groupAssetsByType() {
        this.groupedAssets = {};
        const totalValues: { [key: string]: number } = {};

        this._assets.forEach((asset) => {
            this.translate
                .get(`asset.assetType.${asset.assetType}`)
                .subscribe((translatedType) => {
                    if (!this.groupedAssets[translatedType]) {
                        this.groupedAssets[translatedType] = [];
                        totalValues[translatedType] = 0;
                    }
                    this.groupedAssets[translatedType].push(asset);
                    totalValues[translatedType] += asset.value;
                });
        });

        this.updateChart(totalValues);
    }

    updateChart(totalValues: { [key: string]: number }) {
        const labels = Object.keys(this.groupedAssets);
        const series = labels.map(label => totalValues[label]);

        this.donutChart = {
            ...this.donutChart,
            labels,
            series
        };
    }

    getTotalValue(): number {
        return this._assets.reduce((total, asset) => {
            const valueInSelectedCurrency = asset.value * asset.exchangeRateToDesired;
            return total + valueInSelectedCurrency;
        }, 0);
    }

    getTotalProfit(): number {
        return this._assets.reduce((total, asset) => {
            const profitInSelectedCurrency = asset.profit * asset.exchangeRateToDesired;
            return total + profitInSelectedCurrency;
        }, 0);
    }

    getTotalProfitInPercentage(): number {
        const totalInvestment = this.getTotalInvestment();
        const totalProfit = this.getTotalProfit();

        if (totalInvestment === 0) return 0;

        const profitInPercentage = (totalProfit / totalInvestment) * 100;
        return parseFloat(profitInPercentage.toFixed(2));
    }

    getTotalInvestment(): number {
        return this._assets.reduce((total, asset) => {
            return total + (asset.averagePurchasePrice * asset.count * asset.exchangeRateToDesired);
        }, 0);
    }

    getCurrencyForSum() {
        return this.selectedCurrency;
    }

    onCurrencyChange(selectedCurrency: CurrencyType) {
        this.selectedCurrency = selectedCurrency;
        this.fetchWalletAssets();
    }

    addAssetButton() {
        this.router.navigate(['wallet-add-asset']);
    }

    fetchWalletAssets() {
        this.http.get<AssetAggregate[]>(`${environmentProd.baseUrl}${API_ENDPOINTS.WALLET}/${this.selectedCurrency}`)
            .subscribe({
                    next: (assets) => {
                        this._assets = Array.isArray(assets) ? assets : [];
                        this.groupAssetsByType();
                    },
                    error: (err) => {
                        console.error(err);
                        this._assets = [];
                    },
                }
            );
    }
}
