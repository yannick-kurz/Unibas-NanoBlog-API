package ch.unibas.nanoblog.domain.repository;

import ch.unibas.nanoblog.domain.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Complaint findByComplaintId(long id);

    Complaint findByComplaintTitle(String title);

    Complaint findByComplaintId(String id);
}
