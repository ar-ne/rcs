package ar.ne.rcs.shared.models.stores;

import ar.ne.rcs.shared.models.rc.Job;
import ar.ne.rcs.shared.models.rc.JobMetadata;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class JobStore {
    /**
     * generated uuid
     */
    String id;
    Job job;
    JobMetadata metadata;
    @Builder.Default
    ArrayList<ResultPartial> resultPartials = new ArrayList<>();
}
