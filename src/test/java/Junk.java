public class Junk {
    /*@Ignore
    @Test
    public void createDatabase() throws PartitionExistsException {
        val config = MongoConfiguration.builder().partitionType("yadda").build();
        val client = new MongoClient(config.getHost(), config.getPort());
        client.dropDatabase(config.getDatabaseName());
        val dal = new MongoDal(config);
        val repo = new MongoAdminRepository(dal);

        val partition = new PartitionDao();
        partition.getMaxWorkerCount().setValue(1);
        partition.setPartitionKey("test");
        repo.createPartition(partition);
    }*/
}
