// ============================================
// STORAGE UTILITIES
// ============================================

function getVoters() { return JSON.parse(localStorage.getItem('ovs_voters') || '[]'); }
function saveVoters(v) { localStorage.setItem('ovs_voters', JSON.stringify(v)); }
function getCandidates() { return JSON.parse(localStorage.getItem('ovs_candidates') || '[]'); }
function saveCandidates(c) { localStorage.setItem('ovs_candidates', JSON.stringify(c)); }
function getElection() { return JSON.parse(localStorage.getItem('ovs_election') || '{}'); }
function saveElection(e) { localStorage.setItem('ovs_election', JSON.stringify(e)); }
function getLogs() { return JSON.parse(localStorage.getItem('ovs_logs') || '[]'); }
function saveLogs(l) { localStorage.setItem('ovs_logs', JSON.stringify(l)); }

function addLog(user, action, details) {
    const logs = getLogs();
    logs.unshift({
        timestamp: new Date().toLocaleString('en-IN', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false }),
        isoTime: new Date().toISOString(),
        user: user,
        action: action,
        details: details
    });
    // Keep max 100 logs
    if (logs.length > 100) logs.pop();
    saveLogs(logs);
}

function getTotalVotes() {
    return getCandidates().reduce((sum, c) => sum + c.votes, 0);
}