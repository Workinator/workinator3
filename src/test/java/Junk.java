import com.allardworks.workinator3.contracts.PartitionDto;
import com.allardworks.workinator3.contracts.PartitionExistsException;
import com.allardworks.workinator3.mongo.MongoAdminRepository;
import com.allardworks.workinator3.mongo.MongoConfiguration;
import com.allardworks.workinator3.mongo.MongoDal;
import com.mongodb.MongoClient;
import lombok.val;
import org.junit.Test;

public class Junk {
    @Test
    public void createDatabase() throws PartitionExistsException {
        val config = MongoConfiguration.builder().partitionType("yadda").build();
        val client = new MongoClient(config.getHost(), config.getPort());
        client.dropDatabase(config.getDatabaseName());
        val dal = new MongoDal(config);
        val repo = new MongoAdminRepository(dal);

        val partition = new PartitionDto();
        partition.setPartitionKey("test");
        repo.create(partition);
    }
}
