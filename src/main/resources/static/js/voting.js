// ===========================================
// VOTING PAGE
// ===========================================

let selectedCandidate = null;

function selectCandidate(cardEl, candidateId) {
    // Deselect all
    document.querySelectorAll('.candidate-card').forEach(card => {
        card.classList.remove('selected');
        const radio = card.querySelector('.candidate-radio');
        if (radio) {
            radio.innerHTML = '';
            radio.classList.remove('border-[#f59e0b]');
            radio.classList.add('border-gray-300');
        }
    });

    // Select this candidate
    cardEl.classList.add('selected');
    const radio = cardEl.querySelector('.candidate-radio');
    if (radio) {
        radio.innerHTML = '<div class="w-3 h-3 bg-[#f59e0b] rounded-full"></div>';
        radio.classList.remove('border-gray-300');
        radio.classList.add('border-[#f59e0b]');
    }

    selectedCandidate = candidateId;
    const castBtn = document.getElementById('castVoteBtn');
    if (castBtn) castBtn.disabled = false;
}

function openConfirmModal() {
    if (!selectedCandidate) {
        alert('Please select a candidate before casting your vote.');
        return;
    }

    const candidates = getCandidates();
    const candidate = candidates.find(c => c.id === selectedCandidate);
    if (candidate) {
        document.getElementById('confirmCandidateName').textContent = candidate.name;
        document.getElementById('confirmCandidateParty').textContent = candidate.party;
    }
    document.getElementById('confirmModal').classList.remove('hidden');
}

function closeConfirmModal() {
    document.getElementById('confirmModal').classList.add('hidden');
}

function submitVote() {
    const session = getCurrentSession();
    if (!session || session.role !== 'voter') {
        alert('Session expired. Please login again.');
        window.location.href = 'login.html';
        return;
    }

    // Double-check: voter hasn't already voted
    const voters = getVoters();
    const voterIndex = voters.findIndex(v => v.voterId === session.userId);
    if (voterIndex === -1) {
        alert('Voter not found. Please login again.');
        window.location.href = 'login.html';
        return;
    }
    if (voters[voterIndex].hasVoted) {
        alert('You have already cast your vote. Duplicate voting is not allowed.');
        addLog(session.name, 'BLOCKED', 'Duplicate vote attempt blocked — ' + session.userId);
        window.location.href = 'index.html';
        return;
    }

    // Check election is still active
    const election = getElection();
    if (!election.isActive) {
        alert('Voting has been closed by the administrator.');
        window.location.href = 'index.html';
        return;
    }

    // Record the vote
    const candidates = getCandidates();
    const candIndex = candidates.findIndex(c => c.id === selectedCandidate);
    if (candIndex === -1) {
        alert('Invalid candidate selection.');
        return;
    }

    candidates[candIndex].votes += 1;
    saveCandidates(candidates);

    // Mark voter as voted
    voters[voterIndex].hasVoted = true;
    voters[voterIndex].votedAt = new Date().toISOString();
    saveVoters(voters);

    // Generate receipt
    const receiptId = 'VR-' + Date.now().toString(36).toUpperCase();
    const timestamp = new Date().toLocaleString('en-IN', {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit', second: '2-digit'
    });

    localStorage.setItem('ovs_lastReceipt', JSON.stringify({
        receiptId: receiptId,
        timestamp: timestamp,
        voterId: session.userId,
        voterName: session.name,
        election: election.name,
        constituency: election.constituency
    }));

    // Log the vote
    addLog(session.name, 'VOTE_CAST', 'Vote recorded for ' + election.name + ' — Receipt: ' + receiptId);

    window.location.href = 'receipt.html';
}