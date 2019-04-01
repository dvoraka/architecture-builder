package dvoraka.archbuilder.prototype.statecoordinator.order;

public enum CreateOrderState {

    INIT {
        @Override
        CreateOrderState getNext() {
            return CHECK;
        }

        @Override
        CreateOrderState getPrevious() {
            return END;
        }
    },
    CHECK {
        @Override
        CreateOrderState getNext() {
            return COMPLETE;
        }

        @Override
        CreateOrderState getPrevious() {
            return INIT;
        }
    },
    COMPLETE {
        @Override
        CreateOrderState getNext() {
            return END;
        }

        @Override
        CreateOrderState getPrevious() {
            return CHECK;
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
