package net.altitudetech.propass.commons.converter.retrofit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import lombok.Getter;
import lombok.Setter;

public class ObjectToPathMapMapperTest {

  @Test
  public void testMap() throws IllegalArgumentException, IllegalAccessException {
    Foo foo = new Foo();
    
    ObjectToPathMapMapper mapper = new ObjectToPathMapMapper();
    Map<String, Object> map = mapper.map(foo);
    
    assertEquals(4, map.size());
    assertTrue(map.containsKey("text"));
    assertTrue(map.containsKey("number"));
    assertTrue(map.containsKey("bar.bool"));
    assertTrue(map.containsKey("bar.baz.value"));
    assertEquals(foo.getText(), map.get("text"));
    assertEquals(foo.getNumber(),map.get("number"));
    assertEquals(foo.getBar().getBool(),map.get("bar.bool"));
    assertEquals(foo.getBar().getBaz().getValue(),map.get("bar.baz.value"));
  }
  
  @Getter
  @Setter
  public static class Foo {
    private String text = "Text";
    private Integer number = 13031990;
    private Integer nulled = null;
    private Bar bar = new Bar();
    
    @Getter
    @Setter
    public static class Bar {
      private Boolean bool = true;
      private List<String> ignored = new ArrayList<>();
      private Baz baz = new Baz();
      
      @Getter
      @Setter
      public static class Baz {
        private Double value = 1.0;
      }
    }
  }
  
}
