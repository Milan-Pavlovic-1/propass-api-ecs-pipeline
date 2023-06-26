package net.altitudetech.propass.commons.security.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import jakarta.servlet.http.HttpServletRequest;
import net.altitudetech.propass.commons.security.annotation.Public;

public class PublicRequestMatcher implements RequestMatcher {

  private final List<RequestMappingInfo> publicRequestMappings;
  
  public PublicRequestMatcher(RequestMappingHandlerMapping mapping) {
    this.publicRequestMappings = getPublicMappings(mapping);
  }

  @Override
  public boolean matches(HttpServletRequest request) {
    ServletRequestPathUtils.parseAndCache(request);
    return this.publicRequestMappings.stream()
        .anyMatch(mapping -> matches(request, mapping));
  }

  private boolean matches(HttpServletRequest request, RequestMappingInfo mapping) {
    return methodMatches(request, mapping) && pathMatches(request, mapping);
  }

  private boolean methodMatches(HttpServletRequest request, RequestMappingInfo mapping) {
    return mapping.getActivePatternsCondition() != null ? mapping.getMethodsCondition().getMatchingCondition(request) != null : false;
  }

  private boolean pathMatches(HttpServletRequest request, RequestMappingInfo mapping) {
    try {
      return mapping.getActivePatternsCondition() != null ? mapping.getActivePatternsCondition().getMatchingCondition(request) != null : false;
    } catch (IllegalArgumentException e) {
      // a path that's unknown to Spring's context will not have a @Public annotation on it
      return false;
    }
  }

  private List<RequestMappingInfo> getPublicMappings(RequestMappingHandlerMapping mapping) {
    List<RequestMappingInfo> publicRequestMappings = new ArrayList<>();
    Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

    for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
      if (entry.getValue().hasMethodAnnotation(Public.class)) {
        publicRequestMappings.add(entry.getKey());
      }
    }
    
    return publicRequestMappings;
  }

}
