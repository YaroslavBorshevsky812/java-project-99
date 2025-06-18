package hexlet.code.repository;

import hexlet.code.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    Optional<Status> findBySlug(String slug);
}
