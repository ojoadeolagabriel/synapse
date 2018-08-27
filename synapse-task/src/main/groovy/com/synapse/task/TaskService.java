package com.synapse.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synapse.task.config.KafkaSynapseConfig;
import com.synapse.task.context.EventState;
import com.synapse.task.context.MessagePipeline;
import com.synapse.task.event.CompletionEvent;
import com.synapse.task.event.Event;
import com.synapse.task.handler.KafkaTaskResponseHandler;
import com.synapse.task.handler.ProcessCompletionHandler;
import com.synapse.task.handler.SynapseTaskHandler;
import com.synapse.task.util.Constants;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

import static com.synapse.task.context.EventState.Closed;
import static com.synapse.task.context.EventState.Retry;
import static com.synapse.task.util.Constants.RESULT_POSTFIX;

@Slf4j
public class TaskService {
	public KafkaSynapseConfig config;
	private MessagePipeline messagePipeline;
	private static final Object lock = new Object();
	JDBCClient jdbcClient;

	TaskService(String bootStrapServers, String consumerGroup, JsonObject additionalConfig) {
		config = new KafkaSynapseConfig(bootStrapServers, consumerGroup);
		jdbcClient = JDBCClient.createShared(config.getVertx(), additionalConfig);
	}

	public void startTask(SynapseTaskHandler eventBuildHandler, ProcessCompletionHandler onProcessCompletionHandler) {
		Event event = eventBuildHandler.handle();
		synchronized (lock) {
			if (messagePipeline == null) {
				messagePipeline = new MessagePipeline(config);
				config.getVertx().deployVerticle(messagePipeline);
			}
		}

		try {
			String payload = Constants.mapper.writeValueAsString(event);
			config.kafkaResponseConsumer().subscribe(buildResponseTopic(event.getTopic()), handler -> {
				try {
					if (handler.succeeded()) {
						config.kafkaResponseConsumer().handler(record -> {
							EventState state = EventState.valueOf(record.value());
							CompletionEvent data = new CompletionEvent();
							data.setState(state);
							onProcessCompletionHandler.handle(data);

							System.out.println("processing completion to: " + record.topic());
							persistState(event.getKey(), Closed, event.getMessage());
							System.out.println("------------------------------------------");
						});
					}
				} catch (Exception e) {
					persistState(event.getKey(), Retry, event.getMessage());
				}
			});

			config.getVertx().eventBus().send(Constants.DEFAULT_MESSAGE_PIPELINE, payload, replyHandler -> {
				if (replyHandler.succeeded()) {
				} else {
					System.out.println("could not push: " + payload + " reason: " + replyHandler.cause().getMessage());
				}
			});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * task completion
	 *
	 * @param id
	 * @param completionHandler
	 */
	public void completeTask(String id, KafkaTaskResponseHandler completionHandler) {
		config.kafkaConsumer().subscribe(id, subscribe -> {
			if (subscribe.succeeded()) {

				//handler
				config.kafkaConsumer().handler(record -> {
					EventState result = completionHandler.handle(record.value());
					persistState(id, result, record.value());

					//replace with kafka
					config.kafkaProducer().write(KafkaProducerRecord.create(buildResponseTopic(record.topic()), record.key(), result.name()), handler -> {
						if (handler.succeeded()) {
							System.out.println("sending completion to: " + record.topic() + RESULT_POSTFIX);
						}
					});
				});
			}
		});
	}

	String buildResponseTopic(String topic) {
		return topic + RESULT_POSTFIX;
	}

	AtomicLong counter = new AtomicLong();
	private void persistState(String id, EventState result, String value) {
		System.out.println(String.format("persisting.. %s, %s, %s", id, result.name(), value));
		jdbcClient.getConnection(res -> {
			if (res.succeeded()) {
				SQLConnection connection = res.result();
				connection.execute(String.format("replace into task (name, status) values ('%s','%s')", "Dummy" , result.getValue()), insert -> {
					if (insert.succeeded()) {
						System.out.println("insert successful: " + counter.addAndGet(1));
					} else {
						System.out.println("insert failed: " + insert.cause().getMessage());
					}
				});
			}
		});
	}
}
