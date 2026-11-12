package entity;

/**
 * Entity: the stages of an order.
 *
 * PLACED, PREPARING, READY, SERVED, PAID, or CANCELLED if the customer walks out.
 */
public enum OrderStatus {

    PLACED,
    PREPARING,
    READY,
    SERVED,
    PAID,
    CANCELLED
}
