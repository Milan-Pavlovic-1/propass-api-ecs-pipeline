package net.altitudetech.propass.commons.converter.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import net.altitudetech.propass.commons.exception.IllegalConfigurationException;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.Query;

public class PageableConverterFactory extends Converter.Factory {
  @Override
  public Converter<?, String> stringConverter(Type type, Annotation[] annotations,
      Retrofit retrofit) {
    if (type == Pageable.class) {
      validateAnnotation(annotations);
      return new PageableConverter();
    }
    return null;
  }

  private void validateAnnotation(Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (annotation instanceof Query) {
        Query query = (Query) annotation;
        if (!"page".equals(query.value())) {
          throw new IllegalConfigurationException(
              "When using Pageable, @Query's value must be equal to 'page'.");
        }
        if (!query.encoded()) {
          throw new IllegalConfigurationException(
              "When using Pageable, @Query's encoded must be set to true.");
        }
        return;
      }
    }
    throw new IllegalConfigurationException(
        "When using Pageable, @Query(value = \"page\", encoded=true) must be used.");
  }

  private static class PageableConverter implements Converter<Pageable, String> {
    @Override
    public String convert(Pageable pageable) {
      // hack since retrofit is rigid and won't allow to convert to map via a factory, but it also
      // won't allow @Query to be without name, so we omit page=
      String query = Integer.toString(pageable.getPageNumber()) + "&";
      query += "size=" + Integer.toString(pageable.getPageSize());

      if (pageable.getSort() != null && pageable.getSort().isSorted()) {
        query += "&" + URLEncoder.encode(sortToQuery(pageable.getSort()), StandardCharsets.UTF_8);
      }
      return query;
    }

    private String sortToQuery(Sort sort) {
      List<String> orders = new ArrayList<>();
      if (sort != null) {
        sort.forEach(order -> {
          if (order != null && order.getProperty() != null) {
            orders.add("sort=" + order.getProperty() + ","
                + (order.getDirection() != null ? order.getDirection() : Direction.DESC));
          }
        });
      }
      return String.join("&", orders);
    }
  }
}
