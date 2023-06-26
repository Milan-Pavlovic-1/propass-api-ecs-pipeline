package net.altitudetech.propass.user.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import net.altitudetech.propass.user.service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  public Page<User> findAllByEmail(String email, Pageable pageable);
}
