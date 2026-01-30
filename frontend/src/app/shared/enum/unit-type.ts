export enum UnitType {
    UNIT = 'UNIT',
    OZ = 'OZ',
    MWH = 'MWH',
    LBS = 'LBS',
    T = 'T',
    KG = 'KG',
    MTU = 'MTU',
    BU = 'BU',
    CWT = 'CWT',
    GAL = 'GAL',
    THM = 'THM',
    BBL = 'BBL',
    MMBTU = 'MMBTU',
    DOZEN = 'DOZEN',
    THOUSAND_BOARD_FEET = 'THOUSAND_BOARD_FEET'
  }
  
  export const UnitTypeLabels: { [key in UnitType]: string } = {
    [UnitType.UNIT]: 'unit',
    [UnitType.OZ]: 'oz',
    [UnitType.MWH]: 'MWh',
    [UnitType.LBS]: 'lbs',
    [UnitType.T]: 't',
    [UnitType.KG]: 'kg',
    [UnitType.MTU]: 'mtu',
    [UnitType.BU]: 'bu',
    [UnitType.CWT]: 'cwt',
    [UnitType.GAL]: 'gal',
    [UnitType.THM]: 'thm',
    [UnitType.BBL]: 'bbl',
    [UnitType.MMBTU]: 'MMBtu',
    [UnitType.DOZEN]: 'dz',
    [UnitType.THOUSAND_BOARD_FEET]: 'MBF'
};
  