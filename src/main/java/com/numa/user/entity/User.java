package com.numa.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.numa.audit.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @NotNull
	@Column(nullable = false, unique = true)
	private String username;

    @NotNull
    @Email
	@Column(nullable = false, unique = true)
	private String email;

    @NotNull
	@Column(nullable = false)
	private String password;

	private Long roleId;

    @NotNull
	private String name;
    @NotNull
	private String surname;
	private String country;
	private String mobileNumber;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	private Instant dob;

}