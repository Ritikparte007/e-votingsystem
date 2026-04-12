// ===========================================
// UI UTILITIES
// ===========================================

function updateIndexStats() {
    const voters = getVoters();
    const totalVotes = getTotalVotes();
    const election = getElection();

    animateValue('statVotes', 0, totalVotes, 1200);
    animateValue('statVoters', 0, voters.length, 1200);
    animateValue('statElections', 0, election.isActive ? 1 : 0, 800);

    // Live stats section
    const liveVotersEl = document.getElementById('liveVoters');
    const liveVotesCastEl = document.getElementById('liveVotesCast');
    const liveTurnoutEl = document.getElementById('liveTurnout');

    if (liveVotersEl) liveVotersEl.textContent = voters.length;
    if (liveVotesCastEl) liveVotesCastEl.textContent = totalVotes;
    if (liveTurnoutEl) {
        const turnout = voters.length > 0 ? Math.round((totalVotes / voters.length) * 100) : 0;
        liveTurnoutEl.textContent = turnout + '%';
    }

    // Update candidate progress bars
    updateCandidateBars();
}

function updateCandidateBars() {
    const candidates = getCandidates();
    const totalVotes = getTotalVotes();

    candidates.forEach((c, i) => {
        const bar = document.getElementById('bar' + (i + 1));
        const label = document.getElementById('barLabel' + (i + 1));
        if (bar) {
            const pct = totalVotes > 0 ? Math.round((c.votes / totalVotes) * 100) : 0;
            bar.style.width = pct + '%';
        }
        if (label) {
            const pct = totalVotes > 0 ? Math.round((c.votes / totalVotes) * 100) : 0;
            label.textContent = c.votes + ' (' + pct + '%)';
        }
    });
}

function animateValue(elementId, start, end, duration) {
    const el = document.getElementById(elementId);
    if (!el) return;
    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        el.textContent = Math.floor(progress * (end - start) + start);
        if (progress < 1) {
            window.requestAnimationFrame(step);
        }
    };
    window.requestAnimationFrame(step);
}