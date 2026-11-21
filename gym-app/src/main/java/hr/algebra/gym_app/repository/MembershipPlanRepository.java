package hr.algebra.gym_app.repository;

import hr.algebra.gym_app.model.MembershipPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findByActiveTrue();
}