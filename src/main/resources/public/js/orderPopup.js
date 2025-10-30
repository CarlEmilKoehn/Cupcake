document.addEventListener('DOMContentLoaded', function () {
    const openBtn = document.getElementById('orderBtn');
    const modal = document.getElementById('orderModal');
    const backdrop = modal ? modal.querySelector('.order-modal__backdrop') : null;
    const closeBtn = modal ? modal.querySelector('.order-modal__close') : null;
    const contentDiv = modal ? modal.querySelector('.order-modal__content') : null;

    if (!openBtn || !modal) return;

    function openModal() {
        modal.style.display = 'block';
        loadOrders(); // ✅ Load previous orders when modal opens
    }

    function closeModal() {
        modal.style.display = 'none';
    }

    if (backdrop) backdrop.addEventListener('click', closeModal);
    if (closeBtn) closeBtn.addEventListener('click', closeModal);
    openBtn.addEventListener('click', openModal);

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') closeModal();
    });

    async function loadOrders() {
        const contentDiv = document.querySelector('#orderModal .order-modal__content');
        contentDiv.innerHTML = '<p>Loading...</p>';

        const res = await fetch('/api/orders');
        if (!res.ok) {
            contentDiv.innerHTML = '<p>Could not load orders.</p>';
            return;
        }

        const orders = await res.json();
        if (!orders || orders.length === 0) {
            contentDiv.innerHTML = '<p>No orders.</p>';
            return;
        }

        contentDiv.innerHTML = orders.map(o => `
        <div class="order-entry">
            <strong>Order #${o.orderId}</strong><br>
            Date: ${new Date(o.purchaceDate).toLocaleString()}<br>
            Cupcake ID: ${o.cupcakeId} — Quantity: ${o.cupcakeAmount} — Price: ${o.cupcakePrice} DKK
        </div>
    `).join('');
    }

});
