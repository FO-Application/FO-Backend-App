package com.fo_product.delivery_service.configs;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        // Use ErrorHandlingDeserializer
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        
        // Type Mappings for Delivery Service
        // Mapping OrderConfirmedEvent from order-service to local event
        props.put(JsonDeserializer.TYPE_MAPPINGS, 
            "com.fo_product.order_service.kafka.events.OrderConfirmedEvent:com.fo_product.delivery_service.kafka.events.OrderConfirmedEvent"
        );
        
        // Robustness: Default type if mapping fails
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.HashMap"); 

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        // Error Handler: Log and skip messages that fail deserialization
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            log.error("❌ SKIPPING BAD MESSAGE | Topic: {} | Partition: {} | Offset: {} | Error: {}", 
                    ((ConsumerRecord) record).topic(), 
                    ((ConsumerRecord) record).partition(), 
                    ((ConsumerRecord) record).offset(), 
                    exception.getMessage());
        }, new FixedBackOff(0L, 0L)); // 0 retries -> skip immediately
        
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
