package dvoraka.archbuilder.data;

/**
 * Directory type.
 */
public enum DirType {

    ROOT,

    SRC_ROOT,
    SRC_TEST_ROOT,

    SRC_BASE(false, true),
    SRC_BASE_ABSTRACT(true, true),
    SRC_TEST_BASE(false, true),

    SERVICE_ABSTRACT(true, false),
    SERVICE,
    SERVICE_IMPL,

    CUSTOM_TYPE,
    NEW_TYPE,
    SPRING_CONFIG,

    TEXT,

    /**
     * Currently redundant because you can specify the supertype directly.
     *
     * @see Directory.Builder#superType(Class)
     */
    ABSTRACT(true, false),
    IMPL;

    private final boolean abstractType;
    private final boolean baseType;


    DirType() {
        this(false, false);
    }

    DirType(boolean abstractType, boolean baseType) {
        this.abstractType = abstractType;
        this.baseType = baseType;
    }

    public boolean isAbstract() {
        return abstractType;
    }

    public boolean isBase() {
        return baseType;
    }
}
