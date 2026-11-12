package entity;

/**
 * Entity: the stages of a customer's visit.
 *
 * WAITING, SEATED, ORDERED, SERVED, READY_TO_PAY, PAID, or LEFT if satisfaction
 * reaches 0.
 */
public enum CustomerState {

    WAITING,
    SEATED,
    ORDERED,
    SERVED,
    READY_TO_PAY,
    PAID,
    LEFT
}
