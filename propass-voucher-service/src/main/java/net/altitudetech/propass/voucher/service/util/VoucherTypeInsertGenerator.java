package net.altitudetech.propass.voucher.service.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Singular;

public class VoucherTypeInsertGenerator {
  private static final String SUPER_SAVER = "Super Saver";
  private static final String SMART = "Smart";
  private static final String FREE_SOUL = "Free Soul";
  private static final String ECONOMY = "economy";
  private static final String BUSINESS = "business";
  private static final long SALALAH = 3L;
  private static final long DUBAI = 2L;
  private static final long DOHA = 1L;
  private static final List<VoucherTypeEntry> entries = new ArrayList<>();
  
  public static void main(String[] args) {
    add(e(SUPER_SAVER, SALALAH, ECONOMY).savingsFormula(discounts(8)).build());
    add(e(SMART, SALALAH, ECONOMY).savingsFormula(discounts(6)).build());
    add(e(FREE_SOUL, SALALAH, ECONOMY).savingsFormula(discounts(4)).build());
    add(e(SUPER_SAVER, SALALAH, BUSINESS).savingsFormula(discounts(6)).build());
    add(e(SMART, SALALAH, BUSINESS).savingsFormula(discounts(4)).build());
    add(e(FREE_SOUL, SALALAH, BUSINESS).savingsFormula(discounts(2)).build());
    
    add(e(SUPER_SAVER, DUBAI, ECONOMY).savingsFormula(discounts(7)).build());
    add(e(SMART, DUBAI, ECONOMY).savingsFormula(discounts(5)).build());
    add(e(FREE_SOUL, DUBAI, ECONOMY).savingsFormula(discounts(3)).build());
    add(e(SUPER_SAVER, DUBAI, BUSINESS).savingsFormula(discounts(5)).build());
    add(e(SMART, DUBAI, BUSINESS).savingsFormula(discounts(3)).build());
    add(e(FREE_SOUL, DUBAI, BUSINESS).savingsFormula(discounts(1)).build());
    
    add(e(SUPER_SAVER, DOHA, ECONOMY).savingsFormula(discounts(6)).build());
    add(e(SMART, DOHA, ECONOMY).savingsFormula(discounts(4)).build());
    add(e(FREE_SOUL, DOHA, ECONOMY).savingsFormula(discounts(2)).build());
    add(e(SUPER_SAVER, DOHA, BUSINESS).savingsFormula(discounts(5)).build());
    add(e(SMART, DOHA, BUSINESS).savingsFormula(discounts(3)).build());
    add(e(FREE_SOUL, DOHA, BUSINESS).savingsFormula(discounts(1)).build());
    
    printAll();
  }
  
  private static void printAll() {
    for (int i = 0; i < entries.size(); i++) {
      print(i+1, entries.get(i));
    }
  }
  
  private static void print(int id, VoucherTypeEntry e) {
    System.out.println(format(id, e));
  }
  
  private static String format(int id, VoucherTypeEntry e) {
    return String.format(
        "INSERT INTO vouchers_type (id, name, airline_id, price_formula, restriction_formula) VALUES (%d, '%s', 1,"
        + " '{\"savingsFormula\":\"%s\"}',"
        + " '{\"flights\":[%d],\"minDaysBefore\":10,\"class\":\"%s\",\"cabinBag\":\"%s\",\"checkedBag\":\"%s\",\"seatsSelection\":\"%s\",\"refundFees\":\"%s\"}');",
        id, e.name, e.savingsFormula, e.flights.get(0), e.flightClass, e.cabinBag, e.checkedBag, e.seatsSelection, e.refundFees);
  }
  
  private static VoucherTypeEntry.VoucherTypeEntryBuilder f(VoucherTypeEntry.VoucherTypeEntryBuilder b, String name, String flightClass) {
    if (flightClass.equals(ECONOMY)) {
      switch (name) {
        case SUPER_SAVER: b.checkedBag("0kg"); b.seatsSelection("$"); b.refundFees("None"); break;
        case SMART: b.checkedBag("30kg"); b.seatsSelection("Standard Seats"); b.refundFees("Medium"); break;
        case FREE_SOUL: b.checkedBag("30kg"); b.seatsSelection("Selected Seats"); b.refundFees("Low"); break;
      }
      
      b.cabinBag("1 x 7kg");
    } else {
      switch (name) {
        case SUPER_SAVER: b.refundFees("High"); break;
        case SMART: b.refundFees("Medium"); break;
        case FREE_SOUL: b.refundFees("Low"); break;
      }
      
      b.cabinBag("2 x 7kg");
      b.checkedBag("50kg");
      b.seatsSelection("Free");
    }
    
    return b;
  }
  
  private static VoucherTypeEntry.VoucherTypeEntryBuilder e(String name, Long flight, String flightClass) {
    return f(VoucherTypeEntry.builder().name(name).flight(flight).flightClass(flightClass), name, flightClass);
  }
  
  private static void add(VoucherTypeEntry entry) {
    entries.add(entry);
  }
  
  private static String discounts(int a) {
    return discounts(a, a+1, a+2, a+3);
  }
  
  private static String discounts(int a, int b, int c, int d) {
    return String.format("v < 10 ? %d : v < 20 ? %d : v < 30 ? %d : %d", a, b, c, d);
  }
  
  @Builder
  private static class VoucherTypeEntry {
    public String name;
    @Singular
    public List<Long> flights;
    public String savingsFormula;
    public String cabinBag;
    public String checkedBag;
    public String seatsSelection;
    public String refundFees;
    public String flightClass;
    public Integer vouchers;
  }
  
  // INSERT INTO vouchers_type (id, name, airline_id, price_formula, restriction_formula, voucher_amount) VALUES (1, 'first', 1, '{"vouchers":10,"savingsPercent":20}', '{"flights":[123,456],"minDaysBefore":10,"class":"economy"}', 10);
}
