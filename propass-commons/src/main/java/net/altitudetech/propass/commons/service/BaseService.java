package net.altitudetech.propass.commons.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import net.altitudetech.propass.commons.model.BaseModel;

public abstract class BaseService<T extends BaseModel, R extends JpaRepository<T, Long>> {
  
  protected final R repository;

  protected BaseService(R repository) {
    this.repository = repository;
  }
  
  public Optional<T> findOne(Long id) {
    return getRepository().findById(id);
  }

  public List<T> findAll(List<Long> ids) {
    return getRepository().findAllById(ids);
  }

  public T save(T entity) {
    return getRepository().save(entity);
  }

  public T saveAndFlush(T entity) {
    return getRepository().saveAndFlush(entity);
  }

  public List<T> findAll() {
    return getRepository().findAll();
  }

  public Page<T> findAll(Pageable pageable) {
    return getRepository().findAll(pageable);
  }

  public void delete(T entity) {
    getRepository().delete(entity);
  }

  public void delete(Long id) {
    getRepository().deleteById(id);
  }

  public boolean exists(Long id) {
    return getRepository().findById(id) != null;
  }

  protected R getRepository() {
    return this.repository;
  }

}
