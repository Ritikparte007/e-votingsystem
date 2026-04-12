// ===========================================
// REGISTRATION PAGE
// ===========================================

function handleRegister(event) {
    event.preventDefault();
    const errorEl = document.getElementById('registerError');
    const successEl = document.getElementById('registerSuccess');
    errorEl.classList.add('hidden');
    successEl.classList.add('hidden');

    // Gather fields
    const fullName = document.getElementById('fullName').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const voterId = document.getElementById('voterId').value.trim().toUpperCase();
    const aadhaar = document.getElementById('aadhaar').value.trim();
    const dob = document.getElementById('dob').value;
    const state = document.getElementById('state').value;
    const address = document.getElementById('address').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const terms = document.getElementById('terms').checked;

    // 1. All fields required
    if (!fullName || !email || !phone || !voterId || !aadhaar || !dob || !state || !address || !password || !confirmPassword) {
        showError(errorEl, 'All fields are required. Please fill in every field.');
        return false;
    }

    // 2. Name validation (letters and spaces only, min 3 chars)
    if (fullName.length < 3 || !/^[a-zA-Z\s]+$/.test(fullName)) {
        showError(errorEl, 'Full name must be at least 3 characters and contain only letters.');
        return false;
    }

    // 3. Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showError(errorEl, 'Please enter a valid email address.');
        return false;
    }

    // 4. Phone validation (10 digits)
    const phoneDigits = phone.replace(/[\s+\-]/g, '');
    if (phoneDigits.length < 10 || !/^\d+$/.test(phoneDigits)) {
        showError(errorEl, 'Phone number must be at least 10 digits.');
        return false;
    }

    // 5. Voter ID validation (alphanumeric, 6-10 chars)
    if (voterId.length < 6 || voterId.length > 15 || !/^[A-Z0-9]+$/.test(voterId)) {
        showError(errorEl, 'Voter ID must be 6-15 alphanumeric characters (e.g., ABC1234567).');
        return false;
    }

    // 6. Aadhaar validation (12 digits)
    const aadhaarDigits = aadhaar.replace(/\s/g, '');
    if (aadhaarDigits.length !== 12 || !/^\d+$/.test(aadhaarDigits)) {
        showError(errorEl, 'Aadhaar number must be exactly 12 digits.');
        return false;
    }

    // 7. Age validation (must be 18+)
    const today = new Date();
    const birthDate = new Date(dob);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) age--;
    if (age < 18) {
        showError(errorEl, 'You must be at least 18 years old to register as a voter.');
        return false;
    }

    // 8. Password strength (min 8 chars, at least 1 letter and 1 number)
    if (password.length < 8) {
        showError(errorEl, 'Password must be at least 8 characters long.');
        return false;
    }
    if (!/[a-zA-Z]/.test(password) || !/\d/.test(password)) {
        showError(errorEl, 'Password must contain at least one letter and one number.');
        return false;
    }

    // 9. Password match
    if (password !== confirmPassword) {
        showError(errorEl, 'Passwords do not match. Please re-enter.');
        return false;
    }

    // 10. Terms agreement
    if (!terms) {
        showError(errorEl, 'You must agree to the Terms of Service and Privacy Policy.');
        return false;
    }

    // 11. Duplicate check — Voter ID, Email, Aadhaar
    const voters = getVoters();
    if (voters.find(v => v.voterId === voterId)) {
        showError(errorEl, 'This Voter ID is already registered. Please login or use a different Voter ID.');
        return false;
    }
    if (voters.find(v => v.email === email)) {
        showError(errorEl, 'This email is already registered. Please use a different email or login.');
        return false;
    }
    if (voters.find(v => v.aadhaar.replace(/\s/g, '') === aadhaarDigits)) {
        showError(errorEl, 'This Aadhaar number is already registered. Each citizen can register only once.');
        return false;
    }

    // All validations passed — register voter
    const newVoter = {
        id: voterId,
        name: fullName,
        email: email,
        phone: phone,
        voterId: voterId,
        aadhaar: aadhaar,
        dob: dob,
        state: state,
        address: address,
        password: password,
        status: 'pending', // requires admin verification
        hasVoted: false,
        registeredAt: new Date().toISOString()
    };

    voters.push(newVoter);
    saveVoters(voters);
    addLog(fullName, 'REGISTER', 'New voter registered — ID: ' + voterId + ' (pending verification)');

    successEl.innerHTML = '<i class="fas fa-check-circle mr-1"></i> Registration successful! Your account is <strong>pending admin verification</strong>. You will be able to vote once verified. <a href="login.html" class="underline font-semibold">Login here</a>';
    successEl.classList.remove('hidden');
    document.getElementById('registerForm').reset();
    window.scrollTo({ top: successEl.offsetTop - 100, behavior: 'smooth' });
    return false;
}

function showError(el, msg) {
    el.innerHTML = '<i class="fas fa-exclamation-circle mr-1"></i> ' + msg;
    el.classList.remove('hidden');
    window.scrollTo({ top: el.offsetTop - 100, behavior: 'smooth' });
}