package net.altitudetech.propass.voucher.service.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class VoucherTypeCommand {
  private int priority;
  
  public abstract boolean accept(String name);
}
