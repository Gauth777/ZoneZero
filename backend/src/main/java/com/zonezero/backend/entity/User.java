package com.zonezero.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
  @Id
  @Column(length = 36)
  private String id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column
  private String phone;

  @Column(nullable = false)
  private String region;

  @Column(nullable = false)
  private Instant createdAt;

  public User() {
    this.id = UUID.randomUUID().toString();
    this.createdAt = Instant.now();
  }

  // getters/setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getRegion() { return region; }
  public void setRegion(String region) { this.region = region; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
