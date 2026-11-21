package hr.algebra.gym_app.repository;

import hr.algebra.gym_app.model.ClassInstance;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassInstanceRepository extends JpaRepository<ClassInstance, Long> {

    @EntityGraph(attributePaths = {"classTemplate"})
    List<ClassInstance> findAllByOrderByStartsAtAsc();
}
