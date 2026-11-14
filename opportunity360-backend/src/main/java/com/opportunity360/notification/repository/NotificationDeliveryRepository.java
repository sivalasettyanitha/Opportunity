
package com.opportunity360.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opportunity360.notification.entity.DeliveryChannel;
import com.opportunity360.notification.entity.NotificationDelivery;

@Repository
public interface NotificationDeliveryRepository
        extends JpaRepository<NotificationDelivery, Long> {

    Optional<NotificationDelivery> findByReminder_IdAndChannel(
            Long reminderId,
            DeliveryChannel channel);

    List<NotificationDelivery> findAllByRecipientOrderByCreatedAtDesc(
            String recipient);

    Optional<NotificationDelivery> findByIdAndRecipient(
            Long id,
            String recipient);
}
