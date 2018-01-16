package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.*;
import com.mongodb.MongoWriteException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.allardworks.workinator3.core.ConvertUtility.toDate;

@Service
@RequiredArgsConstructor
public class MongoRepository implements WorkinatorRepository {
    private static Document toBson(final ConsumerDao consumerDao) {
        val doc = new Document();
        doc.put("consumerId", consumerDao.getConsumerId());
        doc.put("registrationId", consumerDao.getConsumerRegistration());
        consumerDao.getMaxExecutorCount().ifPresent(c -> doc.put("maxExecutorCount", c.getValue()));
        return doc;
    }

    private static ConsumerDao toDao(final Document doc){
        val dao = new ConsumerDao();
        dao.getMaxExecutorCount().setValue(doc.getInteger("consumerId"));
        dao.setConsumerId(doc.getString("consumerId"));
        dao.setConsumerRegistration(doc.getString("consumerRegistration"));
        return dao;
    }


    @NonNull
    private final MongoDal dal;

    @NonNull
    private final RebalanceStrategy strategy;

    @Override
    public Assignment getAssignment(@NonNull final ExecutorId executorId) {
        return strategy.getNextAssignment(executorId);
    }

    @Override
    public void releaseAssignment(@NonNull final Assignment assignment) {
        strategy.releaseAssignment(assignment);
    }

    @Override
    public void createConsumer(@NonNull final ConsumerDao consumer) throws ConsumerExistsException {
        val doc = toBson(consumer);
        try {
            dal.getConsumersCollection().insertOne(doc);
        } catch (final MongoWriteException e) {
            if (e.getMessage().contains("E11000 duplicate key error collection")) {
                throw new ConsumerExistsException(consumer.getConsumerId());
            }

            // otherwise...
            throw e;
        }
    }
}
