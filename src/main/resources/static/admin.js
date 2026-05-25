const API = '/api';

const STATUS_LABELS = {
    RECEIVED: 'Modtaget',
    IN_PRODUCTION: 'I produktion',
    READY: 'Klar',
    DELIVERED: 'Leveret'
};

const CATEGORY_LABELS = {
    DIAL: 'Urskive', CASE: 'Urkasse', STRAP: 'Lænke/rem',
    MECHANISM: 'Urværk', HANDS: 'Visere', CROWN: 'Krone', GLASS: 'Glas'
};

function showTab(name) {
    document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.getElementById(`tab-${name}`).classList.add('active');
    event.target.classList.add('active');
}

let allOrders = [];

async function loadOrders() {
    const container = document.getElementById('orders-list');
    try {
        const res = await fetch(`${API}/orders`);
        if (!res.ok) throw new Error('Kunne ikke hente ordrer');
        allOrders = await res.json();
        renderOrders(allOrders);
    } catch (err) {
        container.innerHTML = `<div class="loading">Fejl: ${err.message}</div>`;
    }
}

function filterOrders() {
    const filter = document.getElementById('status-filter').value;
    const filtered = filter ? allOrders.filter(o => o.status === filter) : allOrders;
    renderOrders(filtered);
}

function renderOrders(orders) {
    const container = document.getElementById('orders-list');
    if (orders.length === 0) {
        container.innerHTML = '<div class="loading">Ingen ordrer fundet</div>';
        return;
    }

    container.innerHTML = orders.map(order => {
        const date = new Date(order.createdAt).toLocaleDateString('da-DK', {
            day: 'numeric', month: 'long', year: 'numeric'
        });
        const itemsText = (order.items || [])
            .map(i => `${i.part.name} (×${i.quantity})`)
            .join(' · ');

        return `
        <div class="order-card">
            <div class="order-card-header">
                <div class="order-id">Ordre #${order.id}</div>
                <div class="order-date">${date}</div>
            </div>
            <div class="order-customer">
                <strong>${order.customer.name}</strong> — ${order.customer.email}
                ${order.customer.phone ? `· ${order.customer.phone}` : ''}
            </div>
            <div class="order-items-list">${itemsText || 'Ingen dele registreret'}</div>
            ${order.notes ? `<div class="order-items-list" style="font-style:italic">Note: ${order.notes}</div>` : ''}
            <div class="order-footer">
                <div class="order-price">${order.totalPrice.toLocaleString('da-DK')} kr.</div>
                <div style="display:flex; align-items:center; gap:0.75rem;">
                    <span class="status-badge status-${order.status}">${STATUS_LABELS[order.status]}</span>
                    <select class="status-select" onchange="updateStatus(${order.id}, this.value)">
                        ${Object.keys(STATUS_LABELS).map(s =>
                            `<option value="${s}" ${s === order.status ? 'selected' : ''}>${STATUS_LABELS[s]}</option>`
                        ).join('')}
                    </select>
                </div>
            </div>
        </div>`;
    }).join('');
}

async function updateStatus(orderId, newStatus) {
    try {
        const res = await fetch(`${API}/orders/${orderId}/status?status=${newStatus}`, {
            method: 'PATCH'
        });
        if (!res.ok) throw new Error('Kunne ikke opdatere status');
        await loadOrders();
    } catch (err) {
        alert('Fejl: ' + err.message);
    }
}

let allParts = [];

async function loadPartsAdmin() {
    const container = document.getElementById('parts-admin-list');
    try {
        const res = await fetch(`${API}/parts`);
        if (!res.ok) throw new Error();
        allParts = await res.json();
        renderPartsTable(allParts);
    } catch {
        container.innerHTML = '<div class="loading">Fejl ved indlæsning af dele</div>';
    }
}

function renderPartsTable(parts) {
    const container = document.getElementById('parts-admin-list');
    if (parts.length === 0) {
        container.innerHTML = '<div class="loading">Ingen dele fundet</div>';
        return;
    }

    container.innerHTML = `
        <table class="parts-table">
            <thead>
                <tr>
                    <th>Navn</th>
                    <th>Kategori</th>
                    <th>Pris</th>
                    <th>Lager</th>
                    <th>Handlinger</th>
                </tr>
            </thead>
            <tbody>
                ${parts.map(part => `
                <tr>
                    <td>${part.name}</td>
                    <td>${CATEGORY_LABELS[part.category] || part.category}</td>
                    <td>${part.price.toLocaleString('da-DK')} kr.</td>
                    <td>
                        <input type="number" class="stock-input" value="${part.stock}"
                               id="stock-${part.id}" min="0">
                    </td>
                    <td>
                        <button class="btn-small" onclick="saveStock(${part.id})">Gem</button>
                        <button class="btn-small btn-delete" onclick="deletePart(${part.id})">Slet</button>
                    </td>
                </tr>`).join('')}
            </tbody>
        </table>
    `;
}

async function saveStock(partId) {
    const input = document.getElementById(`stock-${partId}`);
    const newStock = parseInt(input.value);
    try {
        const res = await fetch(`${API}/parts/${partId}/stock?stock=${newStock}`, {
            method: 'PATCH'
        });
        if (!res.ok) throw new Error('Kunne ikke opdatere lager');
        input.style.borderColor = '#4a7c59';
        setTimeout(() => input.style.borderColor = '', 1500);
    } catch (err) {
        alert('Fejl: ' + err.message);
    }
}

async function deletePart(partId) {
    if (!confirm('Er du sikker på at du vil slette denne del?')) return;
    try {
        const res = await fetch(`${API}/parts/${partId}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Kunne ikke slette del');
        await loadPartsAdmin();
    } catch (err) {
        alert('Fejl: ' + err.message);
    }
}

document.getElementById('add-part-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const feedback = document.getElementById('add-part-feedback');

    const partData = {
        name: document.getElementById('part-name').value,
        category: document.getElementById('part-category').value,
        description: document.getElementById('part-description').value,
        price: parseFloat(document.getElementById('part-price').value),
        stock: parseInt(document.getElementById('part-stock').value)
    };

    try {
        const res = await fetch(`${API}/parts`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(partData)
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message);
        }

        feedback.className = 'feedback success';
        feedback.textContent = `✓ "${partData.name}" er tilføjet til lageret`;
        e.target.reset();
        await loadPartsAdmin();
    } catch (err) {
        feedback.className = 'feedback error';
        feedback.textContent = 'Fejl: ' + err.message;
    }
});

loadOrders();
loadPartsAdmin();
