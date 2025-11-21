package com.fo_product.notification_service.configs;

import com.fo_product.notification_service.events.MailSenderEvent;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Bean
    public ConsumerFactory<String, MailSenderEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        // 2. Cấu hình JSON Deserializer
        JsonDeserializer<MailSenderEvent> jsonDeserializer = new JsonDeserializer<>(MailSenderEvent.class, false);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false); // Bỏ qua header, chỉ map theo nội dung JSON

        // 3. Cấu hình ErrorHandlingDeserializer (QUAN TRỌNG: Chống lỗi vòng lặp chết)
        // Nếu deserialize lỗi, nó sẽ trả về null thay vì ném exception làm crash app
        ErrorHandlingDeserializer<MailSenderEvent> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                errorHandlingDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MailSenderEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MailSenderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
