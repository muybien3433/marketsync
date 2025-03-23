export interface NavigationItem {
  id: string;
  title: string;
  type: 'item' | 'collapse' | 'group';
  translate?: string;
  icon?: string;
  hidden?: boolean;
  url?: string;
  classes?: string;
  exactMatch?: boolean;
  external?: boolean;
  target?: boolean;
  breadcrumbs?: boolean;

  children?: NavigationItem[];
}
export const NavigationItems: NavigationItem[] = [
  {
    id: 'wallet',
    title: 'Wallet',
    type: 'group',
    icon: 'icon-ui',
    children: [
      {
        id: 'wallet',
        title: 'Wallet',
        type: 'item',
        url: '/wallet/assets',
        classes: 'nav-item',
        icon: 'icon-trending-up'
      },
      {
        id: 'wallet-asset-add',
        title: 'Add asset',
        type: 'item',
        url: '/wallet/asset/add',
        classes: 'nav-item',
        icon: 'icon-plus-circle'
      },
      {
        id: 'wallet-asset-history',
        title: 'History',
        type: 'item',
        url: '/wallet/assets/history',
        classes: 'nav-item',
        icon: 'icon-book'
      },
    ]
  },
  {
    id: 'subscription',
    title: 'Subscriptions',
    type: 'group',
    icon: 'icon-ui',
    children: [
      {
        id: 'subscription',
        title: 'Subscriptions',
        type: 'item',
        url: '/subscriptions',
        classes: 'nav-item',
        icon: 'icon-bell'
      },
      {
        id: 'subscription-add',
        title: 'Create subscription',
        type: 'item',
        url: '/subscription/add',
        classes: 'nav-item',
        icon: 'icon-plus-circle'
      },
    ]
  },
  {
    id: 'forms',
    title: 'Forms & Tables',
    type: 'group',
    icon: 'icon-group',
    children: [
      {
        id: 'forms-element',
        title: 'Form Elements',
        type: 'item',
        url: '/forms/basic',
        classes: 'nav-item',
        icon: 'icon-file-text'
      },
      {
        id: 'tables',
        title: 'Tables',
        type: 'item',
        url: '/tables/bootstrap',
        classes: 'nav-item',
        icon: 'icon-server'
      }
    ]
  },
  {
    id: 'chart-maps',
    title: 'Chart',
    type: 'group',
    icon: 'icon-charts',
    children: [
      {
        id: 'apexChart',
        title: 'ApexChart',
        type: 'item',
        url: 'apexchart',
        classes: 'nav-item',
        icon: 'icon-pie-chart'
      }
    ]
  }
];
