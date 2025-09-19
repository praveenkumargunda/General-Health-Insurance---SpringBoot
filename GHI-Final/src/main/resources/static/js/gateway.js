document.addEventListener("DOMContentLoaded", function () {
    // Getting the payment request from renewal.html
    const request = JSON.parse(sessionStorage.getItem("paymentRequest"));
    const container = document.getElementById("paymentFields");

    if (!request) {
        container.innerHTML = "<p>No payment information found. Please go back to the renewal page.</p>";
        document.getElementById("confirmPayment").disabled = true;
        return;
    }

    // Show payment mode info
    let html = `<p><strong>Payment Mode:</strong> ${request.paymentMode}</p>`;

    // Dynamically rendering input fields based on payment mode
    if (request.paymentMode === "Credit Card" || request.paymentMode === "Debit Card") {
        html += `
          <div class="mb-3">
            <label class="form-label">Card Number</label>
            <input type="text" class="form-control payment-input" id="cardNumber" maxlength="16" placeholder="1234567812345678">
          </div>
          <div class="mb-3">
            <label class="form-label">Expiry Date</label>
            <input type="month" class="form-control payment-input" id="expiryDate">
          </div>
          <div class="mb-3">
            <label class="form-label">CVV</label>
            <input type="password" class="form-control payment-input" id="cvv" maxlength="3" placeholder="123">
          </div>
        `;
    } else if (request.paymentMode === "Net Banking") {
        html += `
          <div class="mb-3">
            <label class="form-label">Bank Name</label>
            <input type="text" class="form-control payment-input" id="bankName" placeholder="HDFC / SBI / ICICI">
          </div>
          <div class="mb-3">
            <label class="form-label">Account Number</label>
            <input type="text" class="form-control payment-input" id="accountNumber" placeholder="Enter account number">
          </div>
        `;
    } else if (request.paymentMode === "UPI") {
        html += `
          <div class="mb-3">
            <label class="form-label">UPI ID</label>
            <input type="text" class="form-control payment-input" id="upiId" placeholder="example@upi">
          </div>
        `;
    }

    container.innerHTML = html;

    // Disable confirm button initially
    const confirmBtn = document.getElementById("confirmPayment");
    confirmBtn.disabled = true;

    // Enable button only when all inputs are filled
    const paymentInputs = document.querySelectorAll(".payment-input");
    paymentInputs.forEach(input => {
        input.addEventListener("input", () => {
            const allFilled = Array.from(paymentInputs).every(inp => inp.value.trim() !== "");
            confirmBtn.disabled = !allFilled;
        });
    });
});

// Confirm payment button click
document.getElementById("confirmPayment").addEventListener("click", function () {
    const request = JSON.parse(sessionStorage.getItem("paymentRequest"));

    if (!request) {
        alert("Payment information missing! Please go back to renewal page.");
        return;
    }

    // Collect payment inputs
    const paymentInputs = document.querySelectorAll(".payment-input");
    const paymentDetails = {};
    let valid = true;

    paymentInputs.forEach(input => {
        if (!input.value.trim()) valid = false;
        paymentDetails[input.id] = input.value.trim();
    });

    if (!valid) {
        alert("Please fill all required fields!");
        return;
    }

    // Validations for each payment mode are mentioned here
    if (request.paymentMode === "Credit Card" || request.paymentMode === "Debit Card") {
        if (!/^\d{16}$/.test(paymentDetails.cardNumber)) {
            alert("Invalid card number. Must be 16 digits.");
            return;
        }
        const today = new Date();
        if (!paymentDetails.expiryDate) {
            alert("Please enter expiry date.");
            return;
        }
        const [year, month] = paymentDetails.expiryDate.split("-");
        const expiry = new Date(year, month - 1);
        if (expiry < today) {
            alert("Card has expired.");
            return;
        }
        if (!/^\d{3}$/.test(paymentDetails.cvv)) {
            alert("Invalid CVV. Must be 3 digits.");
            return;
        }
    }

    if (request.paymentMode === "Net Banking") {
        if (!/^[A-Za-z]{2,20}$/.test(paymentDetails.bankName)) {
            alert("Invalid bank name.");
            return;
        }
        if (!/^\d{9,18}$/.test(paymentDetails.accountNumber)) {
            alert("Invalid account number. Must be 9–18 numeric digits.");
            return;
        }
    }

    if (request.paymentMode === "UPI") {
        if (!/^[a-zA-Z0-9._-]+@[a-zA-Z]+$/.test(paymentDetails.upiId)) {
            alert("Invalid UPI ID format (example: john123@icici).");
            return;
        }
    }

    // Sending only essentials to backend
    fetch("/renewals/pay", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(request)
    })
    .then(res => res.json())
    .then(data => {
        if (data.message) alert(data.message);
        if (data.nextPremiumDate) {
            alert("Next Premium Date: " + data.nextPremiumDate);
            sessionStorage.removeItem("paymentRequest");
            window.location.href = "/customer/dashboard";
        }
    })
    .catch(err => {
        console.error(err);
        alert("Payment failed!");
    });
});
