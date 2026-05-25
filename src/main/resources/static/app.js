const API = '/api';

let selectedParts = {};

const CATEGORY_LABELS = {
    DIAL: 'Urskive',
    CASE: 'Urkasse',
    STRAP: 'Lænke / rem',
    MECHANISM: 'Urværk',
    HANDS: 'Visere',
    CROWN: 'Krone',
    GLASS: 'Glas'
};

async function loadParts() {
    const container = document.getElementById('parts-container');
    try {
        const res = await fetch(`${API}/parts/grouped`);
        if (!res.ok) throw new Error('Kunne ikke hente dele');
        const grouped = await res.json();

        container.innerHTML = '';

        Object.entries(grouped).forEach(([category, parts]) => {
            const section = document.createElement('div');
            section.className = 'category-section';
            section.innerHTML = `
                <div class="category-title">${CATEGORY_LABELS[category] || category}</div>
                <div class="parts-grid" id="grid-${category}"></div>
            `;
            container.appendChild(section);

            const grid = document.getElementById(`grid-${category}`);
            parts.forEach(part => {
                const card = createPartCard(part, category);
                grid.appendChild(card);
            });
        });

    } catch (err) {
        container.innerHTML = `<div class="loading">Fejl: ${err.message}</div>`;
    }
}

function createPartCard(part, category) {
    const card = document.createElement('div');
    card.className = 'part-card';
    card.id = `part-card-${part.id}`;

    const stockWarning = part.stock <= 3
        ? `<div class="part-stock-low">Kun ${part.stock} tilbage</div>`
        : '';

    card.innerHTML = `
        <div class="part-name">${part.name}</div>
        <div class="part-description">${part.description || ''}</div>
        <div class="part-price">${part.price.toLocaleString('da-DK')} kr.</div>
        ${stockWarning}
    `;

    card.addEventListener('click', () => selectPart(part, category, card));
    return card;
}

function selectPart(part, category, card) {
    if (selectedParts[category]) {
        const prevId = selectedParts[category].id;
        const prevCard = document.getElementById(`part-card-${prevId}`);
        if (prevCard) prevCard.classList.remove('selected');
    }

    selectedParts[category] = part;
    card.classList.add('selected');
    updateSummary();
}

function updateSummary() {
    const summarySection = document.getElementById('order-summary');
    const partsList = document.getElementById('selected-parts-list');
    const totalEl = document.getElementById('total-price');

    const entries = Object.entries(selectedParts);
    if (entries.length === 0) {
        summarySection.style.display = 'none';
        return;
    }

    summarySection.style.display = 'block';
    partsList.innerHTML = '';

    let total = 0;
    entries.forEach(([category, part]) => {
        total += part.price;
        const row = document.createElement('div');
        row.className = 'selected-part-row';
        row.innerHTML = `
            <span>${CATEGORY_LABELS[category] || category}: ${part.name}</span>
            <span>${part.price.toLocaleString('da-DK')} kr.</span>
        `;
        partsList.appendChild(row);
    });

    totalEl.textContent = total.toLocaleString('da-DK');
}

document.getElementById('order-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const btn = document.getElementById('submit-btn');
    btn.disabled = true;
    btn.textContent = 'Sender…';

    const items = Object.values(selectedParts).map(part => ({
        partId: part.id,
        quantity: 1
    }));

    const orderData = {
        customerName: document.getElementById('customer-name').value,
        customerEmail: document.getElementById('customer-email').value,
        customerPhone: document.getElementById('customer-phone').value,
        notes: document.getElementById('notes').value,
        items
    };

    try {
        const res = await fetch(`${API}/orders`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderData)
        });

        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || 'Bestilling fejlede');
        }

        const order = await res.json();
        showSuccessModal(order);

    } catch (err) {
        alert('Fejl: ' + err.message);
        btn.disabled = false;
        btn.textContent = 'Bestil mit ur';
    }
});

function showSuccessModal(order) {
    const modal = document.getElementById('success-modal');
    const msg = document.getElementById('success-message');
    msg.textContent = `Din ordre #${order.id} er modtaget. Vi kontakter dig snarest på ${order.customer.email}.`;
    modal.style.display = 'flex';
}

function resetConfigurator() {
    selectedParts = {};
    document.getElementById('success-modal').style.display = 'none';
    document.getElementById('order-form').reset();
    document.getElementById('order-summary').style.display = 'none';
    document.querySelectorAll('.part-card.selected').forEach(c => c.classList.remove('selected'));
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

loadParts();
