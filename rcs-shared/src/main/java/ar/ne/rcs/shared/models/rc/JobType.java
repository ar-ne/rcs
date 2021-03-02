package ar.ne.rcs.shared.models.rc;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum JobType {
    SHELL_COMMAND("Shell command"),
    PREDEFINED_FUNCTION("Predefined function");

    String value;
}
