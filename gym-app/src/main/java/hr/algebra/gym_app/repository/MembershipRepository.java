package hr.algebra.gym_app.repository;

import hr.algebra.gym_app.model.Membership;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    @EntityGraph(attributePaths = {"member", "plan"})
    List<Membership> findAll();

    @EntityGraph(attributePaths = {"plan"})
    List<Membership> findByMemberId(Long memberId);
}