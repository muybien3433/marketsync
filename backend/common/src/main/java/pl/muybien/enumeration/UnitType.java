package pl.muybien.enumeration;

public enum UnitType {
    UNIT,   // For generic units
    OZ,
    MWH,
    LBS,
    T,
    KG,
    MTU,
    BU,
    CWT,
    GAL,
    THM,
    BBL,
    MMBTU,
    DOZEN,
    THOUSAND_BOARD_FEET;

    public static UnitType extractUnit(String unit) {
        for (UnitType type : UnitType.values()) {
            if (unit.toUpperCase().contains(type.name())) return type;
        }
        return null;
    }
}