package entity;

/**
 * Entity helper: money in whole cents.
 *
 * Amounts are kept in cents and turned into dollars only for display.
 *
 * Usage:
 *   Money.format(1080)   // "$10.80"
 */
public final class Money {

    private Money() {
    }

    public static int percentOf(int cents, int percent) {
        return (cents * percent + 50) / 100;
    }

    public static String format(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
}
