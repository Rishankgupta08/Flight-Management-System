/**
 * Flights Page JavaScript
 */

let currentFlight = null;
let allAirports = [];

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    updateNavigation();
    loadAllFlights();
    loadAirports();
    checkAdminStaffAccess();
    
    // Setup search form
    document.getElementById('searchForm').addEventListener('submit', handleSearch);
    
    // Setup booking seats input
    const seatsInput = document.getElementById('seatsBooked');
    if (seatsInput) {
        seatsInput.addEventListener('input', updateTotalPrice);
    }
});

async function loadAllFlights() {
    try {
        const response = await fetch(`${API_BASE}/flight/list`);
        const result = await response.json();
        
        if (result.success) {
            displayFlights(result.data);
        } else {
            document.getElementById('flightsList').innerHTML = 
                `<p class="error">${result.error}</p>`;
        }
    } catch (error) {
        console.error('Error loading flights:', error);
        document.getElementById('flightsList').innerHTML = 
            '<p class="error">Failed to load flights</p>';
    }
}

async function handleSearch(event) {
    event.preventDefault();
    
    const from = document.getElementById('searchFrom').value.trim();
    const to = document.getElementById('searchTo').value.trim();
    const date = document.getElementById('searchDate').value;
    
    try {
        const params = new URLSearchParams();
        if (from) params.append('from', from);
        if (to) params.append('to', to);
        if (date) params.append('date', date);
        
        const response = await fetch(`${API_BASE}/flight/search?${params}`);
        const result = await response.json();
        
        if (result.success) {
            displayFlights(result.data);
        } else {
            showNotification(result.error, 'error');
        }
    } catch (error) {
        console.error('Error searching flights:', error);
        showNotification('Search failed', 'error');
    }
}

function displayFlights(flights) {
    const container = document.getElementById('flightsList');
    
    if (flights.length === 0) {
        container.innerHTML = '<p class="no-data">No flights found</p>';
        return;
    }
    
    const user = getCurrentUser();
    const canManage = user && (user.role === 'ADMIN' || user.role === 'STAFF');
    
    const html = flights.map(flight => `
        <div class="flight-card">
            <div class="flight-header">
                <span class="flight-number">${flight.flightNumber}</span>
                <span class="status-${flight.status.toLowerCase()}">${flight.status}</span>
            </div>
            <div class="flight-route">
                <div class="route-point">
                    <strong>${flight.sourceAirportCode}</strong>
                    <br><small>${flight.sourceAirportName}</small>
                </div>
                <div class="route-arrow">✈️</div>
                <div class="route-point">
                    <strong>${flight.destinationAirportCode}</strong>
                    <br><small>${flight.destinationAirportName}</small>
                </div>
            </div>
            <div class="flight-details">
                <div>
                    <strong>Departure:</strong><br>
                    ${formatDateTime(flight.departureTime)}
                </div>
                <div>
                    <strong>Arrival:</strong><br>
                    ${formatDateTime(flight.arrivalTime)}
                </div>
                <div>
                    <strong>Seats Available:</strong><br>
                    ${flight.seatsAvailable}
                </div>
                <div>
                    <strong>Price:</strong><br>
                    ${formatCurrency(flight.price)}
                </div>
            </div>
            <div class="flight-actions">
                ${user && flight.status === 'SCHEDULED' && flight.seatsAvailable > 0 ? 
                    `<button class="btn btn-primary" onclick='bookFlight(${JSON.stringify(flight)})'>Book Now</button>` : ''}
                ${canManage ? 
                    `<button class="btn btn-secondary" onclick='editFlight(${JSON.stringify(flight)})'>Edit</button>
                     <button class="btn btn-warning" onclick="cancelFlight(${flight.id})">Cancel</button>
                     <button class="btn btn-danger" onclick="deleteFlight(${flight.id})">Delete</button>` : ''}
            </div>
        </div>
    `).join('');
    
    container.innerHTML = html;
}

async function loadAirports() {
    try {
        const response = await fetch(`${API_BASE}/airport/list`);
        const result = await response.json();
        
        if (result.success) {
            allAirports = result.data;
            populateAirportSelects();
        }
    } catch (error) {
        console.error('Error loading airports:', error);
    }
}

function populateAirportSelects() {
    const sourceSelect = document.getElementById('sourceAirportId');
    const destSelect = document.getElementById('destinationAirportId');
    
    if (!sourceSelect || !destSelect) return;
    
    const options = allAirports.map(airport => 
        `<option value="${airport.id}">${airport.code} - ${airport.name}</option>`
    ).join('');
    
    sourceSelect.innerHTML = options;
    destSelect.innerHTML = options;
}

function checkAdminStaffAccess() {
    const user = getCurrentUser();
    if (user && (user.role === 'ADMIN' || user.role === 'STAFF')) {
        document.getElementById('addFlightSection').style.display = 'block';
    }
}

