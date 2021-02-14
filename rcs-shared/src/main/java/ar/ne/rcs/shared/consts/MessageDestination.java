package ar.ne.rcs.shared.consts;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants()
public class MessageDestination {
    private String COMMAND_CREATE;
    private String COMMAND_UPDATE_RESULT;
    private String COMMAND_UPDATE_META;
    private String DEVICE_REGISTRATION;
    private String DEVICE_OFFLINE;

    private MessageDestination() {
    }

}
