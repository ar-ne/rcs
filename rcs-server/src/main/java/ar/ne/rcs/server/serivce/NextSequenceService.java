package ar.ne.rcs.server.serivce;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class NextSequenceService {
    private final MongoOperations mongo;

    public NextSequenceService(MongoOperations mongo) {
        this.mongo = mongo;
    }

    public int getNextSequence(String seqName) {
        CustomSequences counter = mongo.findAndModify(
                query(where("_id").is(seqName)),
                new Update().inc("seq", 1),
                options().returnNew(true).upsert(true),
                CustomSequences.class);
        return counter.getSeq();
    }

    @Document(collection = "customSequences")
    @Getter
    public static class CustomSequences {
        @Id
        private String id;
        private int seq;
    }
}