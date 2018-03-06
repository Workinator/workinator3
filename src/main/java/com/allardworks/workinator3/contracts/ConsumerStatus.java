package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.commands.UnregisterConsumerCommand;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jaya on 3/5/18.
 * k?
 */
@Data
public class ConsumerStatus {
    private final Workinator workinator;
    private final ConsumerId id;

    private ConsumerRegistration registration;
    private final List<WorkerStatus> workers = new ArrayList<>();
    private final Date connectDate = new Date();
    private Date registrationDate;

    public int getWorkerCount() {
        return workers.size();
    }

    public void register() throws ConsumerExistsException {
        registrationDate = new Date();
        registration = workinator.registerConsumer(RegisterConsumerCommand.builder().id(id).build());
    }

    public void unregister() {
        workinator.unregisterConsumer(new UnregisterConsumerCommand(registration));
        registration = null;
    }


}
