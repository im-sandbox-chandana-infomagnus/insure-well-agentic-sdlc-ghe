package com.insurewell.service;

import com.insurewell.model.Policy;
import com.insurewell.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
public class PolicyRenewalReminderService {

  private final PolicyRepository policyRepository;

  public PolicyRenewalReminderService(PolicyRepository policyRepository) {
    this.policyRepository = policyRepository;
  }

  public List<Policy> findPoliciesExpiringWithin30Days() {
    LocalDate today = LocalDate.now();
    LocalDate reminderEndDate = today.plusDays(30);

    return policyRepository.findAll().stream()
      .filter(policy -> "active".equalsIgnoreCase(policy.getStatus()))
      .filter(policy -> expiresWithin(policy, today, reminderEndDate))
      .sorted(Comparator.comparing(Policy::getEndDate))
      .toList();
  }

  private boolean expiresWithin(Policy policy, LocalDate today, LocalDate reminderEndDate) {
    if (policy.getEndDate() == null) {
      return false;
    }

    try {
      LocalDate endDate = LocalDate.parse(policy.getEndDate());
      return !endDate.isBefore(today) && !endDate.isAfter(reminderEndDate);
    } catch (DateTimeParseException exception) {
      return false;
    }
  }
}
