package tools;

import entity.Appetite;
import entity.ComboMeal;
import entity.CriticCustomer;
import entity.HappyHourPromotion;
import entity.Menu;
import entity.MenuItem;
import entity.Money;
import entity.NoPromotion;
import entity.Promotion;
import entity.SingleDish;
import entity.VIPCustomer;

/**
 * Development tool, not part of the game.
 *
 * Checks the prices, timings and rules that the brief states exactly.
 */
public class RuleCheck {

    private static int checks;
    private static int failures;

    public static void main(String[] args) {
        MenuItem salad = new SingleDish("Salad", 900, 1);
        MenuItem burger = new SingleDish("Burger", 1500, 1);
        MenuItem pasta = new SingleDish("Pasta", 1800, 2);

        expect("Salad price", Money.format(salad.getPriceCents()), "$9.00");
        expect("Burger price", Money.format(burger.getPriceCents()), "$15.00");
        expect("Pasta price", Money.format(pasta.getPriceCents()), "$18.00");
        expect("Pasta units", pasta.getPrepUnits(), 2);

        MenuItem saladPasta = new ComboMeal(salad, pasta);
        expect("Salad+Pasta price", Money.format(saladPasta.getPriceCents()), "$24.30");
        expect("Salad+Pasta units", saladPasta.getPrepUnits(), 3);
        expect("Salad+Pasta portions", saladPasta.getPortions(), 2);
        expect("Salad+Pasta name", saladPasta.getName(), "Salad + Pasta");
        expect("Salad+Burger price", Money.format(new ComboMeal(salad, burger).getPriceCents()),
                "$21.60");
        expect("Burger+Pasta price", Money.format(new ComboMeal(burger, pasta).getPriceCents()),
                "$29.70");

        VIPCustomer vip = new VIPCustomer(1, "V", 1, 0, Appetite.SMALL);
        Promotion happyHour = new HappyHourPromotion();
        int afterVip = vip.applyCustomerDiscount(burger.getPriceCents());
        expect("VIP Burger", Money.format(afterVip), "$13.50");
        expect("VIP Burger in Happy Hour", Money.format(happyHour.applyTo(afterVip)), "$10.80");
        expect("Happy Hour duration", happyHour.getTurnsRemaining(), 2);
        happyHour.endTurn();
        expect("Happy Hour after one turn", happyHour.getTurnsRemaining(), 1);
        expect("Happy Hour still active", happyHour.isActive(), true);
        happyHour.endTurn();
        expect("Happy Hour expired", happyHour.isActive(), false);
        expect("Expired Happy Hour charges full price",
                Money.format(happyHour.applyTo(afterVip)), "$13.50");
        expect("No promotion charges full price",
                Money.format(new NoPromotion().applyTo(1500)), "$15.00");

        expect("percentOf half-up 1", Money.percentOf(1005, 90), 905);
        expect("percentOf half-up 2", Money.percentOf(2700, 90), 2430);
        expect("percentOf 80 of 1350", Money.percentOf(1350, 80), 1080);
        expect("percentOf of zero", Money.percentOf(0, 80), 0);

        expect("Regular loss", new entity.RegularCustomer(2, "R", 1, 0, Appetite.SMALL)
                .getSatisfactionLossPerWaitingTurn(), 8);
        expect("VIP loss", vip.getSatisfactionLossPerWaitingTurn(), 5);
        expect("Critic loss", new CriticCustomer(3, "K", 1, 0, Appetite.SMALL)
                .getSatisfactionLossPerWaitingTurn(), 12);

        Menu menu = new Menu();
        menu.add(salad);
        menu.add(burger);
        menu.add(pasta);
        menu.add(new ComboMeal(salad, burger));
        menu.add(saladPasta);
        menu.add(new ComboMeal(burger, pasta));
        CriticCustomer critic = new CriticCustomer(4, "K", 1, 0, Appetite.SMALL);
        expect("Critic orders", critic.chooseFrom(menu).getName(), "Burger + Pasta");

        expect("small appetite group size", menu.itemsFor(Appetite.SMALL).size(), 3);
        expect("large appetite group size", menu.itemsFor(Appetite.LARGE).size(), 3);
        expect("small appetite picks a single dish",
                menu.itemsFor(Appetite.SMALL).get(0).getPortions(), 1);
        expect("large appetite picks a combo",
                menu.itemsFor(Appetite.LARGE).get(0).getPortions(), 2);

        Menu singlesOnly = new Menu();
        singlesOnly.add(salad);
        singlesOnly.add(burger);
        singlesOnly.add(pasta);
        expect("large appetite falls back to the whole menu",
                singlesOnly.itemsFor(Appetite.LARGE).size(), 3);

        System.out.println(checks + " checks, " + failures + " failures");
        if (failures > 0) {
            throw new AssertionError(failures + " rule checks failed");
        }
    }

    private static void expect(String what, Object actual, Object expected) {
        checks++;
        if (!String.valueOf(actual).equals(String.valueOf(expected))) {
            failures++;
            System.out.println("FAIL " + what + ": expected " + expected + " but got " + actual);
        } else {
            System.out.println("ok   " + what + " = " + actual);
        }
    }
}
