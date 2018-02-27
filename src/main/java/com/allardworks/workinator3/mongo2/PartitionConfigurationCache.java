package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.consumer.StupidCache;
import com.allardworks.workinator3.contracts.PartitionConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Created by jaya on 2/25/18.
 * Retrieves partition configuration objects from the database,
 * and stores them for up to 5 minutes.
 * Objects are lazy loaded one at a time as needed.
 */
@Component
public class PartitionConfigurationCache {
    private final MongoDal dal;
    private final StupidCache<String, PartitionConfiguration> partitionConfigurationStupidCache;

    public PartitionConfigurationCache(final MongoDal dal) {
        this.dal = dal;
        partitionConfigurationStupidCache = new StupidCache<>(dal::getPartitionConfiguration);
    }

    /**
     * Caches the partition configuration objects for 5 minutes each.
     */
    public PartitionConfiguration getConfiguration(final String partitionKey) {
        return partitionConfigurationStupidCache.getItem(partitionKey);
    }
}
