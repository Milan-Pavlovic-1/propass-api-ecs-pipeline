package net.altitudetech.propass.user.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.altitudetech.propass.commons.model.AuditableModel;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User extends AuditableModel {
    @Column(nullable = false)
	private String firstName;
    @Column(nullable = false)
	private String lastName;
    @Column(nullable = false)
	private String email;
    @Column(nullable = false, name = "enc_password")
    private String password;
}
