/**
 * HostelMate — Main Application JavaScript
 * Handles sidebar toggle, dark mode, notifications, and AJAX utilities.
 * 
 * @author HostelMate Team
 */

document.addEventListener('DOMContentLoaded', function() {
    initTheme();
    initSidebar();
    initNotifications();
    initFlashMessages();
    initFormValidation();
});

// ============================================================
// Dark Mode / Theme Toggle
// ============================================================
function initTheme() {
    const toggle = document.getElementById('themeToggle');
    const saved = localStorage.getItem('hostelmate-theme') || 'light';
    document.documentElement.setAttribute('data-theme', saved);
    updateThemeIcon(saved);

    if (toggle) {
        toggle.addEventListener('click', function() {
            const current = document.documentElement.getAttribute('data-theme');
            const next = current === 'dark' ? 'light' : 'dark';
            document.documentElement.setAttribute('data-theme', next);
            localStorage.setItem('hostelmate-theme', next);
            updateThemeIcon(next);
        });
    }
}

function updateThemeIcon(theme) {
    const icon = document.querySelector('#themeToggle i');
    if (icon) {
        icon.className = theme === 'dark' ? 'bi bi-sun' : 'bi bi-moon-stars';
    }
}

// ============================================================
// Sidebar Toggle (Mobile)
// ============================================================
function initSidebar() {
    const toggle = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.sidebar-overlay');

    if (toggle && sidebar) {
        toggle.addEventListener('click', function() {
            sidebar.classList.toggle('show');
            if (overlay) overlay.classList.toggle('show');
        });
    }

    if (overlay) {
        overlay.addEventListener('click', function() {
            sidebar.classList.remove('show');
            overlay.classList.remove('show');
        });
    }
}

// ============================================================
// Notifications
// ============================================================
function initNotifications() {
    const btn = document.getElementById('notificationBtn');
    const dropdown = document.getElementById('notificationDropdown');

    if (btn && dropdown) {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            dropdown.classList.toggle('show');
            if (dropdown.classList.contains('show')) {
                loadNotifications();
            }
        });

        // Close on click outside
        document.addEventListener('click', function(e) {
            if (!dropdown.contains(e.target) && e.target !== btn) {
                dropdown.classList.remove('show');
            }
        });
    }
}

function loadNotifications() {
    const list = document.getElementById('notificationList');
    const badge = document.querySelector('.notification-badge');
    if (!list) return;

    fetch(getContextPath() + '/student/notifications?limit=10')
        .then(r => r.json())
        .then(data => {
            if (badge) {
                if (data.unreadCount > 0) {
                    badge.textContent = data.unreadCount > 9 ? '9+' : data.unreadCount;
                    badge.style.display = 'flex';
                } else {
                    badge.style.display = 'none';
                }
            }

            if (data.notifications.length === 0) {
                list.innerHTML = '<div class="empty-state" style="padding:24px"><i class="bi bi-bell-slash"></i><p>No notifications</p></div>';
                return;
            }

            list.innerHTML = data.notifications.map(n => `
                <div class="notification-item ${n.read ? '' : 'unread'}" onclick="markNotifRead(${n.id}, '${n.link}')">
                    <i class="notif-icon bi ${n.iconClass}"></i>
                    <div>
                        <div class="notif-msg">${escapeHtml(n.message)}</div>
                        <div class="notif-time">${n.timeAgo}</div>
                    </div>
                </div>
            `).join('');
        })
        .catch(err => console.error('Error loading notifications:', err));
}

function markNotifRead(id, link) {
    fetch(getContextPath() + '/student/notifications', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'action=read&id=' + id
    }).then(() => {
        if (link) {
            window.location.href = getContextPath() + link;
        } else {
            loadNotifications();
        }
    });
}

function markAllRead() {
    fetch(getContextPath() + '/student/notifications', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'action=readAll'
    }).then(() => loadNotifications());
}

