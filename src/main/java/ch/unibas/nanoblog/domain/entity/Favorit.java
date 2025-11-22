package ch.unibas.nanoblog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "favorit")
@Getter
@Setter
@NoArgsConstructor
public class Favorit extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;
}
