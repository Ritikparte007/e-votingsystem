// ============================================
// GLOBAL APP LOGIC
// ============================================

document.addEventListener('DOMContentLoaded', function () {
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const mobileMenu = document.getElementById('mobileMenu');
    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', function () {
            mobileMenu.classList.toggle('hidden');
        });
    }

    // Initialize index page stats
    if (document.getElementById('statVotes')) {
        updateIndexStats();
    }

    // Protect pages requiring authentication
    if (typeof protectPage === 'function') protectPage();
});

// Auto-refresh stats every 3 seconds
if (document.getElementById('statVotes')) {
    setInterval(function () {
        const voters = getVoters();
        const totalVotes = getTotalVotes();
        const liveVotersEl = document.getElementById('liveVoters');
        const liveVotesCastEl = document.getElementById('liveVotesCast');
        const liveTurnoutEl = document.getElementById('liveTurnout');

        if (liveVotersEl) liveVotersEl.textContent = voters.length;
        if (liveVotesCastEl) liveVotesCastEl.textContent = totalVotes;
        if (liveTurnoutEl) {
            const turnout = voters.length > 0 ? Math.round((totalVotes / voters.length) * 100) : 0;
            liveTurnoutEl.textContent = turnout + '%';
        }

        const statVotesEl = document.getElementById('statVotes');
        const statVotersEl = document.getElementById('statVoters');
        
        if (statVotesEl) statVotesEl.textContent = totalVotes;
        if (statVotersEl) statVotersEl.textContent = voters.length;

        if (typeof updateCandidateBars === 'function') updateCandidateBars();
    }, 3000);
}

function switchRole(role) {
    const voterTab = document.getElementById("tabVoter");
    const adminTab = document.getElementById("tabAdmin");
    const roleInput = document.getElementById("loginRole");

    if (role === "admin") {
        adminTab.classList.add("bg-[#1a3c5e]", "text-white");
        voterTab.classList.remove("bg-[#1a3c5e]", "text-white");

        roleInput.value = "admin";
    } else {
        voterTab.classList.add("bg-[#1a3c5e]", "text-white");
        adminTab.classList.remove("bg-[#1a3c5e]", "text-white");

        roleInput.value = "voter";
    }
}