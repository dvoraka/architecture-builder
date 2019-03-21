package dvoraka.archbuilder.submodule.net;

import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.ResultData;
import dvoraka.archbuilder.sample.microservice.data.message.RequestMessage;
import dvoraka.archbuilder.sample.microservice.data.message.ResponseMessage;
import dvoraka.archbuilder.sample.microservice.net.BaseNetComponent;
import dvoraka.archbuilder.sample.microservice.net.ServiceNetComponent;
import dvoraka.archbuilder.sample.microservice.net.receive.NetReceiver;
import dvoraka.archbuilder.sample.microservice.server.AbstractServer;

public class DefaultNetConfig extends NetConfig {

    public DefaultNetConfig() {
        super(
                ResultData.class,
                BaseException.class,
                RequestMessage.class,
                ResponseMessage.class,
                ServiceNetComponent.class,
                NetReceiver.class,
                BaseNetComponent.class,
                AbstractServer.class
        );
    }
}
