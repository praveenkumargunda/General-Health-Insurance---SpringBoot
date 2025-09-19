function fetchCustomerHistory() {
    const customerId = document.getElementById("customerId").value;

    // Step 1: Basic validation  .. this is in payment history page
    if (!customerId) {
        alert("Please enter your Customer ID!");
        return;
    }

    const customerIdNum = parseInt(customerId);
    if (isNaN(customerIdNum) || customerIdNum <= 0) {
        alert("Customer ID must be a positive number!");
        return;
    }

   /* // Step 2: Call history API
    fetch(`/api/history/${customerId}`)
	//fetch(`/renewals/customer/${customerId}`)
        .then(res => res.json())
        .then(data => {
            const tableBody = document.getElementById("historyTable");
            tableBody.innerHTML = "";

            if (!data || data.length === 0) {
                tableBody.innerHTML = `<tr><td colspan="10" class="text-center">No payment history found</td></tr>`;
                return;
            }

            data.forEach(row => {
                tableBody.innerHTML += `
                    <tr>
                        <td>${row.customerId}</td>
                        <td>${row.policyId}</td>
                        <td>${row.paymentId}</td>
                        <td>${row.amount}</td>
                        <td>${row.mode}</td>
                        <td>${row.status}</td>
                        <td>${row.paymentDate}</td>
                        <td>${row.nextPremiumDate}</td>
                        <td>${row.policyStatus}</td>
                        <td>${row.statusMessage}</td>
                    </tr>
                `;
            });
        })
        .catch(err => console.error("Error fetching history:", err)); */

    // Step 3: Call renewals API
    fetch(`/renewals/customer/${customerId}`)
        .then(res => {
            if (res.status === 401) {
                throw new Error("Please log in first to access renewal details");
            }
            if (res.status === 403) {
                throw new Error("Access Denied: You can only view your own renewal details. Please enter your correct Customer ID.");
            }
            if (res.status === 400) {
                return res.json().then(errorData => {
                    if (errorData.errorCode === "INVALID_CUSTOMER_ID") {
                        throw new Error("Customer ID must be a positive number");
                    } else {
                        throw new Error("Invalid request: " + errorData.message);
                    }
                });
            }
            if (res.status === 404) {
                return res.json().then(errorData => {
                    if (errorData.errorCode === "CUSTOMER_NOT_FOUND") {
                        throw new Error("Customer not found with the provided ID");
                    } else if (errorData.errorCode === "POLICY_NOT_FOUND") {
                        throw new Error("No policies found for this customer");
                    } else {
                        throw new Error("Customer or policies not found");
                    }
                });
            }
            return res.json();
        })
        .then(data => {
            const table = document.getElementById("historyTable");
            table.innerHTML = "";

            if (!data || !Array.isArray(data)) {
                table.innerHTML = "<tr><td colspan='10' class='text-center text-danger'>Invalid data format received from server</td></tr>";
                return;
            }

            if (data.length === 0) {
                table.innerHTML = "<tr><td colspan='10' class='text-center'>No policies found</td></tr>";
                return;
            }

            data.forEach(policy => {
                table.innerHTML += `
                <tr>
                    <td>${policy.customerId ?? "-"}</td>
                    <td>${policy.policyId ?? "-"}</td>
                    <td>${policy.paymentId ?? "-"}</td>
                    <td>${policy.amount ?? "-"}</td>
                    <td>${policy.paymentMode ?? "-"}</td>
                    <td>${policy.paymentStatus ?? "-"}</td>
                    <td>${policy.paymentDate ?? "-"}</td>
                    <td>${policy.nextPremiumDate ?? "-"}</td>
                    <td>${policy.policyStatus}</td>
                    <td>${policy.message ?? "-"}</td>
                </tr>`;
            });

          //  console.log("Renewal history loaded successfully for Customer ID:", customerId);
        })
        .catch(err => {
            alert("Error: " + err.message);

            let errorMessage = "";
            if (err.message.includes("Please log in first")) {
                errorMessage = "❌ Unauthorized - Please log in first";
            } else if (err.message.includes("Access Denied")) {
                errorMessage = "❌ Access Denied: This is not your Customer ID.";
            } else if (err.message.includes("Customer ID must be a positive number")) {
                errorMessage = "❌ Invalid Customer ID - must be a positive number";
            } else if (err.message.includes("Customer not found")) {
                errorMessage = "❌ Customer not found with this ID";
            } else if (err.message.includes("No policies found")) {
                errorMessage = "❌ No policies found for this Customer ID";
            } else {
                errorMessage = "❌ Error loading data: " + err.message;
            }

            document.getElementById("historyTable").innerHTML =
                `<tr><td colspan='10' class='text-center text-danger'><strong>${errorMessage}</strong></td></tr>`;
        });
}
