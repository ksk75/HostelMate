/**
 * HostelMate — Charts Module
 * Chart.js configuration for dashboard analytics.
 * 
 * @author HostelMate Team
 */

/**
 * Initialize the category breakdown doughnut chart.
 * 
 * @param {string} canvasId - The canvas element ID
 * @param {Array} labels - Category names
 * @param {Array} data - Category amounts
 */
function initCategoryChart(canvasId, labels, data) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const isDark = document.documentElement.getAttribute('data-theme') === 'dark';

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: [
                    '#6366f1', '#14b8a6', '#f59e0b', '#ef4444',
                    '#3b82f6', '#8b5cf6', '#ec4899'
                ],
                borderWidth: 0,
                hoverBorderWidth: 3,
                hoverBorderColor: '#fff',
                borderRadius: 4,
                spacing: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '65%',
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 16,
                        usePointStyle: true,
                        pointStyleWidth: 10,
                        font: {
                            size: 12,
                            family: "'Inter', sans-serif",
                            weight: '500'
                        },
                        color: isDark ? '#94a3b8' : '#64748b'
                    }
                },
                tooltip: {
                    backgroundColor: isDark ? '#1e293b' : '#ffffff',
                    titleColor: isDark ? '#f1f5f9' : '#1e293b',
                    bodyColor: isDark ? '#94a3b8' : '#64748b',
                    borderColor: isDark ? '#334155' : '#e2e8f0',
                    borderWidth: 1,
                    cornerRadius: 8,
                    padding: 12,
                    displayColors: true,
                    callbacks: {
                        label: function(context) {
                            let value = context.parsed;
                            return ' ₹' + value.toLocaleString('en-IN', { minimumFractionDigits: 2 });
                        }
                    }
                }
            }
        }
    });
}

/**
 * Initialize the monthly trend line chart.
 * 
 * @param {string} canvasId - The canvas element ID
 * @param {Array} labels - Month labels
 * @param {Array} data - Monthly amounts
 */
function initTrendChart(canvasId, labels, data) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const isDark = document.documentElement.getAttribute('data-theme') === 'dark';

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Monthly Expenses',
                data: data,
                borderColor: '#6366f1',
                backgroundColor: 'rgba(99, 102, 241, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#6366f1',
                pointBorderColor: '#ffffff',
                pointBorderWidth: 2,
                pointRadius: 5,
                pointHoverRadius: 7,
                pointHoverBorderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index'
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: isDark ? 'rgba(51,65,85,.3)' : 'rgba(226,232,240,.5)',
                        drawBorder: false
                    },
                    ticks: {
                        font: { size: 11, family: "'Inter', sans-serif" },
                        color: isDark ? '#64748b' : '#94a3b8',
                        callback: function(value) {
                            return '₹' + value.toLocaleString('en-IN');
                        },
                        padding: 8
                    },
                    border: { display: false }
                },
                x: {
                    grid: { display: false },
                    ticks: {
                        font: { size: 11, family: "'Inter', sans-serif" },
                        color: isDark ? '#64748b' : '#94a3b8',
                        padding: 8
                    },
                    border: { display: false }
                }
            },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: isDark ? '#1e293b' : '#ffffff',
                    titleColor: isDark ? '#f1f5f9' : '#1e293b',
                    bodyColor: isDark ? '#94a3b8' : '#64748b',
                    borderColor: isDark ? '#334155' : '#e2e8f0',
                    borderWidth: 1,
                    cornerRadius: 8,
                    padding: 12,
                    callbacks: {
                        label: function(context) {
                            return '₹' + context.parsed.y.toLocaleString('en-IN', { minimumFractionDigits: 2 });
                        }
                    }
                }
            }
        }
    });
}

/**
 * Initialize a bar chart for admin reports.
 * 
 * @param {string} canvasId - The canvas element ID  
 * @param {Array} labels - Labels
 * @param {Array} data - Data values
 * @param {string} label - Dataset label
 */
function initBarChart(canvasId, labels, data, label) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const isDark = document.documentElement.getAttribute('data-theme') === 'dark';

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: label || 'Amount',
                data: data,
                backgroundColor: [
                    'rgba(99, 102, 241, 0.8)',
                    'rgba(20, 184, 166, 0.8)',
                    'rgba(245, 158, 11, 0.8)',
                    'rgba(239, 68, 68, 0.8)',
                    'rgba(59, 130, 246, 0.8)',
                    'rgba(139, 92, 246, 0.8)',
                    'rgba(236, 72, 153, 0.8)'
                ],
                borderRadius: 6,
                borderSkipped: false,
                maxBarThickness: 50
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: isDark ? 'rgba(51,65,85,.3)' : 'rgba(226,232,240,.5)',
                        drawBorder: false
                    },
                    ticks: {
                        font: { size: 11, family: "'Inter', sans-serif" },
                        color: isDark ? '#64748b' : '#94a3b8',
                        callback: function(value) {
                            return '₹' + value.toLocaleString('en-IN');
                        }
                    },
                    border: { display: false }
                },
                x: {
                    grid: { display: false },
                    ticks: {
                        font: { size: 11, family: "'Inter', sans-serif" },
                        color: isDark ? '#64748b' : '#94a3b8'
                    },
                    border: { display: false }
                }
            },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: isDark ? '#1e293b' : '#ffffff',
                    titleColor: isDark ? '#f1f5f9' : '#1e293b',
                    bodyColor: isDark ? '#94a3b8' : '#64748b',
                    borderColor: isDark ? '#334155' : '#e2e8f0',
                    borderWidth: 1,
                    cornerRadius: 8,
                    padding: 12,
                    callbacks: {
                        label: function(context) {
                            return '₹' + context.parsed.y.toLocaleString('en-IN', { minimumFractionDigits: 2 });
                        }
                    }
                }
            }
        }
    });
}
