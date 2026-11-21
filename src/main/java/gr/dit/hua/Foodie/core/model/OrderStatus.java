package gr.dit.hua.Foodie.core.model;

/**
 * Every order has an active status
 */
public enum OrderStatus {
    PENDING,            // placed by the customer, waiting for the owner
    ACCEPTED,
    REJECTED,
    CANCELLED,          // cancelled by the customer while PENDING
    PREPARING,
    READY,
    DISPATCH_PENDING,   // courier request timed out, result unknown
    OUT_FOR_DELIVERY,
    DELIVERED,          // delivery orders
    COMPLETED           // pickup orders
}
