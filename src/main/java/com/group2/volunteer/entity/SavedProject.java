package com.group2.volunteer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "saved_projects",
        uniqueConstraints = @UniqueConstraint(name = "unique_user_project", columnNames = {"volunteer_id", "project_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavedProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_save_volunteer"))
    private User volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_save_project"))
    private Project project;

    @Column(name = "saved_at", insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime savedAt;
}
