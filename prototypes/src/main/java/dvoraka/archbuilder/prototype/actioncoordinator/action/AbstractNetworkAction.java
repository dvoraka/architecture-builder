package dvoraka.archbuilder.prototype.actioncoordinator.action;

import dvoraka.archbuilder.prototype.actioncoordinator.context.ActionContext;
import dvoraka.archbuilder.prototype.actioncoordinator.net.ClientNetComponent;
import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.ResultData;
import dvoraka.archbuilder.sample.microservice.data.message.Message;
import dvoraka.archbuilder.sample.microservice.data.message.ResultMessage;

import java.util.function.Consumer;

public abstract class AbstractNetworkAction<ID, D, PD, SC extends ActionContext<ID, D, PD>>
        extends AbstractAction<ID, D, PD, SC> {

    private volatile String corrId;


    protected AbstractNetworkAction(SC context) {
        super(context);
    }

    protected String getCorrId() {
        return corrId;
    }

    private void setCorrId(String corrId) {
        this.corrId = corrId;
    }

    protected <
            M extends Message,
            R extends ResultMessage<RD, E>,
            RD extends ResultData<E>,
            E extends BaseException,
            C extends ClientNetComponent<M, R, E, ?, RD>>
    void send(M message, C client, Consumer<R> callback) {
        setCorrId(message.getId());
        client.sendFast(message, callback);
    }

    protected void callback(ResultMessage<?, ?> resultMessage) {
        checkResultMessage(resultMessage);
    }

    protected void rollbackCallback(ResultMessage<?, ?> resultMessage) {
        checkRollbackResultMessage(resultMessage);
    }

    protected boolean checkId(ResultMessage<?, ?> resultMessage) {
        return resultMessage.getCorrId().equals(getCorrId());
    }

    protected void checkResultMessage(ResultMessage<?, ?> resultMessage) {
        checkResultMessage(resultMessage, false);
    }

    protected void checkRollbackResultMessage(ResultMessage<?, ?> resultMessage) {
        checkResultMessage(resultMessage, true);
    }

    private void checkResultMessage(ResultMessage<?, ?> resultMessage, boolean rollback) {
        if (!checkId(resultMessage)) {
            return;
        }

        log.debug("Received: {}", resultMessage);

        try {
            resultMessage.checkStatus();
            if (rollback) {
                getContext().rollbackDone();
            } else {
                getContext().actionDone();
            }
        } catch (BaseException e) {
            getContext().actionFailed();
        }
    }
}
