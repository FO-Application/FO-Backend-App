package com.fo_product.notification_service.configs;

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
        // Use ErrorHandlingDeserializer to handle deserialization errors
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        
        // Define delegate deserializers
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        
        // Type Mappings: Map remote class names (from producer) to local class names (in consumer)
        // Format: "RemoteClassName:LocalClassName, ..."
        props.put(JsonDeserializer.TYPE_MAPPINGS, 
            "com.fo_product.order_service.kafka.events.OrderCreatedEvent:com.fo_product.notification_service.events.OrderCreatedEvent," +
            "com.fo_product.order_service.kafka.events.OrderPaidEvent:com.fo_product.notification_service.events.OrderPaidEvent," +
            "com.fo_product.order_service.kafka.events.OrderDeliveringEvent:com.fo_product.notification_service.events.OrderDeliveringEvent," +
            "com.fo_product.order_service.kafka.events.OrderCancelledEvent:com.fo_product.notification_service.events.OrderCancelledEvent," +
            "com.fo_product.order_service.kafka.events.OrderReadyEvent:com.fo_product.notification_service.events.OrderReadyEvent," +
            "com.fo_product.shipper_service.events.ShipperFoundEvent:com.fo_product.notification_service.events.ShipperFoundEvent," +
            "com.fo_product.shipper_service.events.ShipperAssignedEvent:com.fo_product.notification_service.events.ShipperAssignedEvent," +
            "com.fo_product.wallet_service.events.WalletWithdrawalEvent:com.fo_product.notification_service.events.WalletWithdrawalEvent," +
            "com.fo_product.user_service.kafka.events.MailSenderEvent:com.fo_product.notification_service.events.MailSenderEvent," +
            "com.fo_product.merchant_service.kafka.events.RestaurantLifecycleEvent:com.fo_product.notification_service.events.RestaurantLifecycleEvent"
        );
        
        // Default type if mapping fails or header missing (optional, but good for robustness)
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.HashMap"); 

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        // Error Handler: Log and skip messages that fail deserialization
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            log.error("SKIPPING BAD MESSAGE | Topic: {} | Partition: {} | Offset: {} | Error: {}",
                    ((ConsumerRecord) record).topic(), 
                    ((ConsumerRecord) record).partition(), 
                    ((ConsumerRecord) record).offset(), 
                    exception.getMessage());
        }, new FixedBackOff(0L, 0L)); // 0 retries -> skip immediately
        
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
