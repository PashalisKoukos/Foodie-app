package gr.dit.hua.Foodie.core.model;


/**
 * Every order has an active status
 */
public enum OrderStatus {
    PENDING, ACCEPTED, REJECTED, CANCELLED, PREPARING, READY,
    DISPATCH_PENDING, OUT_FOR_DELIVERY, DELIVERED, COMPLETED;

    // Which statuses may follow this one (BR5). OUT_FOR_DELIVERY can only become DELIVERED (BR3).
    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == ACCEPTED || next == REJECTED || next == CANCELLED;
            case ACCEPTED -> next == PREPARING;
            case PREPARING -> next == READY;
            case READY -> next == COMPLETED || next == OUT_FOR_DELIVERY || next == DISPATCH_PENDING;
            case DISPATCH_PENDING -> next == OUT_FOR_DELIVERY || next == READY;
            case OUT_FOR_DELIVERY -> next == DELIVERED;
            default -> false; // REJECTED, CANCELLED, COMPLETED, DELIVERED are final
        };
    }
}
