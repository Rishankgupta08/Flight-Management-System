/**
 * Airports Page JavaScript
 */

let currentAirport = null;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    updateNavigation();
    loadAllAirports();
    checkAdminAccess();
});

async function loadAllAirports() {
    try {
        const response = await fetch(`${API_BASE}/airport/list`);
        const result = await response.json();
        
        if (result.success) {
            displayAirports(result.data);
        } else {
            document.getElementById('airportsList').innerHTML = 
                `<p class="error">${result.error}</p>`;
        }
    } catch (error) {
        console.error('Error loading airports:', error);
        document.getElementById('airportsList').innerHTML = 
            '<p class="error">Failed to load airports</p>';
    }
}

async function searchAirports(event) {
    event.preventDefault();
    
    const query = document.getElementById('searchQuery').value.trim();
    
    if (!query) {
        loadAllAirports();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/airport/search?q=${encodeURIComponent(query)}`);
        const result = await response.json();
        
        if (result.success) {
            displayAirports(result.data);
        } else {
            showNotification(result.error, 'error');
        }
    } catch (error) {
        console.error('Error searching airports:', error);
        showNotification('Search failed', 'error');
    }
}

function displayAirports(airports) {
    const container = document.getElementById('airportsList');
    
    if (airports.length === 0) {
        container.innerHTML = '<p class="no-data">No airports found</p>';
        return;
    }
    
    const user = getCurrentUser();
    const isAdmin = user && user.role === 'ADMIN';
    
    const html = airports.map(airport => `
        <div class="airport-card">
            <div class="airport-code">${airport.code}</div>
            <div class="airport-name">${airport.name}</div>
            <p><strong>City:</strong> ${airport.city}</p>
            <p><strong>Country:</strong> ${airport.country}</p>
            ${isAdmin ? `
                <div class="airport-actions">
                    <button class="btn btn-secondary" onclick='editAirport(${JSON.stringify(airport)})'>
                        Edit
                    </button>
                    <button class="btn btn-danger" onclick="deleteAirport(${airport.id})">
                        Delete
                    </button>
                </div>
            ` : ''}
        </div>
    `).join('');
    
    container.innerHTML = html;
}

function checkAdminAccess() {
    const user = getCurrentUser();
    if (user && user.role === 'ADMIN') {
        document.getElementById('addAirportSection').style.display = 'block';
    }
}

function showAddAirportModal() {
    document.getElementById('modalTitle').textContent = 'Add New Airport';
    document.getElementById('airportForm').reset();
    document.getElementById('airportId').value = '';
    document.getElementById('airportCode').removeAttribute('readonly');
    currentAirport = null;
    document.getElementById('airportModal').classList.add('active');
}

function editAirport(airport) {
    currentAirport = airport;
    document.getElementById('modalTitle').textContent = 'Edit Airport';
    document.getElementById('airportId').value = airport.id;
    document.getElementById('airportName').value = airport.name;
    document.getElementById('airportCode').value = airport.code;
    document.getElementById('airportCode').setAttribute('readonly', 'readonly');
    document.getElementById('airportCity').value = airport.city;
    document.getElementById('airportCountry').value = airport.country;
    
    document.getElementById('airportModal').classList.add('active');
}

function closeAirportModal() {
    document.getElementById('airportModal').classList.remove('active');
    document.getElementById('airportCode').removeAttribute('readonly');
}

async function handleAirportSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const isEdit = !!formData.get('id');
    
    // Convert code to uppercase
    const code = formData.get('code');
    formData.set('code', code.toUpperCase());
    
    try {
        const url = isEdit ? '/airport/update' : '/airport/create';
        const response = await fetch(`${API_BASE}${url}`, {
            method: isEdit ? 'PUT' : 'POST',
            body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
            showNotification(result.message, 'success');
            closeAirportModal();
            loadAllAirports();
        } else {
            showNotification(result.error, 'error');
        }
    } catch (error) {
        console.error('Error saving airport:', error);
        showNotification('Failed to save airport', 'error');
    }
}

async function deleteAirport(airportId) {
    if (!confirm('Are you sure you want to delete this airport? This may affect existing flights.')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/airport/${airportId}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.success) {
            showNotification('Airport deleted successfully', 'success');
            loadAllAirports();
        } else {
            showNotification(result.error, 'error');
        }
    } catch (error) {
        console.error('Error deleting airport:', error);
        showNotification('Failed to delete airport', 'error');
    }
}
