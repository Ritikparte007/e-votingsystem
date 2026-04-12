// ===========================================
// ADMIN DASHBOARD FUNCTIONS
// ===========================================

// Tab switching
function showTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.sidebar-link').forEach(l => l.classList.remove('active'));
    document.getElementById('tab-' + tabName).classList.add('active');
    const tabBtn = document.querySelector('[data-tab="' + tabName + '"]');
    if (tabBtn) tabBtn.classList.add('active');

    // Refresh data when switching tabs
    if (tabName === 'overview') refreshOverview();
    if (tabName === 'voters') refreshVoterTable();
    if (tabName === 'candidates') refreshCandidateCards();
    if (tabName === 'results') refreshResults();
    if (tabName === 'logs') refreshLogs();
    if (tabName === 'elections') refreshElectionTab();
}

function refreshOverview() {
    const voters = getVoters();
    const candidates = getCandidates();
    const totalVotes = getTotalVotes();
    const election = getElection();

    // Update stat cards
    const el1 = document.getElementById('adminStatElections');
    const el2 = document.getElementById('adminStatVoters');
    const el3 = document.getElementById('adminStatVotes');
    const el4 = document.getElementById('adminStatCandidates');
    const el5 = document.getElementById('adminStatTurnout');

    if (el1) el1.textContent = election.isActive ? '1' : '0';
    if (el2) el2.textContent = voters.length.toLocaleString();
    if (el3) el3.textContent = totalVotes.toLocaleString();
    if (el4) el4.textContent = candidates.length;
    if (el5) {
        const turnout = voters.length > 0 ? ((totalVotes / voters.length) * 100).toFixed(1) : '0.0';
        el5.textContent = turnout + '%';
    }

    // Update vote distribution bars
    const distContainer = document.getElementById('adminVoteDist');
    if (distContainer) {
        distContainer.innerHTML = '';
        candidates.forEach(c => {
            const pct = totalVotes > 0 ? ((c.votes / totalVotes) * 100).toFixed(1) : '0.0';
            distContainer.innerHTML += `
                <div>
                    <div class="flex justify-between text-sm mb-1">
                        <span class="font-medium">${c.name} — ${c.party}</span>
                        <span class="text-gray-500">${c.votes} (${pct}%)</span>
                    </div>
                    <div class="h-3 bg-gray-100 rounded-full overflow-hidden">
                        <div class="h-full bg-${c.color}-500 rounded-full transition-all" style="width:${pct}%"></div>
                    </div>
                </div>`;
        });
    }

    // Update recent activity
    refreshRecentActivity();
}

function refreshRecentActivity() {
    const logs = getLogs().slice(0, 5);
    const container = document.getElementById('adminRecentActivity');
    if (!container) return;

    const iconMap = {
        'VOTE_CAST': { bg: 'green', icon: 'fa-vote-yea' },
        'REGISTER': { bg: 'blue', icon: 'fa-user-plus' },
        'VERIFY': { bg: 'amber', icon: 'fa-check' },
        'BLOCKED': { bg: 'red', icon: 'fa-shield' },
        'LOGIN': { bg: 'purple', icon: 'fa-right-to-bracket' },
        'LOGOUT': { bg: 'gray', icon: 'fa-sign-out-alt' },
        'LOGIN_FAILED': { bg: 'red', icon: 'fa-times' },
        'ELECTION': { bg: 'blue', icon: 'fa-calendar' },
        'CANDIDATE': { bg: 'amber', icon: 'fa-user-tie' },
        'VOTER_UPDATE': { bg: 'green', icon: 'fa-user-pen' }
    };

    container.innerHTML = logs.map(log => {
        const style = iconMap[log.action] || { bg: 'gray', icon: 'fa-circle' };
        return `<div class="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
            <div class="w-8 h-8 bg-${style.bg}-100 rounded-full flex items-center justify-center flex-shrink-0">
                <i class="fas ${style.icon} text-${style.bg}-600 text-xs"></i>
            </div>
            <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-800 truncate">${log.details}</p>
                <p class="text-xs text-gray-400">${log.timestamp} — ${log.user}</p>
            </div>
        </div>`;
    }).join('');
}