// ============================================================
// Flash Messages (auto-dismiss)
// ============================================================
function initFlashMessages() {
    const alerts = document.querySelectorAll('.flash-alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });
}

// ============================================================
// Client-side Form Validation
// ============================================================
function initFormValidation() {
    // Add real-time validation to forms with .needs-validation class
    const forms = document.querySelectorAll('.needs-validation');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
}

// ============================================================
// Expense Modal Helpers
// ============================================================
function viewExpense(expenseId) {
    fetch(getContextPath() + '/student/expenses?action=view&id=' + expenseId)
        .then(r => r.json())
        .then(data => {
            if (data.error) {
                showToast('error', data.error);
                return;
            }

            const modal = document.getElementById('viewExpenseModal');
            if (!modal) return;

            document.getElementById('viewTitle').textContent = data.title;
            document.getElementById('viewAmount').textContent = '₹' + formatNumber(data.amount);
            document.getElementById('viewCategory').textContent = data.categoryName;
            document.getElementById('viewDate').textContent = data.expenseDate;
            document.getElementById('viewPaidBy').textContent = data.paidByName;
            document.getElementById('viewDescription').textContent = data.description || 'No description';
            document.getElementById('viewSplitType').textContent = data.splitType;

            const sharesList = document.getElementById('viewShares');
            sharesList.innerHTML = data.shares.map(s => `
                <div class="d-flex justify-content-between align-items-center py-2 border-bottom">
                    <span>${escapeHtml(s.userName)}</span>
                    <div>
                        <span class="me-2">₹${formatNumber(s.shareAmount)}</span>
                        <span class="badge-status ${s.paid ? 'badge-paid' : 'badge-pending'}">
                            ${s.paid ? 'Paid' : 'Pending'}
                        </span>
                    </div>
                </div>
            `).join('');

            new bootstrap.Modal(modal).show();
        })
        .catch(err => {
            console.error('Error loading expense:', err);
            showToast('error', 'Failed to load expense details.');
        });
}

function editExpense(expenseId) {
    fetch(getContextPath() + '/student/expenses?action=view&id=' + expenseId)
        .then(r => r.json())
        .then(data => {
            if (data.error) return;

            const modal = document.getElementById('editExpenseModal');
            if (!modal) return;

            document.getElementById('editExpenseId').value = data.id;
            document.getElementById('editTitle').value = data.title;
            document.getElementById('editDescription').value = data.description || '';
            document.getElementById('editAmount').value = data.amount;
            document.getElementById('editDate').value = data.expenseDate;
            document.getElementById('editCategory').value = data.categoryId;

            // Check shared users
            const checkboxes = modal.querySelectorAll('input[name="sharedWith"]');
            const sharedUserIds = data.shares.map(s => s.userId);
            checkboxes.forEach(cb => {
                cb.checked = sharedUserIds.includes(parseInt(cb.value));
            });

            new bootstrap.Modal(modal).show();
        });
}

function confirmDelete(expenseId) {
    if (confirm('Are you sure you want to delete this expense? This action cannot be undone.')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = getContextPath() + '/student/expenses';
        form.innerHTML = `
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="expenseId" value="${expenseId}">
        `;
        document.body.appendChild(form);
        form.submit();
    }
}

// ============================================================
// Utility Functions
// ============================================================

function getContextPath() {
    // Extract context path from current URL
    const path = window.location.pathname;
    const idx = path.indexOf('/', 1);
    return idx > 0 ? path.substring(0, idx) : '';
}

function formatNumber(num) {
    return parseFloat(num).toLocaleString('en-IN', { 
        minimumFractionDigits: 2, 
        maximumFractionDigits: 2 
    });
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showToast(type, message) {
    const toast = document.createElement('div');
    toast.className = `flash-alert alert-${type}`;
    toast.innerHTML = `<i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i> ${escapeHtml(message)}`;
    
    const container = document.querySelector('.content-area');
    if (container) {
        container.prepend(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }
}

// ============================================================
// Landing Page Navbar Scroll
// ============================================================
window.addEventListener('scroll', function() {
    const nav = document.querySelector('.landing-nav');
    if (nav) {
        if (window.scrollY > 50) {
            nav.classList.add('scrolled');
        } else {
            nav.classList.remove('scrolled');
        }
    }
});
