package entity;

/**
 * Entity: the kinds of customer.
 *
 * ArrivalSchedule reads the line-up of types to decide which subclass of Customer to
 * create.
 */
public enum CustomerType {

    REGULAR,
    VIP,
    CRITIC
}
