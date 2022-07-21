package com.tweetapp.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public final class ConsumerService {
@KafkaListener(topics = "appLogs")
public void consume(String message) {
		System.out.println("Received Messasge in group - group-id: " + message);
	}
}