import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslatePipe, TranslateService} from "@ngx-translate/core";
import {AssetAggregate} from "../../common/model/asset-aggregate";
import {HttpClient} from "@angular/common/http";
import {CurrencyType} from "../../common/model/currency-type";
import {API_ENDPOINTS} from "../../common/service/api-endpoints";
import {PreferenceService} from "../../common/service/preference-service";
import {Router} from "@angular/router";
import {ApexOptions, ChartComponent} from "ng-apexcharts";
import {CardComponent} from "../../common/components/card/card.component";
import {NgbProgressbar} from "@ng-bootstrap/ng-bootstrap";
import {environment} from "../../../environments/environment";

@Component({
    selector: 'app-wallet',
    imports: [CommonModule, CardComponent, ChartComponent, TranslatePipe, NgbProgressbar, CardComponent],
    templateUrl: './wallet.component.html',
    styleUrls: ['./wallet.component.scss']
})
export default class WalletComponent implements OnInit {
    protected readonly Object = Object;
    protected _assets: AssetAggregate[] = [];
    groupedAssets: { [key: string]: AssetAggregate[] } = {};
    selectedCurrency: CurrencyType;
    donutChart: Partial<ApexOptions>;
    profits: any[] = [];

    isLoading: boolean = true;

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
                height: 297
            },
            dataLabels: {
                enabled: true,
                formatter: (val: number) => {
                    return val.toFixed(2) + '%';
                }
            },
            plotOptions: {
                pie: {
                    customScale: 0.8,
                    donut: {
                        size: '50%'
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
            },
        };
    }

    ngOnInit() {
        setTimeout(() => {
            this.selectedCurrency = this.preferenceService.getPreferredCurrency();
            this.fetchWalletAssets();
        }, 300);
    }

    groupAssetsByType() {
        this.groupedAssets = {};
        const totalValues: { [key: string]: number } = {};

        this._assets.forEach((asset) => {
            this.translate
                .get(`asset.type.${asset.assetType}`)
                .subscribe((translatedType) => {
                    if (!this.groupedAssets[translatedType]) {
                        this.groupedAssets[translatedType] = [];
                        totalValues[translatedType] = 0;
                    }
                    this.groupedAssets[translatedType].push(asset);
                    totalValues[translatedType] += asset.value;
                });
        });
        this.updateAssetDivisionChart(totalValues);
    }

    getTotalValue(): number {
        return this._assets.reduce((total, asset) => {
            const valueInSelectedCurrency = asset.value * asset.exchangeRateToDesired;
            return total + valueInSelectedCurrency;
        }, 0);
    }

    getTotalProfit(): number {
        return this._assets.reduce((total, asset) => {
            return total + (asset.profit * asset.exchangeRateToDesired);
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

    addAssetButton() {
        this.router.navigate(['wallet/asset/add']);
    }

    private updateProfitCharts() {
        this.profits = [
            {
                title: this.translate.instant('common.total.value'),
                icon: this.resolveProfitIcon(),
                amount: this.getTotalValue(),
                progress: 80,
                design: 'col-md-6',
                progress_bg: 'progress-c-theme'
            },
            {
                title: this.translate.instant('asset.profit'),
                icon: this.resolveProfitIcon(),
                amount: this.getTotalProfit(),
                progress: 60,
                design: 'col-md-6',
                progress_bg: 'progress-c-theme2'
            },
            {
                title: this.translate.instant('asset.profit.in.percentage'),
                icon: this.resolveProfitIcon(),
                percent: this.getTotalProfitInPercentage() + '%',
                progress: 40,
                design: 'col-md-6',
                progress_bg: 'progress-c-theme3'
            }
        ];
    }

    private resolveProfitIcon() {
        return this.getTotalProfit() > 0 ? 'icon-arrow-up text-c-green' : 'icon-arrow-down text-c-red';
    }


    private updateAssetDivisionChart(totalValues: { [key: string]: number }) {
        const labels = Object.keys(this.groupedAssets);
        const series = labels.map(label => totalValues[label]);

        this.donutChart = {
            ...this.donutChart,
            labels,
            series
        };
    }

    fetchWalletAssets() {
        this.isLoading = true;
        this.http.get<AssetAggregate[]>(`${environment.baseUrl}${API_ENDPOINTS.WALLET}/${this.selectedCurrency}`)
            .subscribe({
                    next: (assets) => {
                        this._assets = Array.isArray(assets) ? assets : [];
                        this.groupAssetsByType();
                        this.updateProfitCharts();
                        this.isLoading = false;
                    },
                    error: (err) => {
                        console.error(err);
                        this._assets = [];
                        this.isLoading = false;
                    },
                }
            );
    }
}