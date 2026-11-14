
package com.opportunity360.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.notification.dto.NotificationPreferenceRequest;
import com.opportunity360.notification.dto.NotificationPreferenceResponse;
import com.opportunity360.notification.entity.NotificationPreference;
import com.opportunity360.notification.repository.NotificationPreferenceRepository;
import com.opportunity360.user.entity.User;
import com.opportunity360.user.repository.UserRepository;

@Service
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public NotificationPreferenceService(
            NotificationPreferenceRepository preferenceRepository,
            UserRepository userRepository) {

        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    // Get existing preferences or create default preferences.
    @Transactional
    public NotificationPreferenceResponse getPreferences(String email) {

        NotificationPreference preference = preferenceRepository
                .findByUser_Email(email)
                .orElseGet(() -> createDefaultPreferences(email));

        return NotificationPreferenceResponse.fromPreference(preference);
    }

    // Create default preferences for a new user.
    private NotificationPreference createDefaultPreferences(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        NotificationPreference preference =
                new NotificationPreference();

        preference.setUser(user);
        preference.setEmailEnabled(true);
        preference.setInAppEnabled(true);

        return preferenceRepository.save(preference);
    }

    // Update notification preferences.
    @Transactional
    public NotificationPreferenceResponse updatePreferences(
            String email,
            NotificationPreferenceRequest request) {

        NotificationPreference preference = preferenceRepository
                .findByUser_Email(email)
                .orElseGet(() -> createDefaultPreferences(email));

        preference.setEmailEnabled(request.isEmailEnabled());
        preference.setInAppEnabled(request.isInAppEnabled());

        NotificationPreference updatedPreference =
                preferenceRepository.save(preference);

        return NotificationPreferenceResponse.fromPreference(
                updatedPreference);
    }
}
