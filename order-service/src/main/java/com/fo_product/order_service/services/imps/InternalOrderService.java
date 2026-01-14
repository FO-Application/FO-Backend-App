package com.fo_product.order_service.services.imps;

import com.fo_product.order_service.exceptions.OrderException;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;
import com.fo_product.order_service.models.entities.Order;
import com.fo_product.order_service.models.enums.OrderStatus;
import com.fo_product.order_service.models.repositories.OrderRepository;
import com.fo_product.order_service.services.interfaces.IInternalOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalOrderService implements IInternalOrderService {
    OrderRepository orderRepository;

    @Override
    @Transactional
    public void updateAppTransId(Long orderId, String appTransId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));
        order.setAppTransId(appTransId);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOrderStatusByAppTransId(String appTransId, String status) {
        Order order = orderRepository.findByAppTransId(appTransId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_WITH_APP_TRANS_ID));

        try {
            order.setOrderStatus(OrderStatus.valueOf(status));
        } catch (Exception e) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
        orderRepository.save(order);
    }
}
