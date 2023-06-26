package net.altitudetech.propass.flight.service.specification;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.altitudetech.propass.flight.service.model.Flight;
import net.altitudetech.propass.flight.service.model.FlightLocation;

public class FlightSpecification implements Specification<Flight> {
  private static final long serialVersionUID = -5358954922616184934L;

  private final Flight filter;

  public FlightSpecification(Flight filter) {
    this.filter = filter;
  }

  @Override
  public Predicate toPredicate(Root<Flight> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    List<Predicate> predicates = new ArrayList<>();

    if (this.filter != null) {
      setupFlightLocationFilter(this.filter.getLocationFrom(), "From", root, cb, predicates);
      setupFlightLocationFilter(this.filter.getLocationTo(), "To", root, cb, predicates);

      if (this.filter.getAirlineId() != null) {
        Path<String> path = root.get("airlineId");
        predicates.add(cb.equal(path, this.filter.getAirlineId()));
      }
    }

    return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
  }

  private void setupFlightLocationFilter(FlightLocation location, String columnSuffix,
      Root<Flight> root, CriteriaBuilder cb, List<Predicate> predicates) {
    if (location != null) {
      if (location.getCode() != null) {
        Path<String> codePath = root.get("location" + columnSuffix).get("code");
        predicates.add(cb.equal(codePath, location.getCode()));
      }

      if (location.getName() != null) {
        Path<String> namePath = root.get("location" + columnSuffix).get("name");
        predicates.add(cb.like(cb.lower(namePath), location.getName() + "%"));
      }
    }
  }

}
