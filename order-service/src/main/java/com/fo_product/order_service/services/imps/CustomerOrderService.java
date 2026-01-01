package com.fo_product.order_service.services.imps;

import com.fo_product.order_service.clients.MerchantClient;
import com.fo_product.order_service.clients.UserClient;
import com.fo_product.order_service.dtos.feigns.OptionItemDTO;
import com.fo_product.order_service.dtos.feigns.ProductDTO;
import com.fo_product.order_service.dtos.feigns.RestaurantDTO;
import com.fo_product.order_service.dtos.feigns.UserDTO;
import com.fo_product.order_service.dtos.requests.OrderItemRequest;
import com.fo_product.order_service.dtos.requests.OrderRequest;
import com.fo_product.order_service.dtos.requests.UpdateOrderStatusRequest;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.exceptions.OrderException;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;
import com.fo_product.order_service.kafka.KafkaProducerService;
import com.fo_product.order_service.kafka.events.OrderCompletedEvent;
import com.fo_product.order_service.kafka.events.OrderConfirmedEvent;
import com.fo_product.order_service.kafka.events.OrderDeliveringEvent;
import com.fo_product.order_service.mappers.OrderMapper;
import com.fo_product.order_service.models.entities.Order;
import com.fo_product.order_service.models.entities.OrderItem;
import com.fo_product.order_service.models.entities.OrderItemOption;
import com.fo_product.order_service.models.enums.OrderStatus;
import com.fo_product.order_service.models.enums.PaymentMethod;
import com.fo_product.order_service.models.repositories.OrderRepository;
import com.fo_product.order_service.services.interfaces.ICustomerOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerOrderService implements ICustomerOrderService {
    OrderRepository orderRepository;
    UserClient userClient;
    MerchantClient merchantClient;
    OrderMapper mapper;

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        UserDTO user = userClient.getUserById(userId);

        RestaurantDTO restaurant = merchantClient.getRestaurant(request.merchantId());

        List<Long> productIds = request.items().stream().map(OrderItemRequest::productId).toList();

        List<ProductDTO> products = merchantClient.getAllProductsByIds(productIds);

        Map<Long, ProductDTO> productMap = products.stream().collect(Collectors.toMap(ProductDTO::id, Function.identity()));

        Order order = Order.builder()
                .userId(userId)
                .customerName(user.firstName() + " " + user.lastName())
                .customerPhone(user.phone())
                .customerEmail(user.email())
                .deliveryAddress(request.deliveryAddress())
                .merchantId(request.merchantId())
                .merchantName(restaurant.name())
                .merchantLogo(restaurant.imageFileUrl())
                .orderStatus(OrderStatus.CREATED)
                .paymentMethod(PaymentMethod.valueOf(request.paymentMethod()))
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            ProductDTO productDTO = productMap.get(itemRequest.productId());

            if (productDTO == null)
                throw new OrderException(OrderErrorCode.PRODUCT_NOT_EXIST);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(productDTO.id())
                    .productName(productDTO.name())
                    .productImage(productDTO.imageUrl())
                    .unitPrice(productDTO.price())
                    .quantity(itemRequest.quantity())
                    .build();

            List<OrderItemOption> itemOptions = new ArrayList<>();
            BigDecimal totalOptionPrice = BigDecimal.ZERO;

            if (itemRequest.optionIds() != null && !itemRequest.optionIds().isEmpty()) {
                Map<Long, OptionItemDTO> optionMap = productDTO.optionGroups().stream()
                        .filter(og -> og.options() != null) // Skip qua option group rỗng
                        .flatMap(og -> og.options().stream())// Gom tất cả List<OptionGroup> thành một stream to
                        .collect(Collectors.toMap(
                                OptionItemDTO::id, //Key: id Option
                                Function.identity(), //Value: Đối tượng OptionGroupDTO
                                (existing, replacement) -> existing) //So sánh nếu id đã tồn tại/ trùng thì giữ cái cũ
                        );

                for (Long optionId : itemRequest.optionIds()) {
                    OptionItemDTO optionItemDTO = optionMap.get(optionId);

                    if (optionItemDTO != null) {
                        if (!optionItemDTO.isAvailable())
                            throw new OrderException(OrderErrorCode.TOPPING_OUT_OF_STOCK);

                        OrderItemOption orderItemOption = OrderItemOption.builder()
                                .orderItem(orderItem)
                                .optionGroupName(optionItemDTO.optionGroupName())
                                .optionName(optionItemDTO.name())
                                .priceAdjustment(optionItemDTO.priceAdjustment())
                                .build();

                        itemOptions.add(orderItemOption);
                        totalOptionPrice = totalOptionPrice.add(optionItemDTO.priceAdjustment());
                    } else {
                        throw new OrderException(OrderErrorCode.INVALID_OPTION_ID);
                    }
                }
            }
            orderItem.setOrderItemOptions(itemOptions);

            //Thuật toán tính tiền cho 1 món ăn
            //Công thức: (Giá món + Tổng giá Topping đi kèm nếu có) * Số lượng
            BigDecimal singleOrderItemPrice = productDTO.price().add(totalOptionPrice);
            BigDecimal singleOrderItemTotal = singleOrderItemPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));

            orderItem.setTotalPrice(singleOrderItemTotal);

            subTotal = subTotal.add(singleOrderItemTotal);
            orderItems.add(orderItem);
        }
        //Chốt đơn
        order.setOrderItems(orderItems);
        order.setSubTotal(subTotal);

        //Hardcore tạm phí ship hàng và phiếu giảm giá ở đây(Tương lai phát triển sau)
        order.setShippingFee(BigDecimal.valueOf(25000));
        order.setDiscountAmount(BigDecimal.ZERO);

        //Tổng đơn hàng = Tiền đồ ăn + tiền ship - tiền giảm giá
        BigDecimal grandTotal = subTotal.add(order.getShippingFee()).subtract(order.getDiscountAmount());
        order.setGrandTotal(grandTotal);

        Order result = orderRepository.save(order);
        log.info("Tạo thành công Order ID: {} - Tổng tiền: {}", result.getId(), result.getGrandTotal());

        return mapper.response(result);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Long userId, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> result = orderRepository.findByUserId(userId, pageable);

        return result.map(mapper::response);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        if (!order.getUserId().equals(userId))
            throw new OrderException(OrderErrorCode.ORDER_INVALID);

        return mapper.response(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        if (!order.getUserId().equals(userId))
            throw new OrderException(OrderErrorCode.ORDER_INVALID);

        if (order.getOrderStatus() != OrderStatus.CREATED)
            throw new OrderException(OrderErrorCode.CANNOT_CANCEL_ORDER);

        order.setOrderStatus(OrderStatus.CANCELED);
        Order result = orderRepository.save(order);

        return mapper.response(result);
    }
}
