package hr.algebra.gym_app.repository;

import hr.algebra.gym_app.model.ClassTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassTemplateRepository extends JpaRepository<ClassTemplate, Long> {
    List<ClassTemplate> findByActiveTrue();
}