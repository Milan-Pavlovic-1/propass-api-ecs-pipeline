package net.altitudetech.propass.commons.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import net.jodah.typetools.TypeResolver;

public class BaseController<E, D> {
  protected final ModelMapper modelMapper;

  public BaseController(ModelMapper modelMapper) {
    super();
    this.modelMapper = modelMapper;
  }
  
  protected E toEntity(D dto) {
    return this.modelMapper.map(dto, entityClass());
  }
  
  protected D toDTO(E entity) {
    return this.modelMapper.map(entity, dtoClass());
  }
  protected Page<D> toDTOs(Page<E> page) {
    return page.map(e -> toDTO(e));
  }
  
  @SuppressWarnings("unchecked")
  private Class<E> entityClass() {
    return (Class<E>) nthGeneric(0);
  }
  
  @SuppressWarnings("unchecked")
  private Class<D> dtoClass() {
    return (Class<D>) nthGeneric(1);
  }
  
  private Class<?> nthGeneric(int n) {
    return TypeResolver.resolveRawArguments(BaseController.class, this.getClass())[n];
  }
  
}
