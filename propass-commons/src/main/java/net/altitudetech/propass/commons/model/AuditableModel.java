package net.altitudetech.propass.commons.model;

import java.util.Date;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableModel extends BaseModel {

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @LastModifiedBy
  @Column(name = "updated_by")
  private String updatedBy;

  @CreatedDate
  @Temporal(value = TemporalType.TIMESTAMP)
  @Column(name = "created_at", updatable = false)
  private Date createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(value = TemporalType.TIMESTAMP)
  private Date updatedAt;

  public AuditableModel(Long id) {
    super(id);
  }

}
