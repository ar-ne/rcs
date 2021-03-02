package ar.ne.rcs.shared.models.rc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobLifecycle {
    ADDED("Added"),
    WAITING("Waiting"),
    RUNNING("Running"),
    FINISHED("Finished");

    private final String value;
}
