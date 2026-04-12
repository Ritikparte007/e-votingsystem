// ============================================
// INITIALIZATION
// ============================================

function initializeData() {
    if (!localStorage.getItem('ovs_initialized')) {
        // Default voters
        const voters = [
            { id: 'VOT001', name: 'Arjun Mehta', email: 'arjun@email.com', phone: '9876543210', voterId: 'VOT001', aadhaar: '1234 5678 9012', dob: '1995-05-15', state: 'Delhi', address: '42 Connaught Place, New Delhi', password: 'voter123', status: 'verified', hasVoted: false, registeredAt: '2026-03-10T10:00:00' },
            { id: 'VOT002', name: 'Kavya Reddy', email: 'kavya@email.com', phone: '9876543211', voterId: 'VOT002', aadhaar: '2345 6789 0123', dob: '1998-08-22', state: 'Karnataka', address: '15 MG Road, Bangalore', password: 'voter123', status: 'verified', hasVoted: false, registeredAt: '2026-03-11T14:30:00' }
        ];
        localStorage.setItem('ovs_voters', JSON.stringify(voters));

        // Default candidates
        const candidates = [
            { id: 'C1', name: 'Rajesh Kumar', party: 'National Democratic Party', symbol: '🏆', color: 'saffron', votes: 0 },
            { id: 'C2', name: 'Priya Sharma', party: "People's Progressive Alliance", symbol: '🌟', color: 'green', votes: 0 },
            { id: 'C3', name: 'Amit Patel', party: 'United Citizens Front', symbol: '🔔', color: 'blue', votes: 0 },
            { id: 'C4', name: 'Sunita Devi', party: 'Independent', symbol: '⭐', color: 'purple', votes: 0 }
        ];
        localStorage.setItem('ovs_candidates', JSON.stringify(candidates));

        // Election state
        const election = {
            name: 'General Assembly Election 2026',
            startDate: '2026-03-15',
            endDate: '2026-03-30',
            constituency: 'Central Delhi - 01',
            isActive: true
        };
        localStorage.setItem('ovs_election', JSON.stringify(election));

        // Activity logs
        localStorage.setItem('ovs_logs', JSON.stringify([]));
        // Admin credentials
        localStorage.setItem('ovs_admin', JSON.stringify({ username: 'admin', password: 'admin123' }));
        localStorage.setItem('ovs_initialized', 'true');
    }
}

// Initialize on every page load
initializeData();