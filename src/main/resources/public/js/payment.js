// JavaScript (forklaret)

// Små hjælpefunktioner til brugerfeedback og redirect
function ok(msg){
    alert(msg);                   // Vis en success-besked
    window.location.href = '/homepage'; // Gå tilbage til forsiden
}
function err(msg){
    alert(msg);                   // Vis en fejlbesked
}

// Event handlers for (tidligere) MobilePay og Kort knapper
// De er defensive (?.), så koden ikke fejler, hvis knapperne ikke findes i DOM.

// MobilePay: læs og rens telefonnummer (kun cifre)
document.getElementById('mpPayBtn')?.addEventListener('click', () => {
    const phone = (document.getElementById('mpPhone').value || '').replace(/\D/g,'');
    // Her kunne du validere phone.length === 8 og sende til backend
});

// Kort: læs og rens kortdata
document.getElementById('cardPayBtn')?.addEventListener('click', () => {
    const n = (document.getElementById('ccNumber').value || '').replace(/\s+/g,''); // kortnummer uden mellemrum
    const e = (document.getElementById('ccExpiry').value || '').trim();             // udløb MM/YY
    const c = (document.getElementById('ccCvv').value || '').trim();                // CVV
    // Her kunne du lave Luhn-validering, format-tjek osv.
});

// /public/js/payment.js
// /public/js/payment.js
(function () {
    // (keep your delivery/pickup + time-window code as-is)

    const pickupRadio = document.getElementById('pickup');
    const deliveryRadio = document.getElementById('delivery');

    const sumItemsEl = document.getElementById('sumItems');
    const sumShippingEl = document.getElementById('sumShipping');
    const sumTotalEl = document.getElementById('sumTotal');

    function basket() {
        try { return JSON.parse(localStorage.getItem('basket') || '[]'); }
        catch { return []; }
    }
    function itemsTotal() {
        return basket().reduce((acc, it) => acc + (parseInt(it.price,10)||0) * (parseInt(it.quantity,10)||0), 0);
    }
    function recalc() {
        const items = itemsTotal();
        const shipping = deliveryRadio.checked ? 39 : 0; // adapt if you store øre/cents
        const total = items + shipping;
        sumItemsEl.textContent = `${items} kr`;
        sumShippingEl.textContent = `${shipping} kr`;
        sumTotalEl.textContent = `${total} kr`;
        return { items, shipping, total };
    }
    pickupRadio?.addEventListener('change', recalc);
    deliveryRadio?.addEventListener('change', recalc);
    recalc();

    document.getElementById('payOrderBtn').addEventListener('click', async () => {
        const items = basket();
        if (!items.length) {
            document.getElementById('payMessage').textContent = 'Your basket is empty.';
            return;
        }

        // Build backend payload (JSON)
        const payload = {
            payMethod: document.querySelector('input[name="payMethod"]:checked')?.value || 'coins',
            shippingCents: deliveryRadio.checked ? 3900 : 0, // if backend expects øre/cents
            items: items.map(i => ({ cupcakeId: i.cupcake_id, quantity: i.quantity }))
        };

        // minimal validation before POST
        if (payload.items.some(x => !Number.isInteger(x.cupcakeId) || x.cupcakeId <= 0)) {
            document.getElementById('payMessage').textContent = 'Ugyldigt produkt i kurven (cupcakeId).';
            return;
        }

        const res = await fetch('/payment/submit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const text = await res.text();
        document.getElementById('payMessage').textContent = text;

        if (res.ok) {
            localStorage.removeItem('basket');
            window.location.href = '/homepage';
        }
    });
})();

