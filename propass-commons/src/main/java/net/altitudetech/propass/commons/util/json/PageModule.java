package net.altitudetech.propass.commons.util.json;

import org.springframework.data.domain.Page;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class PageModule extends SimpleModule {
  private static final long serialVersionUID = 4223541553269356324L;

  public PageModule() {
    addDeserializer(Page.class, new PageDeserializer());
  }
}
