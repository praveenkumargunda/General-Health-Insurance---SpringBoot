package com.resilenceindia.insurance.service;
import java.util.*;

import com.resilenceindia.insurance.dto.RenewalPaymentRequest;
import com.resilenceindia.insurance.dto.RenewalResponse;

public interface RenewalService {
   // RenewalResponse getRenewalDetails(Long policyId);
    RenewalResponse payPremium(RenewalPaymentRequest request);
    List<RenewalResponse> getRenewalsByCustomerId(Long customerId);
}
