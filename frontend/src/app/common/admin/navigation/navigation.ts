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
    title: 'common.wallet',
    type: 'group',
    icon: 'icon-ui',
    children: [
      {
        id: 'wallet',
        title: 'common.wallet',
        type: 'item',
        url: '/wallet/assets',
        classes: 'nav-item',
        icon: 'icon-trending-up'
      },
      {
        id: 'wallet-asset-add',
        title: 'asset.add.asset',
        type: 'item',
          url: '/wallet/asset/add',
        classes: 'nav-item',
        icon: 'icon-plus-circle'
      },
      {
        id: 'wallet-asset-history',
        title: 'common.history',
        type: 'item',
        url: '/wallet/assets/history',
        classes: 'nav-item',
        icon: 'icon-book'
      },
    ]
  },
  {
    id: 'subscription',
    title: 'subscription.subscriptions',
    type: 'group',
    icon: 'icon-ui',
    children: [
      {
        id: 'subscription',
        title: 'subscription.subscriptions',
        type: 'item',
        url: '/subscription/subscriptions',
        classes: 'nav-item',
        icon: 'icon-bell'
      },
      {
        id: 'subscription-add',
        title: 'subscription.add.subscription',
        type: 'item',
        url: '/subscription/add',
        classes: 'nav-item',
        icon: 'icon-plus-circle'
      },
    ]
  },
  {
    id: 'settings',
    title: 'navbar.preferences',
    type: 'group',
    icon: 'icon-ui',
    children: [
      {
        id: 'currency-preferences',
        title: 'common.currency',
        type: 'item',
        url: '/settings/currency',
        classes: 'nav-item',
        icon: 'icon-settings',
      },
    ]
  },
];
