package com.insurewell.service;

import com.insurewell.model.Policy;
import com.insurewell.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyRenewalReminderServiceTest {

  @Mock
  private PolicyRepository policyRepository;

  @InjectMocks
  private PolicyRenewalReminderService reminderService;

  @Test
  void returnsOnlyActivePoliciesExpiringTodayThroughThirtyDays() {
    LocalDate today = LocalDate.now();
    List<Policy> policies = List.of(
      policy("today", "active", today),
      policy("in-thirty-days", "ACTIVE", today.plusDays(30)),
      policy("expired", "active", today.minusDays(1)),
      policy("too-far-away", "active", today.plusDays(31)),
      policy("inactive", "inactive", today.plusDays(5)),
      policyWithEndDate("invalid-date", "active", "not-a-date")
    );
    when(policyRepository.findAll()).thenReturn(policies);

    List<Policy> expiringPolicies = reminderService.findPoliciesExpiringWithin30Days();

    assertEquals(List.of("today", "in-thirty-days"),
      expiringPolicies.stream().map(Policy::getId).toList());
  }

  private Policy policy(String id, String status, LocalDate endDate) {
    return policyWithEndDate(id, status, endDate.toString());
  }

  private Policy policyWithEndDate(String id, String status, String endDate) {
    return Policy.builder()
      .id(id)
      .status(status)
      .endDate(endDate)
      .build();
  }
}
