package pl.muybien.finance;

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

    public static UnitType fromString(String type) {
        if (type != null) {
            try {
                return UnitType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unit unitType " + type + " not supported");
            }
        }
        throw new IllegalArgumentException("Unit unitType is null");
    }
}