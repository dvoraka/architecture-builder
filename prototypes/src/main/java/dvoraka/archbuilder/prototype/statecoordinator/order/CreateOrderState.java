package dvoraka.archbuilder.prototype.statecoordinator.order;

public enum CreateOrderState {

    INIT {
        @Override
        CreateOrderState getNext() {
            return COMPLETE;
        }

        @Override
        CreateOrderState getPrevious() {
            return END;
        }
    },
    COMPLETE {
        @Override
        CreateOrderState getNext() {
            return END;
        }

        @Override
        CreateOrderState getPrevious() {
            return INIT;
        }
    },
    END {
        @Override
        CreateOrderState getNext() {
            return null;
        }

        @Override
        CreateOrderState getPrevious() {
            return null;
        }
    };

    abstract CreateOrderState getNext();

    abstract CreateOrderState getPrevious();
}
