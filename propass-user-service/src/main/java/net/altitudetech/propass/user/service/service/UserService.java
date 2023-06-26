package net.altitudetech.propass.user.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import net.altitudetech.propass.commons.service.BaseService;
import net.altitudetech.propass.user.service.model.User;
import net.altitudetech.propass.user.service.repository.UserRepository;

@Service
public class UserService extends BaseService<User, UserRepository> {

  @Autowired
  public UserService(UserRepository userRepository) {
    super(userRepository);
  }
  
  public Page<User> findAll(String email, Pageable pageable) {
    if (email != null) {
      return this.repository.findAllByEmail(email, pageable);
    } else {
      return super.findAll(pageable);
    }
  }

}
