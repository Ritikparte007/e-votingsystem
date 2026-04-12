// ===========================================
// AUTHENTICATION & LOGIN PAGE
// ===========================================

function getCurrentSession() {
    const session = localStorage.getItem('ovs_session');
    return session ? JSON.parse(session) : null;
}

function setSession(role, userId, name) {
    localStorage.setItem('ovs_session', JSON.stringify({ role, userId, name, loginTime: new Date().toISOString() }));
}

function clearSession() {
    localStorage.removeItem('ovs_session');
}

// ---- Page Protection (Access Control) ----
function protectPage() {
    const path = window.location.pathname;
    const session = getCurrentSession();

    // Vote page requires voter session
    if (path.includes('vote.html')) {
        if (!session || session.role !== 'voter') {
            alert('Access Denied: Please login as a voter to access the voting page.');
            window.location.href = 'login.html';
            return;
        }
        // Check if voter already voted
        const voters = getVoters();
        const voter = voters.find(v => v.voterId === session.userId);
        if (voter && voter.hasVoted) {
            alert('You have already cast your vote. Duplicate voting is not allowed.');
            window.location.href = 'index.html';
            return;
        }
        // Check if election is active
        const election = getElection();
        if (!election.isActive) {
            alert('Voting is currently closed. Please check back later.');
            window.location.href = 'index.html';
            return;
        }
        // Show voter name in navbar
        const voterNameEl = document.getElementById('voterNameDisplay');
        if (voterNameEl) voterNameEl.textContent = 'Welcome, ' + session.name;
    }

    // Admin page requires admin session
    if (path.includes('admin.html')) {
        if (!session || session.role !== 'admin') {
            alert('Access Denied: Admin privileges required.');
            window.location.href = 'login.html';
            return;
        }
    }

    // Receipt page — show receipt data
    if (path.includes('receipt.html')) {
        if (!session) {
            window.location.href = 'login.html';
            return;
        }
    }
}

// ---- Logout ----
function logout() {
    const session = getCurrentSession();
    if (session) {
        addLog(session.name || session.userId, 'LOGOUT', session.role + ' logged out');
    }
    clearSession();
    window.location.href = 'index.html';
}

function switchRole(role) {
    const tabVoter = document.getElementById('tabVoter');
    const tabAdmin = document.getElementById('tabAdmin');
    const loginIdLabel = document.getElementById('loginIdLabel');
    const loginIdIcon = document.getElementById('loginIdIcon');
    const loginId = document.getElementById('loginId');
    const loginRole = document.getElementById('loginRole');

    if (!tabVoter) return;

    if (role === 'voter') {
        tabVoter.className = 'flex-1 py-2.5 text-sm font-semibold rounded-lg bg-[#1a3c5e] text-white transition';
        tabAdmin.className = 'flex-1 py-2.5 text-sm font-semibold rounded-lg text-gray-500 hover:text-gray-700 transition';
        loginIdLabel.textContent = 'Voter ID or Email';
        loginIdIcon.className = 'fas fa-id-card';
        loginId.placeholder = 'Enter Voter ID or email';
        loginRole.value = 'voter';
    } else {
        tabAdmin.className = 'flex-1 py-2.5 text-sm font-semibold rounded-lg bg-[#1a3c5e] text-white transition';
        tabVoter.className = 'flex-1 py-2.5 text-sm font-semibold rounded-lg text-gray-500 hover:text-gray-700 transition';
        loginIdLabel.textContent = 'Admin Username';
        loginIdIcon.className = 'fas fa-user-shield';
        loginId.placeholder = 'Enter admin username';
        loginRole.value = 'admin';
    }
}

function togglePassword() {
    const passField = document.getElementById('loginPassword');
    const eyeIcon = document.getElementById('eyeIcon');
    if (passField.type === 'password') {
        passField.type = 'text';
        eyeIcon.className = 'fas fa-eye-slash';
    } else {
        passField.type = 'password';
        eyeIcon.className = 'fas fa-eye';
    }
}

function handleLogin(event) {
    event.preventDefault();
    const loginIdVal = document.getElementById('loginId').value.trim();
    const password = document.getElementById('loginPassword').value;
    const role = document.getElementById('loginRole').value;
    const errorEl = document.getElementById('loginError');

    // Validation: non-empty fields
    if (!loginIdVal || !password) {
        errorEl.textContent = 'Please fill in all fields.';
        errorEl.classList.remove('hidden');
        return false;
    }

    if (role === 'admin') {
        // Admin login
        const admin = JSON.parse(localStorage.getItem('ovs_admin') || '{}');
        if (loginIdVal === admin.username && password === admin.password) {
            setSession('admin', 'admin', 'Admin');
            addLog('Admin', 'LOGIN', 'Admin logged in successfully');
            window.location.href = 'admin.html';
            return false;
        } else {
            errorEl.textContent = 'Invalid admin credentials. Please try again.';
            errorEl.classList.remove('hidden');
            addLog(loginIdVal, 'LOGIN_FAILED', 'Failed admin login attempt');
            return false;
        }
    } else {
        // Voter login — match by voterId or email
        const voters = getVoters();
        const voter = voters.find(v => (v.voterId === loginIdVal || v.email === loginIdVal) && v.password === password);

        if (!voter) {
            errorEl.textContent = 'Invalid Voter ID/Email or password. Please try again.';
            errorEl.classList.remove('hidden');
            addLog(loginIdVal, 'LOGIN_FAILED', 'Failed voter login attempt');
            return false;
        }

        // Check if voter is verified
        if (voter.status !== 'verified') {
            errorEl.textContent = 'Your account is pending admin verification. Please wait for approval.';
            errorEl.classList.remove('hidden');
            return false;
        }

        setSession('voter', voter.voterId, voter.name);
        addLog(voter.name, 'LOGIN', 'Voter logged in — ID: ' + voter.voterId);
        window.location.href = 'vote.html';
        return false;
    }
}