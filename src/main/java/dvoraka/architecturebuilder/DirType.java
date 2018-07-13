package dvoraka.architecturebuilder;

/**
 * Directory type.
 */
public enum DirType {

    ROOT,

    SRC_ROOT,
    SRC_TEST_ROOT,

    SRC_BASE(false, true),
    SRC_TEST_BASE(false, true),

    SRC_PROPERTIES,
    SRC_TEST_PROPERTIES,

    SERVICE_ABSTRACT(true, false),
    SERVICE,
    SERVICE_IMPL;

    private final boolean abstractType;
    private final boolean baseType;


    DirType() {
        this(false, false);
    }

    DirType(boolean abstractType, boolean baseType) {
        this.abstractType = abstractType;
        this.baseType = baseType;
    }

    public boolean isAbstractType() {
        return abstractType;
    }

    public boolean isBaseType() {
        return baseType;
    }
}
