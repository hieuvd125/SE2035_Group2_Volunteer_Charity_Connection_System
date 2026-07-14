package com.group2.volunteer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "saved_projects",uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "volunteer_id","project_id"
        })
})

public class SavedProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id",nullable = false)
    private User volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @Column(name = "saved_at",nullable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onCreate(){
        if(this.savedAt == null){
            this.savedAt = LocalDateTime.now();
        }
    }




}
