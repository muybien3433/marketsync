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
  // {
  //   id: 'navigation',
  //   title: 'Home',
  //   type: 'group',
  //   icon: 'icon-navigation',
  //   children: [
  //     {
  //       id: 'wallet',
  //       title: 'Wallet',
  //       type: 'item',
  //       url: '/wallet',
  //       icon: 'feather icon-home',
  //       classes: 'nav-item'
  //     }
  //   ]
  // },
  {
    id: 'navigation',
    title: 'Navigation',
    type: 'group',
    icon: 'icon-ui',
    children: [
      {
        id: 'wallet',
        title: 'Wallet',
        type: 'collapse',
        icon: 'feather icon-box',
        children: [
          {
            id: 'wallet-asset-add',
            title: 'Add asset',
            type: 'item',
            url: '/assets/add'
          },
          {
            id: 'wallet-asset-history',
            title: 'History',
            type: 'item',
            url: '/assets/history'
          }
        ]
      },
      {
        id: 'subscription',
        title: 'Subscription',
        type: 'collapse',
        icon: 'feather icon-box',
        children: [
          {
            id: 'subscription-add',
            title: 'Add subscription',
            type: 'item',
            url: '/subscriptions/add'
          },
        ]
      },
      {
        id: 'api',
        title: 'API',
        type: 'item',
        url: 'javascript:',
        classes: 'nav-item disabled',
        icon: 'feather icon-power',
        external: true
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
        icon: 'feather icon-file-text'
      },
      {
        id: 'tables',
        title: 'Tables',
        type: 'item',
        url: '/tables/bootstrap',
        classes: 'nav-item',
        icon: 'feather icon-server'
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
        icon: 'feather icon-pie-chart'
      }
    ]
  }
];
