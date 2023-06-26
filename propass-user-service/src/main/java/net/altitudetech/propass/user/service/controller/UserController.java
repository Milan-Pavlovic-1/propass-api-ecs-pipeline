package net.altitudetech.propass.user.service.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.altitudetech.propass.commons.controller.BaseController;
import net.altitudetech.propass.commons.exception.NotFoundException;
import net.altitudetech.propass.commons.security.annotation.Public;
import net.altitudetech.propass.user.service.dto.UserDTO;
import net.altitudetech.propass.user.service.model.User;
import net.altitudetech.propass.user.service.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User, UserDTO> {
  @Autowired
  private UserService userService;
  
  @Autowired
  public UserController(ModelMapper modelMapper) {
    super(modelMapper);
  }

  @Public
  @GetMapping
  public Page<UserDTO> list(Pageable pageable, @RequestParam(required = false) String email) {
    return toDTOs(this.userService.findAll(email, pageable));
  }

  @GetMapping("/{id}")
  public UserDTO single(@PathVariable Long id) {
    User user = this.userService.findOne(id)
        .orElseThrow(() -> new NotFoundException("User " + id + " not found."));
    return toDTO(user);
  }
  
  @Public
  @PostMapping
  public UserDTO create(@RequestBody UserDTO user) {
    return toDTO(this.userService.save(toEntity(user)));
  }
  
  @PutMapping("/{id}")
  public UserDTO update(@PathVariable Long id, @RequestBody UserDTO user) {
    user.setId(id);
    return toDTO(this.userService.save(toEntity(user)));
  }
}
