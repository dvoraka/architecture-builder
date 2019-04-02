package dvoraka.archbuilder.prototype.actioncoordinator.action;

public interface Action<PD> {

    void process();

    default void resume(PD data) {
        throw new UnsupportedOperationException();
    }
}