function showAddFlightModal() {
    document.getElementById('modalTitle').textContent = 'Add New Flight';
    document.getElementById('flightForm').reset();
    document.getElementById('flightId').value = '';
    currentFlight = null;
    document.getElementById('flightModal').classList.add('active');
}

function editFlight(flight) {
    currentFlight = flight;
    document.getElementById('modalTitle').textContent = 'Edit Flight';
    document.getElementById('flightId').value = flight.id;
    document.getElementById('flightNumber').value = flight.flightNumber;
    document.getElementById('sourceAirportId').value = flight.sourceAirportId;
    document.getElementById('destinationAirportId').value = flight.destinationAirportId;
    
    // Format dates for datetime-local input
    const departure = new Date(flight.departureTime);
    const arrival = new Date(flight.arrivalTime);
    document.getElementById('departureTime').value = formatDateTimeLocal(departure);
    document.getElementById('arrivalTime').value = formatDateTimeLocal(arrival);
    
    document.getElementById('seatsAvailable').value = flight.seatsAvailable;
    document.getElementById('price').value = flight.price;
    
    document.getElementById('flightModal').classList.add('active');
}

function formatDateTimeLocal(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function closeFlightModal() {
    document.getElementById('flightModal').classList.remove('active');
}

async function handleFlightSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const isEdit = !!formData.get('id');
    
    try {
        const url = isEdit ? '/flight/update' : '/flight/create';
        const response = await fetch(`${API_BASE}${url}`, {
            method: isEdit ? 'PUT' : 'POST',
            body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
            showNotification(result.message, 'success');
            closeFlightModal();
            loadAllFlights();
        } else {
            showNotification(result.error, 'error');
        }
    } catch (error) {
        console.error('Error saving flight:', error);
        showNotification('Failed to save flight', 'error');
    }
}

async function cancelFlight(flightId) {
    if (!confirm('Are you sure you want to cancel this flight?')) return;
    
    try {
        const response = await fetch(`${API_BASE}/flight/cancel/${flightId}`, {
            method: 'PUT'
        });
        
        const result = await response.json();
        
        if (result.success) {
            showNotification('Flight cancelled successfully', 'success');
            loadAllFlights();
        } else {
            showNotification(result.error, 'error');
        }
    } catch (error) {
        console.error('Error cancelling flight:', error);
        showNotification('Failed to cancel flight', 'error');
    }
}

async function deleteFlight(flightId) {
    if (!confirm('Are you sure you want to delete this flight? This cannot be undone.')) return;
    
    try {
        const response = await fetch(`${API_BASE}/flight/${flightId}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.success) {
            showNotification('Flight deleted successfully', 'success');
            loadAllFlights();
        } else {
            showNotification(result.error, 'error');
        }
    } catch (error) {
        console.error('Error deleting flight:', error);
        showNotification('Failed to delete flight', 'error');
    }
}

function bookFlight(flight) {
    if (!isAuthenticated()) {
        showNotification('Please login to book flights', 'error');
        setTimeout(() => window.location.href = 'login.html', 1500);
        return;
    }
    
    currentFlight = flight;
    document.getElementById('bookingFlightId').value = flight.id;
    document.getElementById('seatsBooked').value = 1;
    document.getElementById('seatsBooked').max = flight.seatsAvailable;
    
    const infoHtml = `
        <div class="flight-info">
            <h3>${flight.flightNumber}</h3>
            <p>${flight.sourceAirportCode} → ${flight.destinationAirportCode}</p>
            <p><strong>Departure:</strong> ${formatDateTime(flight.departureTime)}</p>
            <p><strong>Price per seat:</strong> ${formatCurrency(flight.price)}</p>
            <p><strong>Available seats:</strong> ${flight.seatsAvailable}</p>
        </div>
    `;
    
    document.getElementById('bookingFlightInfo').innerHTML = infoHtml;
    updateTotalPrice();
    document.getElementById('bookingModal').classList.add('active');
}

function updateTotalPrice() {
    if (!currentFlight) return;
    
    const seats = parseInt(document.getElementById('seatsBooked').value) || 0;
    const total = seats * currentFlight.price;
    
    document.getElementById('totalPriceDisplay').innerHTML = 
        `<strong>Total Price: ${formatCurrency(total)}</strong>`;
}

function closeBookingModal() {
    document.getElementById('bookingModal').classList.remove('active');
}

async function handleBookingSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    
    try {
        const response = await fetch(`${API_BASE}/booking/create`, {
            method: 'POST',
            body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
            showNotification('Booking confirmed successfully!', 'success');
            closeBookingModal();
            setTimeout(() => window.location.href = 'bookings.html', 1500);
        } else {
            showNotification(result.error, 'error');
        }
    } catch (error) {
        console.error('Error creating booking:', error);
        showNotification('Failed to create booking', 'error');
    }
}
