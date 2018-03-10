package com.allardworks.workinator3.httpapi;

import com.allardworks.workinator3.contracts.ConsumerStatus;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
@Data
@Document(collection = "Consumers")
public class Consumer {
    private ConsumerStatus status;
    private String name;
    private Date connectDate;
    private int maxWorkerCount;
}
