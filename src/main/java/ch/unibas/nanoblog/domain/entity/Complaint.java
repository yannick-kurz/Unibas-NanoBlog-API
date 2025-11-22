package ch.unibas.nanoblog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "complaint")
@Getter
@Setter
@NoArgsConstructor
public class Complaint extends BaseEntity {

    @Column(name = "username")
    private String usermail;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;
}
