package net.altitudetech.propass.commons.converter.modelmapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeConverter implements Converter<JsonNode, JsonNode> {

  @Override
  public JsonNode convert(MappingContext<JsonNode, JsonNode> context) {
    return context.getSource() != null ? context.getSource().deepCopy() : null;
  }

}
