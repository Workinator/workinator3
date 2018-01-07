
# Overview

# Workers

The point of the Workinator is to schedule and assign work. The "work" is whatever the application is supposed to do.

A worker does the worker. The application developer implements a `Worker` interface.

## Types of Workers

### WorkerAsync

Status: In Progress

Implementations of this intervace are exected by AsyncExecutors.

### WorkerSync

Status: less real than big foot

Implementations of this interface are executed by SyncExecutors.

# Executors

Each worker is managed by an executor. The executor retrieves assignments from the coordinator, and forwards it to the worker. It instructs the worker when to stop working.

The executor manages the worker. The worker does the work.

## Types of Executors

### Async Executor

Status: In Progress

The AsyncExecutor creates a thread, and executes the worker on that thread. Thus, all workers run on dedicated threads in parallel.

This is appropriate when your code is to run in a main thread.

The worker needs to watch the worker context to know when to stop working.

```
while (context.canContinue) {
   // work
}
```

### Sync Executor

Status: Saw it in a dream

The SyncExecutor does not create a thread per worker. This only appropriate if your code is using other code that starts it's own thread. For example: a RabbitMq consmer or Kafka Stream client. The worker would just set those up and run. They create their own threads and go.

The worker needs to catch Stop events from the worker context so that it knows when to stop.

```
context.onStop(c -> {
  // shut 'er down
});
```

Anything that can be done by SyncExecutor can also be done, less efficiently, by AsyncExecutor. I am focusing on AsyncExecutor to start.


# Rebalance Strategies

The strategies are implement at the repository level. This could, perhaps, be abstracted, but that's not a short-term priority.

The strategies are currently implemented using MongoDb.

## The "What's Next" Strategy

Status: In Progress

`mongo/WhatsNextRebalanceStrategy`

Given

* a number of partitions
* a number of consumers
* a number of executors per consumer

The total number of available threads is `(# of partitions) * (# of threads per consumer)`.

The number of executors could be significantly less than the number of partitions.

Each thread requests and assignment from the coordinator. If an assignment is given, then the executor starts working on that partition.


## The "Assignment" Strategy

Status: Thought of it. This was implemented in previous versions of workinator. It is not on the short-term list for workinator3 because the "What's Next" approach covers the same use cases.

Given

* a number of partitions
* a number of consumers

The number of partitions is divided by the number of consumers, and each consumer is assigned some of the partitions.

However, a consumer can only handle a certain number of consumers. Thus, if the sum of the max capacity of the consumers is less than the number of partitions, then not all partitions will be processed.

The assignments rebalance as consumers start and stop.

The `What's Next` strategy covers the same use cases. If the number of executors exceeds the number of partitions, then the result is the same as the `assignment` strategy.