function refreshVoterTable() {
    const voters = getVoters();
    const tbody = document.getElementById('voterTableBody');
    if (!tbody) return;

    tbody.innerHTML = voters.map(v => `
        <tr class="hover:bg-gray-50 transition">
            <td class="px-6 py-4 text-sm font-medium text-gray-800">${v.name}</td>
            <td class="px-6 py-4 text-sm text-gray-600 font-mono">${v.voterId}</td>
            <td class="px-6 py-4 text-sm text-gray-600">${v.email}</td>
            <td class="px-6 py-4">
                <span class="bg-${v.status === 'verified' ? 'green' : 'yellow'}-100 text-${v.status === 'verified' ? 'green' : 'yellow'}-700 text-xs font-bold px-3 py-1 rounded-full capitalize">${v.status}</span>
            </td>
            <td class="px-6 py-4">
                ${v.hasVoted ? '<span class="text-green-500"><i class="fas fa-check-circle"></i> Yes</span>' : '<span class="text-gray-300"><i class="fas fa-minus-circle"></i> No</span>'}
            </td>
            <td class="px-6 py-4 flex space-x-1">
                ${v.status === 'pending' ? `<button onclick="verifyVoter('${v.voterId}')" class="px-2 py-1 bg-green-50 text-green-600 rounded text-xs hover:bg-green-100" title="Verify"><i class="fas fa-check"></i> Verify</button>` : ''}
                <button onclick="deleteVoter('${v.voterId}')" class="px-2 py-1 bg-red-50 text-red-600 rounded text-xs hover:bg-red-100" title="Delete"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

function verifyVoter(voterId) {
    const voters = getVoters();
    const idx = voters.findIndex(v => v.voterId === voterId);
    if (idx !== -1) {
        voters[idx].status = 'verified';
        saveVoters(voters);
        addLog('Admin', 'VERIFY', voters[idx].name + ' verified by admin');
        refreshVoterTable();
        alert('Voter ' + voters[idx].name + ' has been verified successfully.');
    }
}

function deleteVoter(voterId) {
    if (!confirm('Are you sure you want to delete this voter? This action cannot be undone.')) return;
    let voters = getVoters();
    const voter = voters.find(v => v.voterId === voterId);
    voters = voters.filter(v => v.voterId !== voterId);
    saveVoters(voters);
    addLog('Admin', 'VOTER_UPDATE', 'Voter deleted: ' + (voter ? voter.name : voterId));
    refreshVoterTable();
}

function refreshCandidateCards() {
    const candidates = getCandidates();
    const container = document.getElementById('candidateCardsContainer');
    if (!container) return;

    container.innerHTML = candidates.map(c => `
        <div class="bg-white rounded-xl border border-gray-100 shadow-sm p-5 flex items-center space-x-4">
            <div class="w-14 h-14 bg-${c.color}-500/10 rounded-full flex items-center justify-center text-2xl">${c.symbol}</div>
            <div class="flex-1">
                <h4 class="font-bold text-gray-800">${c.name}</h4>
                <p class="text-sm text-gray-500">${c.party}</p>
                <p class="text-xs text-gray-400">Votes: ${c.votes}</p>
            </div>
            <div class="flex space-x-2">
                <button onclick="deleteCandidate('${c.id}')" class="w-8 h-8 bg-red-50 rounded-lg flex items-center justify-center text-red-500 hover:bg-red-100"><i class="fas fa-trash text-xs"></i></button>
            </div>
        </div>`).join('');
}

function deleteCandidate(candId) {
    if (!confirm('Are you sure you want to delete this candidate?')) return;
    let candidates = getCandidates();
    const cand = candidates.find(c => c.id === candId);
    candidates = candidates.filter(c => c.id !== candId);
    saveCandidates(candidates);
    addLog('Admin', 'CANDIDATE', 'Candidate deleted: ' + (cand ? cand.name : candId));
    refreshCandidateCards();
}

function addCandidate() {
    const name = prompt('Enter candidate name:');
    if (!name || name.trim().length < 2) { alert('Invalid name.'); return; }
    const party = prompt('Enter party name:');
    if (!party || party.trim().length < 2) { alert('Invalid party name.'); return; }
    const symbol = prompt('Enter symbol emoji (e.g. 🌟):') || '🏛️';

    const candidates = getCandidates();
    const newId = 'C' + (candidates.length + 1 + Date.now() % 1000);
    candidates.push({ id: newId, name: name.trim(), party: party.trim(), symbol: symbol, color: 'gray', votes: 0 });
    saveCandidates(candidates);
    addLog('Admin', 'CANDIDATE', 'New candidate added: ' + name.trim());
    refreshCandidateCards();
}

function refreshResults() {
    const candidates = getCandidates().slice().sort((a, b) => b.votes - a.votes);
    const totalVotes = getTotalVotes();
    const container = document.getElementById('resultsContainer');
    if (!container) return;

    container.innerHTML = candidates.map((c, i) => {
        const pct = totalVotes > 0 ? ((c.votes / totalVotes) * 100).toFixed(1) : '0.0';
        const isLeading = i === 0 && c.votes > 0;
        return `
        <div class="flex items-center space-x-4 p-4 ${isLeading ? 'bg-amber-50 border border-amber-200' : 'bg-gray-50'} rounded-xl">
            <span class="text-3xl">${c.symbol}</span>
            <div class="flex-1">
                <div class="flex items-center space-x-2">
                    <h4 class="font-bold text-gray-800 ${isLeading ? 'text-lg' : ''}">${c.name}</h4>
                    ${isLeading ? '<span class="bg-amber-500 text-white text-xs font-bold px-2 py-0.5 rounded">LEADING</span>' : ''}
                </div>
                <p class="text-sm text-gray-500">${c.party}</p>
            </div>
            <div class="text-right">
                <p class="text-2xl font-bold ${isLeading ? 'text-amber-500' : 'text-gray-700'}">${c.votes}</p>
                <p class="text-sm text-gray-500">${pct}%</p>
            </div>
        </div>`;
    }).join('');

    const totalEl = document.getElementById('resultsTotalVotes');
    if (totalEl) totalEl.textContent = totalVotes;
}

function refreshLogs() {
    const logs = getLogs();
    const tbody = document.getElementById('logsTableBody');
    if (!tbody) return;

    const badgeColors = {
        'VOTE_CAST': 'green', 'REGISTER': 'blue', 'VERIFY': 'amber', 'BLOCKED': 'red',
        'LOGIN': 'purple', 'LOGOUT': 'gray', 'LOGIN_FAILED': 'red', 'ELECTION': 'blue',
        'CANDIDATE': 'amber', 'VOTER_UPDATE': 'green'
    };

    tbody.innerHTML = logs.map(log => {
        const color = badgeColors[log.action] || 'gray';
        return `<tr class="hover:bg-gray-50">
            <td class="px-6 py-3 text-gray-500 font-mono text-xs">${log.timestamp}</td>
            <td class="px-6 py-3 font-medium text-sm">${log.user}</td>
            <td class="px-6 py-3"><span class="bg-${color}-100 text-${color}-700 text-xs px-2 py-0.5 rounded-full font-medium">${log.action}</span></td>
            <td class="px-6 py-3 text-gray-500 text-sm">${log.details}</td>
        </tr>`;
    }).join('');
}

function refreshElectionTab() {
    const election = getElection();
    const toggleBtn = document.getElementById('electionToggleBtn');
    const statusBadge = document.getElementById('electionStatusBadge');
    if (toggleBtn) {
        toggleBtn.textContent = election.isActive ? '⏹ Stop Voting' : '▶ Start Voting';
        toggleBtn.className = election.isActive
            ? 'px-5 py-2.5 rounded-lg text-sm font-semibold transition shadow-md bg-red-500 hover:bg-red-600 text-white'
            : 'px-5 py-2.5 rounded-lg text-sm font-semibold transition shadow-md bg-green-500 hover:bg-green-600 text-white';
    }
    if (statusBadge) {
        statusBadge.innerHTML = election.isActive
            ? '<span class="bg-green-100 text-green-700 text-xs font-bold px-3 py-1 rounded-full">Active</span>'
            : '<span class="bg-red-100 text-red-700 text-xs font-bold px-3 py-1 rounded-full">Stopped</span>';
    }
}

function toggleElection() {
    const election = getElection();
    election.isActive = !election.isActive;
    saveElection(election);
    addLog('Admin', 'ELECTION', 'Voting ' + (election.isActive ? 'started' : 'stopped') + ' for ' + election.name);
    refreshElectionTab();
    alert('Voting has been ' + (election.isActive ? 'STARTED' : 'STOPPED') + '.');
}

function resetElectionData() {
    if (!confirm('⚠ Are you sure you want to RESET all election data? This will clear all votes, voters, and candidates. This cannot be undone!')) return;
    if (!confirm('This is your FINAL confirmation. All data will be permanently deleted.')) return;

    localStorage.removeItem('ovs_initialized');
    localStorage.removeItem('ovs_voters');
    localStorage.removeItem('ovs_candidates');
    localStorage.removeItem('ovs_election');
    localStorage.removeItem('ovs_logs');
    localStorage.removeItem('ovs_session');
    localStorage.removeItem('ovs_lastReceipt');

    initializeData();
    addLog('Admin', 'ELECTION', 'All election data has been reset');
    alert('Election data has been reset to defaults.');
    location.reload();
}