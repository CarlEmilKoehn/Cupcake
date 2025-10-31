(function () {
    if (window.__basketPopupInit) return;
    window.__basketPopupInit = true;

    document.addEventListener('DOMContentLoaded', function () {
        const orderForm = document.getElementById('orderForm');
        const basketModal = document.getElementById('basketModal');
        const basketContent = basketModal.querySelector('.basket-modal__content');
        const openBtn = document.getElementById('basketBtn');
        const backdrop = basketModal.querySelector('.basket-modal__backdrop');
        const closeBtn = basketModal.querySelector('.basket-modal__close');

        function getBasket() {
            try { return JSON.parse(localStorage.getItem('basket') || '[]'); }
            catch { return []; }
        }
        function setBasket(arr) {
            localStorage.setItem('basket', JSON.stringify(arr));
            document.dispatchEvent(new CustomEvent('basket:updated', { detail: arr }));
        }

        function removeAt(index) {
            const b = getBasket();
            if (index >= 0 && index < b.length) {
                b.splice(index, 1);
                setBasket(b);
            }
        }
        function clearBasket() { setBasket([]); }

        function renderBasket() {
            const basket = getBasket();
            if (!basket.length) {
                basketContent.innerHTML = `<p>Basket is empty.</p>`;
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
            <button class="basket-remove" data-index="${idx}" aria-label="Remove item" title="Remove">🗑 Remove</button>
          </div>`;
            }).join('');

            basketContent.innerHTML = `
        ${lines}
        <div style="display:flex;align-items:center;justify-content:space-between;margin-top:.75rem; font-family: Arial;">
          <strong>I alt: ${total} kr</strong>
          <div style="display:flex;gap:.5rem;">
            <button id="basket-clear" class="basket-clear" title="Clear basket">Clear basket</button>
          </div>
        </div>`;
        }

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

        orderForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const quantity = parseInt(document.getElementById('amount').value, 10);
            const bottomSelect = document.getElementById('bottom');
            const toppingSelect = document.getElementById('topping');

            const bottomId = parseInt(bottomSelect.value, 10);
            const toppingId = parseInt(toppingSelect.value, 10);

            const bottomName = bottomSelect.options[bottomSelect.selectedIndex].text.split(' - ')[0];
            const toppingName = toppingSelect.options[toppingSelect.selectedIndex].text.split(' - ')[0];
            const res = await fetch(`/api/cupcakes/resolve?topping_id=${toppingId}&bottom_id=${bottomId}`);

            if (!res.ok) { alert('Could not find the cupcake combination'); return; }
            const data = await res.json();
            if (!data?.cupcakeId || data.cupcakeId <= 0) { alert('The cupcake-combination does not exist'); return; }

            const cupcakeId = data.cupcakeId;
            const unitPrice = data.price;

            const b = getBasket();
            const name = `${toppingName} + ${bottomName}`;

            const ix = b.findIndex(x => x.cupcake_id === cupcakeId && x.name === name && x.price === unitPrice);
            if (ix >= 0) {
                b[ix].quantity += quantity;
            } else {
                b.push({ cupcake_id: cupcakeId, quantity, name, price: unitPrice });
            }
            setBasket(b);

            renderBasket();
            basketModal.style.display = 'block';
        });

        function openModal() { basketModal.style.display = 'block'; renderBasket(); }
        function closeModal() { basketModal.style.display = 'none'; }

        openBtn.addEventListener('click', openModal);
        backdrop.addEventListener('click', closeModal);
        closeBtn.addEventListener('click', closeModal);

        document.addEventListener('basket:updated', () => {
            if (basketModal.style.display === 'block') renderBasket();
        });
    });
})();
