
package com.opportunity360.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opportunity360.notification.entity.InAppNotification;

@Repository
public interface NotificationRepository
        extends JpaRepository<InAppNotification, Long> {

    List<InAppNotification> findAllByUser_EmailOrderByCreatedAtDesc(
            String email);

    Optional<InAppNotification> findByIdAndUser_Email(
            Long id, String email);

    long countByUser_EmailAndReadFalse(String email);

    boolean existsByReminder_Id(Long reminderId);
}
