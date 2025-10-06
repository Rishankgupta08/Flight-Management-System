/**
 * Main JavaScript - Common utilities and functions
 */

const API_BASE = '/AirportManagementSystem';

// Authentication helpers
function getCurrentUser() {
    const userStr = sessionStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
}

function isAuthenticated() {
    return getCurrentUser() !== null;
}

function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = 'login.html';
    }
}

function hasRole(...roles) {
    const user = getCurrentUser();
    return user && roles.includes(user.role);
}

// Logout function
async function logout() {
    try {
        await fetch(`${API_BASE}/auth/logout`, { method: 'POST' });
        sessionStorage.removeItem('currentUser');
        showNotification('Logged out successfully', 'success');
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1000);
    } catch (error) {
        console.error('Logout error:', error);
        sessionStorage.removeItem('currentUser');
        window.location.href = 'index.html';
    }
}

// Update navigation based on authentication
function updateNavigation() {
    const isAuth = isAuthenticated();
    const user = getCurrentUser();
    
    const navLogin = document.getElementById('navLogin');
    const navLogout = document.getElementById('navLogout');
    const navDashboard = document.getElementById('navDashboard');
    const navBookings = document.getElementById('navBookings');
    
    if (navLogin) navLogin.style.display = isAuth ? 'none' : 'block';
    if (navLogout) navLogout.style.display = isAuth ? 'block' : 'none';
    if (navDashboard) navDashboard.style.display = isAuth ? 'block' : 'none';
    if (navBookings) navBookings.style.display = isAuth ? 'block' : 'none';
}

// Notification system
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Format date/time
function formatDateTime(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Format currency
function formatCurrency(amount) {
    return `$${parseFloat(amount).toFixed(2)}`;
}

// API request helper
async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(`${API_BASE}${url}`, {
            ...options,
            headers: {
                ...options.headers
            }
        });
        
        const result = await response.json();
        return result;
    } catch (error) {
        console.error('API Request error:', error);
        throw error;
    }
}

// Form data helper
function getFormData(formId) {
    const form = document.getElementById(formId);
    return new FormData(form);
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    updateNavigation();
});
