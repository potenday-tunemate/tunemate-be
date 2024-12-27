package com.tunemate.be.domain.genre.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "genre", unique = true, nullable = false)
    private String genre;

    @Column(name = "created_at", updatable = false, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
