package dvoraka.archbuilder.prototype.actioncoordinator.order;

public enum CreateOrderAction {

    INIT {
        @Override
        CreateOrderAction getNext() {
            return CHECK;
        }

        @Override
        CreateOrderAction getPrevious() {
            return END;
        }
    },
    CHECK {
        @Override
        CreateOrderAction getNext() {
            return COMPLETE;
        }

        @Override
        CreateOrderAction getPrevious() {
            return INIT;
        }
    },
    COMPLETE {
        @Override
        CreateOrderAction getNext() {
            return END;
        }

        @Override
        CreateOrderAction getPrevious() {
            return CHECK;
        }
    },
    END {
        @Override
        CreateOrderAction getNext() {
            return null;
        }

        @Override
        CreateOrderAction getPrevious() {
            return null;
        }
    };

    abstract CreateOrderAction getNext();

    abstract CreateOrderAction getPrevious();
}
