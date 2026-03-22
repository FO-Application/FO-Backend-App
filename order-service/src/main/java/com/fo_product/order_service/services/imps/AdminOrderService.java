package com.fo_product.order_service.services.imps;

import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.mappers.OrderMapper;
import com.fo_product.order_service.models.entities.Order;
import com.fo_product.order_service.models.enums.OrderStatus;
import com.fo_product.order_service.models.repositories.OrderRepository;
import com.fo_product.order_service.services.interfaces.IAdminOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminOrderService implements IAdminOrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (status != null && !status.isEmpty()) {
            return orderRepository.findByOrderStatus(OrderStatus.valueOf(status), pageable)
                    .map(orderMapper::response);
        } else {
            return orderRepository.findAll(pageable)
                    .map(orderMapper::response);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public com.fo_product.order_service.dtos.responses.DashboardStatsResponse getDashboardStats(int days) {
        java.time.LocalDateTime startDate = java.time.LocalDateTime.now().minusDays(days - 1).with(java.time.LocalTime.MIN);
        java.util.List<Order> orders = orderRepository.findByCreatedAtAfter(startDate);
        
        java.util.Map<java.time.LocalDate, java.util.List<Order>> ordersByDate = orders.stream()
                .collect(java.util.stream.Collectors.groupingBy(o -> o.getCreatedAt().toLocalDate()));

        java.util.List<com.fo_product.order_service.dtos.responses.DailyStatResponse> chartData = new java.util.ArrayList<>();
        
        // Ensure all days are present even if no orders
        for (int i = days - 1; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
            java.util.List<Order> dailyOrders = ordersByDate.getOrDefault(date, java.util.Collections.emptyList());
            
            long count = dailyOrders.size();
            java.math.BigDecimal revenue = dailyOrders.stream()
                    .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                    .map(o -> o.getGrandTotal() != null ? o.getGrandTotal() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            String name = formatDayOfWeek(date);
            
            chartData.add(com.fo_product.order_service.dtos.responses.DailyStatResponse.builder()
                    .name(name)
                    .orders(count)
                    .revenue(revenue)
                    .build());
        }

        return com.fo_product.order_service.dtos.responses.DashboardStatsResponse.builder()
                .chartData(chartData)
                .build();
    }

    private String formatDayOfWeek(java.time.LocalDate date) {
        if (date.equals(java.time.LocalDate.now())) return "Hôm nay";
        int value = date.getDayOfWeek().getValue();
        return value == 7 ? "CN" : "Thứ " + (value + 1);
    }
}
