package tz.tante.auth.manager.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class Account extends BaseEntity
{
  @Column(unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String phoneNumber;

  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private boolean enabled = true;

  @Column(nullable = false)
  private boolean emailVerified = false;

  @Column(nullable = false)
  private boolean phoneNumberVerified = false;

  @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
  private User user;

  public Account(String phoneNumber, String passwordHash)
  {
    this.phoneNumber = phoneNumber;
    this.passwordHash = passwordHash;
  }

  public Account(String phoneNumber, String passwordHash, String email)
  {
    this.phoneNumber = phoneNumber;
    this.passwordHash = passwordHash;
    this.email = email;
  }


}
