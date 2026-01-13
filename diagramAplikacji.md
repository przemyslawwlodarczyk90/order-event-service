```mermaid
classDiagram

%% ============================================
%% APPLICATION ENTRY POINT
%% ============================================
class OrderEventServiceApplication {
+main(String[]) void
}

    %% ============================================
    %% CONTROLLERS
    %% ============================================
    class AdminSettingsController {
        -SystemSettingsService service
        +getSettings() SystemSettings
        +updateSettings(SettingsDto) void
    }

    class OrderEventQueryController {
        -OrderEventFacade facade
        +getOrderEvents(String) ResponseEntity~List~OrderEvent~~
        +getAllOrderEvents() ResponseEntity~List~OrderEvent~~
    }

    class OrderReceiverController {
        -OrderEventKafkaProducer producer
        +receiveOrderEvent(OrderRequestDto) ResponseEntity~Void~
    }

    class OrderStatusUpdateController {
        -OrderEventKafkaProducer producer
        +markOrderAsPackedAndShipped(String) ResponseEntity~Void~
        +markOrderAsOutForDelivery(String) ResponseEntity~Void~
    }

    %% ============================================
    %% KAFKA
    %% ============================================
    class OrderEventKafkaProducer {
        -KafkaTemplate~String,Object~ kafkaTemplate
        +sendOrderEvent(String, Object) void
    }

    class OrderEventKafkaConsumer {
        -OrderEventFacade facade
        +consume(ConsumerRecord~String,Object~) void
    }

    %% ============================================
    %% DOMAIN
    %% ============================================
    class OrderStatus {
        <<enumeration>>
        ORDER_ACCEPTED
        PACKED_AND_SHIPPED
        OUT_FOR_DELIVERY
    }

    %% ============================================
    %% DTOs
    %% ============================================
    class OrderRequestDto {
        -String shipmentNumber
        -String recipientEmail
        -String recipientCountryCode
        -String senderCountryCode
        -int statusCode
    }

    class OrderStatusUpdateDto {
        -String shipmentNumber
        -OrderStatus status
    }

    class SettingsDto {
        -int consumerConcurrency
        -boolean emailEnabled
        -int emailRatePerSecond
    }

    %% ============================================
    %% ENTITY
    %% ============================================
    class OrderEvent {
        -Long id
        -String shipmentNumber
        -String recipientEmail
        -String recipientCountryCode
        -String senderCountryCode
        -int statusCode
        -Instant receivedAt
    }

    %% ============================================
    %% MAPPER
    %% ============================================
    class OrderEventMapper {
        +toEntity(OrderRequestDto) OrderEvent
    }

    %% ============================================
    %% NOTIFICATION
    %% ============================================
    class EmailNotificationFacade {
        -OrderAcceptedEmailHandler acceptedHandler
        -PackedAndShippedEmailHandler packedHandler
        -OutForDeliveryEmailHandler outHandler
        +sendOrderAccepted(OrderEvent) void
        +sendPackedAndShipped(OrderEvent) void
        +sendOutForDelivery(OrderEvent) void
    }

    class EmailNotificationHandler {
        <<interface>>
        +send(OrderEvent) void
    }

    class OrderAcceptedEmailHandler {
        +send(OrderEvent) void
    }

    class PackedAndShippedEmailHandler {
        +send(OrderEvent) void
    }

    class OutForDeliveryEmailHandler {
        +send(OrderEvent) void
    }

    %% ============================================
    %% REPOSITORIES
    %% ============================================
    class OrderEventRepository {
        <<interface>>
        +findByShipmentNumber(String) Optional~OrderEvent~
        +findTopByShipmentNumberOrderByReceivedAtDesc(String) Optional~OrderEvent~
        +findAllByShipmentNumberOrderByReceivedAtAsc(String) List~OrderEvent~
    }

    class SystemSettingsRepository {
        <<interface>>
        +findAndLockById1() Optional~SystemSettings~
    }

    %% ============================================
    %% SERVICE – FACADE
    %% ============================================
    class OrderEventFacade {
        -CreateOrderEventHandler createHandler
        -PackAndShipOrderHandler packHandler
        -OutForDeliveryOrderHandler outForDeliveryHandler
        -EmailNotificationFacade notificationFacade
        -GetOrderEventsHandler getOrderEventsHandler
        -GetAllOrderEventsHandler getAllOrderEventsHandler
        +processOrderEvent(OrderRequestDto) void
        +processStatusUpdate(OrderStatusUpdateDto) void
        +markOrderAsPackedAndShipped(String) void
        +markOrderAsOutForDelivery(String) void
        +getOrderEvents(String) List~OrderEvent~
        +getAllOrderEvents() List~OrderEvent~
    }

    %% ============================================
    %% SERVICE HANDLERS
    %% ============================================
    class CreateOrderEventHandler {
        -OrderEventRepository repository
        -OrderEventMapper mapper
        -Validator validator
        +handle(OrderRequestDto) OrderEvent
    }

    class GetAllOrderEventsHandler {
        -OrderEventRepository repository
        +handle() List~OrderEvent~
    }

    class GetOrderEventsHandler {
        -OrderEventRepository repository
        -Validator validator
        +handle(String) List~OrderEvent~
    }

    class PackAndShipOrderHandler {
        -OrderEventRepository repository
        -Validator validator
        +handle(String) Optional~OrderEvent~
    }

    class OutForDeliveryOrderHandler {
        -OrderEventRepository repository
        -Validator validator
        +handle(String) Optional~OrderEvent~
    }

    %% ============================================
    %% SETTINGS – RUNTIME CONFIGURATION
    %% ============================================
    class SettingsCache {
        -SystemSettingsRepository repository
        -SystemSettings cachedSettings
        +init() void
        +refresh() void
        +get() SystemSettings
    }

    class SettingsInitializer {
        -SystemSettingsRepository repository
        +run(String[]) void
    }

    class SystemSettings {
        -Long id
        -int consumerConcurrency
        -boolean emailEnabled
        -int emailRatePerSecond
        -Long version
    }

    class SystemSettingsService {
        -SystemSettingsRepository repository
        +get() SystemSettings
        +update(SettingsDto) void
    }

    %% ============================================
    %% VALIDATOR
    %% ============================================
    class Validator {
        +validateOrderDoesNotExist(String, Optional~OrderEvent~) void
        +validateInitialStatusAllowed(String, int) void
        +validateOrderExists(String, Optional~OrderEvent~) OrderEvent
        +validateAlreadyPackedAndShipped(String, int) boolean
        +validateCanBeOutForDelivery(String, int) void
    }

    %% ============================================
    %% EXCEPTIONS
    %% ============================================
    class InvalidOrderStatusTransitionException {
        +InvalidOrderStatusTransitionException(String)
    }

    class OrderNotFoundException {
        +OrderNotFoundException(String)
    }

    %% ============================================
    %% RELATIONSHIPS – APPLICATION
    %% ============================================
    OrderEventServiceApplication ..> AdminSettingsController
    OrderEventServiceApplication ..> OrderEventQueryController
    OrderEventServiceApplication ..> OrderReceiverController
    OrderEventServiceApplication ..> OrderStatusUpdateController

    %% ============================================
    %% RELATIONSHIPS – CONTROLLERS
    %% ============================================
    AdminSettingsController --> SystemSettingsService
    AdminSettingsController ..> SettingsDto
    AdminSettingsController ..> SystemSettings

    OrderEventQueryController --> OrderEventFacade
    OrderEventQueryController ..> OrderEvent

    OrderReceiverController --> OrderEventKafkaProducer
    OrderReceiverController ..> OrderRequestDto

    OrderStatusUpdateController --> OrderEventKafkaProducer
    OrderStatusUpdateController ..> OrderStatusUpdateDto
    OrderStatusUpdateController ..> OrderStatus

    %% ============================================
    %% RELATIONSHIPS – KAFKA
    %% ============================================
    OrderEventKafkaConsumer --> OrderEventFacade
    OrderEventKafkaConsumer ..> OrderRequestDto
    OrderEventKafkaConsumer ..> OrderStatusUpdateDto
    OrderEventKafkaConsumer ..> InvalidOrderStatusTransitionException

    OrderEventKafkaProducer ..> OrderRequestDto
    OrderEventKafkaProducer ..> OrderStatusUpdateDto

    %% ============================================
    %% RELATIONSHIPS – DOMAIN / DTO
    %% ============================================
    OrderStatusUpdateDto --> OrderStatus

    %% ============================================
    %% RELATIONSHIPS – MAPPER
    %% ============================================
    OrderEventMapper ..> OrderRequestDto
    OrderEventMapper ..> OrderEvent

    %% ============================================
    %% RELATIONSHIPS – NOTIFICATION
    %% ============================================
    EmailNotificationFacade --> OrderAcceptedEmailHandler
    EmailNotificationFacade --> PackedAndShippedEmailHandler
    EmailNotificationFacade --> OutForDeliveryEmailHandler
    EmailNotificationFacade ..> OrderEvent

    OrderAcceptedEmailHandler ..|> EmailNotificationHandler
    PackedAndShippedEmailHandler ..|> EmailNotificationHandler
    OutForDeliveryEmailHandler ..|> EmailNotificationHandler

    OrderAcceptedEmailHandler ..> OrderEvent
    PackedAndShippedEmailHandler ..> OrderEvent
    OutForDeliveryEmailHandler ..> OrderEvent
    EmailNotificationHandler ..> OrderEvent

    %% ============================================
    %% RELATIONSHIPS – REPOSITORIES
    %% ============================================
    OrderEventRepository ..> OrderEvent
    SystemSettingsRepository ..> SystemSettings

    %% ============================================
    %% RELATIONSHIPS – SERVICE FACADE
    %% ============================================
    OrderEventFacade --> CreateOrderEventHandler
    OrderEventFacade --> PackAndShipOrderHandler
    OrderEventFacade --> OutForDeliveryOrderHandler
    OrderEventFacade --> GetOrderEventsHandler
    OrderEventFacade --> GetAllOrderEventsHandler
    OrderEventFacade --> EmailNotificationFacade

    OrderEventFacade ..> OrderRequestDto
    OrderEventFacade ..> OrderStatusUpdateDto
    OrderEventFacade ..> OrderEvent
    OrderEventFacade ..> IllegalStateException

    %% ============================================
    %% RELATIONSHIPS – SERVICE HANDLERS
    %% ============================================
    CreateOrderEventHandler --> OrderEventRepository
    CreateOrderEventHandler --> OrderEventMapper
    CreateOrderEventHandler --> Validator
    CreateOrderEventHandler ..> OrderRequestDto
    CreateOrderEventHandler ..> OrderEvent

    GetAllOrderEventsHandler --> OrderEventRepository
    GetAllOrderEventsHandler ..> OrderEvent

    GetOrderEventsHandler --> OrderEventRepository
    GetOrderEventsHandler --> Validator
    GetOrderEventsHandler ..> OrderEvent

    PackAndShipOrderHandler --> OrderEventRepository
    PackAndShipOrderHandler --> Validator
    PackAndShipOrderHandler ..> OrderEvent
    PackAndShipOrderHandler ..> OrderStatus

    OutForDeliveryOrderHandler --> OrderEventRepository
    OutForDeliveryOrderHandler --> Validator
    OutForDeliveryOrderHandler ..> OrderEvent
    OutForDeliveryOrderHandler ..> OrderStatus

    %% ============================================
    %% RELATIONSHIPS – SETTINGS
    %% ============================================
    SettingsCache --> SystemSettingsRepository
    SettingsCache ..> SystemSettings

    SettingsInitializer --> SystemSettingsRepository
    SettingsInitializer ..> SystemSettings

    SystemSettingsService --> SystemSettingsRepository
    SystemSettingsService ..> SettingsDto
    SystemSettingsService ..> SystemSettings

    %% ============================================
    %% RELATIONSHIPS – VALIDATOR
    %% ============================================
    Validator ..> OrderEvent
    Validator ..> OrderStatus
    Validator ..> InvalidOrderStatusTransitionException
    Validator ..> OrderNotFoundException

    %% ============================================
    %% RELATIONSHIPS – EXCEPTIONS
    %% ============================================
    GetOrderEventsHandler ..> OrderNotFoundException
    PackAndShipOrderHandler ..> OrderNotFoundException
    OutForDeliveryOrderHandler ..> OrderNotFoundException