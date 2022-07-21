package com.tweetapp.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public final class ProducerService {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private static final String TOPIC = "appLogs";

	public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(String message) {
		this.kafkaTemplate.send(TOPIC, message);
	}
}