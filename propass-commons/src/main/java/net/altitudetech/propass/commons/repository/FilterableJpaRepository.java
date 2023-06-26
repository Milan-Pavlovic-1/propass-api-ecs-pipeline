package net.altitudetech.propass.commons.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import net.altitudetech.propass.commons.model.BaseModel;

@NoRepositoryBean
public interface FilterableJpaRepository<T extends BaseModel>
		extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

}
