package dvoraka.architecturebuilder;

/**
 * Directory type.
 */
public enum DirType {

    ROOT,

    SRC_ROOT,
    SRC_TEST_ROOT,

    SRC_BASE,
    SRC_TEST_BASE,

    SRC_PROPERTIES,
    SRC_TEST_PROPERTIES,

    SERVICE_ABSTRACT(true),
    SERVICE,
    SERVICE_IMPL;

    private final boolean abstractType;


    DirType() {
        this(false);
    }

    DirType(boolean abstractType) {
        this.abstractType = abstractType;
    }

    public boolean isAbstractType() {
        return abstractType;
    }
}
