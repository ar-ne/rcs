package ar.ne.rcs.server.serivce;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
abstract class BaseService {
    protected final JmsTemplate jmsTemplate;

    public BaseService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
