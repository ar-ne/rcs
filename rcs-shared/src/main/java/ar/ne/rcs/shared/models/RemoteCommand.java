package ar.ne.rcs.shared.models;

import lombok.Data;

import java.util.Date;

@Data
public class RemoteCommand {
    public int id;
    public String targetIMEI;
    public int status;
    public Integer code;
    public String command;
    public String result;
    public Date schedule;
}
