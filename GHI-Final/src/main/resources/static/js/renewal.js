// Auto-fetch policy details when Policy ID changes
document.getElementById("policyId").addEventListener("change", async function () {
    const policyId = this.value;
    if (!policyId) return;

    try {
        const response = await fetch(`/policies/${policyId}`); 
        if (!response.ok) throw new Error("Policy not found");

        const policy = await response.json();

        // Fill amount
        document.getElementById("amount").value = policy.premiumAmount;

        // Fill renewal option dropdown (term)
        const optionSelect = document.getElementById("option");
        optionSelect.innerHTML = ""; // clear existing

        if (policy.term === "HALF_YEARLY") {
            optionSelect.innerHTML = `<option value="half-yearly" selected>Half-Yearly</option>`;
        } else if (policy.term === "YEARLY") {
            optionSelect.innerHTML = `<option value="yearly" selected>Yearly</option>`;
        } else {
            optionSelect.innerHTML = `
                <option value="half-yearly">Half-Yearly</option>
                <option value="yearly">Yearly</option>`;
        }

        // Optional helper text
        const help = document.getElementById("policyHelp");
        if (help) {
            help.textContent = `Policy: ${policy.name}, Coverage: ${policy.coverageAmount}`;
        }
    } catch (err) {
        const help = document.getElementById("policyHelp");
        if (help) help.textContent = "Invalid Policy ID";

        document.getElementById("amount").value = "";
        document.getElementById("option").innerHTML = "<option disabled selected>Choose...</option>";
    }
});


/*document.getElementById("renewalForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const policyId = document.getElementById("policyId").value;
    const amount = document.getElementById("amount").value;
    const paymentMode = document.getElementById("paymentMode").value;
    const option = document.getElementById("option").value;
	const customerId = document.getElementById("customerId").value;

	const data = {
	    customerId: customerId,
	    policyId: policyId,
	    amount: amount,
	    paymentMode: paymentMode,
	    option: option
	};


    // Save request in localStorage so we can use it in gateway.html
    localStorage.setItem("paymentRequest", JSON.stringify(data));
    window.location.href = "gateway";
});*/

// Form submit handler
document.getElementById("renewalForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const policyId = document.getElementById("policyId").value;
    const amount = document.getElementById("amount").value;
    const paymentMode = document.getElementById("paymentMode").value;
    const option = document.getElementById("option").value;
    const customerId = document.getElementById("customerId").value;

    const data = {
        customerId,
        policyId,
        amount,
        paymentMode,
        option
    };

    // Saving needed + payment mode temporarily
    sessionStorage.setItem("paymentRequest", JSON.stringify(data));

    // Redirect to gateway page
    window.location.href = "gateway";
});
