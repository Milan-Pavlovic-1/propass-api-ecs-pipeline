package net.altitudetech.propass.commons.converter.retrofit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;

public class ObjectToPathMapMapper {

  public Map<String, Object> map(Object object)
      throws IllegalArgumentException, IllegalAccessException {
    return map(null, object);
  }

  private Map<String, Object> map(String prefix, Object object)
      throws IllegalArgumentException, IllegalAccessException {
    Map<String, Object> map = new HashMap<>();
    if (object != null) {
      Class<?> cls = object.getClass();
      for (Field field : getAllDeclaredFields(cls)) {
        field.setAccessible(true);
        if (isSimpleType(field.getType()) && field.get(object) != null) {
          String path =  getPath(prefix, field);
          Object value = field.get(object);
          map.put(path, value);
        } else if (Collection.class.isAssignableFrom(field.getType())) {
          // TODO ignore for now
        } else {
          map.putAll(map(getPath(prefix, field), field.get(object)));
        }
      }
    }
    return map;
  }
  
  private String getPath(String prefix, Field field) {
    return prefix != null ? prefix + "." + field.getName() : field.getName();
  }

  private boolean isSimpleType(Class<?> cls) {
    return Number.class.isAssignableFrom(cls) || Boolean.class.isAssignableFrom(cls)
        || String.class.isAssignableFrom(cls) || cls.isEnum();
  }

  private List<Field> getAllDeclaredFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<>();
    ReflectionUtils.doWithFields(clazz, fields::add);
    return fields;
  }
}
