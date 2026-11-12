package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entity: the results so far.
 *
 * Revenue is added up from the payment records, so the two can never disagree.
 */
public class ScoreBoard {

    private final List<PaymentRecord> payments = new ArrayList<PaymentRecord>();
    private int servedCount;
    private int unhappyDepartures;

    public void recordPayment(PaymentRecord record) {
        payments.add(record);
    }

    public void recordServing() {
        servedCount++;
    }

    public void recordUnhappyDeparture() {
        unhappyDepartures++;
    }

    public int getRevenueCents() {
        int total = 0;
        for (PaymentRecord record : payments) {
            total += record.getAmountCents();
        }
        return total;
    }

    public int getPaidCustomerCount() {
        return payments.size();
    }

    public boolean hasPayments() {
        return !payments.isEmpty();
    }

    public double getAverageSatisfaction() {
        if (payments.isEmpty()) {
            return 0.0;
        }
        int sum = 0;
        for (PaymentRecord record : payments) {
            sum += record.getSatisfaction();
        }
        return (double) sum / payments.size();
    }

    public int getServedCount() {
        return servedCount;
    }

    public int getUnhappyDepartures() {
        return unhappyDepartures;
    }

    public List<PaymentRecord> getPayments() {
        return Collections.unmodifiableList(payments);
    }
}
