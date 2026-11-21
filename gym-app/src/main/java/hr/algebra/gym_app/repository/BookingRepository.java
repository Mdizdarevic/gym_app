package hr.algebra.gym_app.repository;

import hr.algebra.gym_app.model.Booking;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"member", "classInstance"})
    List<Booking> findAll();

    @EntityGraph(attributePaths = {"classInstance"})
    List<Booking> findByMemberId(Long memberId);

    @EntityGraph(attributePaths = {"member"})
    List<Booking> findByClassInstanceId(Long classInstanceId);

    long countByClassInstanceIdAndStatus(Long classInstanceId, String status);
}