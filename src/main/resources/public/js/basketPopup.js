// /public/js/basketPopup.js
document.addEventListener('DOMContentLoaded', function () {
    const orderForm = document.getElementById('orderForm');
    const basketBtn = document.getElementById('basketBtn');
    const basketModal = document.getElementById('basketModal');
    const basketContent = basketModal.querySelector('.basket-modal__content');

    function getBasket() {
        try { return JSON.parse(localStorage.getItem('basket') || '[]'); }
        catch { return []; }
    }
    function setBasket(arr) {
        localStorage.setItem('basket', JSON.stringify(arr));
    }
    function renderBasket() {
        const basket = getBasket();
        if (!basket.length) { basketContent.innerHTML = "<p>Basket is empty.</p>"; return; }
        let total = 0;
        basketContent.innerHTML = basket.map(it => {
            const line = it.price * it.quantity;
            total += line;
            return `<div class="basket-item">
        <p>${it.quantity} × ${it.name} — ${it.price} kr</p>
        <p><strong>Linje:</strong> ${line} kr</p>
      </div>`;
        }).join('');
        basketContent.innerHTML += `<hr><p><strong>I alt: ${total} kr</strong></p>`;
    }

    orderForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const quantity = parseInt(document.getElementById('amount').value, 10);
        const bottomSelect = document.getElementById('bottom');
        const toppingSelect = document.getElementById('topping');

        const bottomId = parseInt(bottomSelect.value, 10);
        const toppingId = parseInt(toppingSelect.value, 10);

        const bottomName = bottomSelect.options[bottomSelect.selectedIndex].text.split(' - ')[0];
        const toppingName = toppingSelect.options[toppingSelect.selectedIndex].text.split(' - ')[0];

        // Ask backend to resolve cupcakeId + price (authoritative)
        const r = await fetch(`/api/cupcakes/resolve?topping_id=${toppingId}&bottom_id=${bottomId}`);
        if (!r.ok) { alert('Invalid cupcake combination'); return; }
        const data = await r.json();
        if (!data?.cupcakeId || data.cupcakeId <= 0) { alert('Invalid cupcake combination.'); return; }

        const cupcakeId = data.cupcakeId;
        const unitPrice = data.price; // integer DKK if that’s what you store

        const basket = getBasket();
        basket.push({
            cupcake_id: cupcakeId,
            quantity,
            name: `${toppingName} + ${bottomName}`,
            price: unitPrice
        });
        setBasket(basket);

        renderBasket();
        basketModal.style.display = 'block';
    });

    // modal open/close
    const openBtn = document.getElementById('basketBtn');
    const backdrop = basketModal.querySelector('.basket-modal__backdrop');
    const closeBtn = basketModal.querySelector('.basket-modal__close');
    openBtn.addEventListener('click', () => { basketModal.style.display = 'block'; renderBasket(); });
    backdrop.addEventListener('click', () => basketModal.style.display = 'none');
    closeBtn.addEventListener('click', () => basketModal.style.display = 'none');
});

// /public/js/basketPopup.js
document.addEventListener('DOMContentLoaded', function () {
    const orderForm = document.getElementById('orderForm');
    const basketBtn = document.getElementById('basketBtn');
    const basketModal = document.getElementById('basketModal');
    const basketContent = basketModal.querySelector('.basket-modal__content');
    const openBtn = document.getElementById('basketBtn');
    const backdrop = basketModal.querySelector('.basket-modal__backdrop');
    const closeBtn = basketModal.querySelector('.basket-modal__close');

    // ---- storage helpers
    function getBasket() {
        try { return JSON.parse(localStorage.getItem('basket') || '[]'); }
        catch { return []; }
    }
    function setBasket(arr) {
        localStorage.setItem('basket', JSON.stringify(arr));
        // fire a storage-like event for other tabs/pages if needed
        document.dispatchEvent(new CustomEvent('basket:updated', { detail: arr }));
    }
    function removeAt(index) {
        const b = getBasket();
        if (index >= 0 && index < b.length) {
            b.splice(index, 1);
            setBasket(b);
        }
    }
    function clearBasket() {
        setBasket([]);
    }

    // ---- render UI (+ remove buttons)
    function renderBasket() {
        const basket = getBasket();
        if (!basket.length) {
            basketContent.innerHTML = `
        <p>Basket is empty.</p>
      `;
            return;
        }
        let total = 0;
        const lines = basket.map((item, idx) => {
            const line = (item.price * item.quantity) || 0;
            total += line;
            return `
        <div class="basket-item" data-index="${idx}" style="display:flex;align-items:center;justify-content:space-between;gap:.75rem;padding:.35rem 0;border-bottom:1px solid #eee; font-family: Arial;">
          <div>
            <div><strong>${item.quantity} × ${item.name}</strong></div>
            <div>${item.price} kr/item — linje: ${line} kr</div>
          </div>
          <button class="basket-remove" data-index="${idx}" aria-label="Remove item" title="Remove">
            🗑 Remove
          </button>
        </div>`;
        }).join('');

        basketContent.innerHTML = `
      ${lines}
      <div style="display:flex;align-items:center;justify-content:space-between;margin-top:.75rem; font-family: Arial;">
        <strong>I alt: ${total} kr</strong>
        <div style="display:flex;gap:.5rem; font-family: Arial;">
          <button id="basket-clear" class="basket-clear" title="Clear basket">Clear basket</button>
        </div>
      </div>
    `;
    }

    // ---- event delegation for remove / clear
    basketContent.addEventListener('click', (e) => {
        const removeBtn = e.target.closest('.basket-remove');
        if (removeBtn) {
            const idx = parseInt(removeBtn.dataset.index, 10);
            removeAt(idx);
            renderBasket();
            return;
        }
        if (e.target.id === 'basket-clear') {
            clearBasket();
            renderBasket();
        }
    });



    // ---- modal open/close
    function openModal() { basketModal.style.display = 'block'; renderBasket(); }
    function closeModal() { basketModal.style.display = 'none'; }

    openBtn.addEventListener('click', openModal);
    backdrop.addEventListener('click', closeModal);
    closeBtn.addEventListener('click', closeModal);

    // optional: keep basket fresh if another page changed it
    document.addEventListener('basket:updated', () => {
        if (basketModal.style.display === 'block') renderBasket();
    });
});
