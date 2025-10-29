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

(function() {
    // 1) Levering/afhentning toggle
    const pickupRadio = document.getElementById('pickup');           // radioknap for afhentning
    const deliveryRadio = document.getElementById('delivery');       // radioknap for levering
    const pickupSec = document.getElementById('pickup-time');        // sektion med dato/tid
    const deliverySec = document.getElementById('delivery-address'); // sektion med adressefelter

    // Vis afhentningens felter når "Afhentning" er valgt, ellers vis leveringsfelter
    function toggleDelivery() {
        const isPickup = pickupRadio.checked;
        pickupSec.hidden = !isPickup;
        deliverySec.hidden = isPickup;
    }
    // Opdater visning når brugeren skifter metode
    pickupRadio.addEventListener('change', toggleDelivery);
    deliveryRadio.addEventListener('change', toggleDelivery);
    // Starttilstand
    toggleDelivery();

    // 2) Dato/tid-begrænsninger for afhentning
    const dateEl = document.getElementById('pickupDate'); // dato input
    const timeEl = document.getElementById('pickupTime'); // tid input
    const OPEN_TIME = '09:00';   // åbningstid (eksempel)
    const CLOSE_TIME = '18:00';  // lukketid (eksempel)
    const BUFFER_MIN = 30;       // mindste forberedelsestid i minutter, når afhentning er i dag

    // Små helpers
    function pad(n){ return String(n).padStart(2,'0'); }
    function sameYMD(a,b){
        return a.getFullYear()===b.getFullYear() && a.getMonth()===b.getMonth() && a.getDate()===b.getDate();
    }

    // Når valgt dato er i dag:
    // - min-tid er nu + BUFFER_MIN (men aldrig før OPEN_TIME)
    // - max-tid er CLOSE_TIME
    // - ryd tid, hvis den falder uden for intervallet
    function setTodayBounds() {
        const now = new Date();
        const plus = new Date(now.getTime() + BUFFER_MIN*60000);
        const dynMin = `${pad(plus.getHours())}:${pad(plus.getMinutes())}`;
        timeEl.min = dynMin > OPEN_TIME ? dynMin : OPEN_TIME;
        timeEl.max = CLOSE_TIME;
        if (timeEl.value && (timeEl.value < timeEl.min || timeEl.value > timeEl.max)) timeEl.value = '';
    }

    // Når valgt dato er fremtiden:
    // - min/max er faste åbningstider
    // - ryd tid, hvis den falder uden for intervallet
    function setFutureBounds() {
        timeEl.min = OPEN_TIME;
        timeEl.max = CLOSE_TIME;
        if (timeEl.value && (timeEl.value < timeEl.min || timeEl.value > timeEl.max)) timeEl.value = '';
    }

    // Kaldt når dato ændres:
    // - uden dato: ryd begrænsninger
    // - i dag: brug dynamiske grænser
    // - fremtid: brug faste grænser
    // - fortid: ryd tid og brug faste grænser (så brugeren kan rette datoen)
    function onDateChange() {
        if (!dateEl.value) {
            timeEl.min=''; timeEl.max=''; timeEl.value='';
            return;
        }
        const chosen = new Date(dateEl.value + 'T00:00:00');
        const today = new Date();
        const todayYMD = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        if (sameYMD(chosen, todayYMD)) {
            setTodayBounds();
        } else if (chosen > todayYMD) {
            setFutureBounds();
        } else {
            timeEl.value = '';
            setFutureBounds();
        }
    }

    // Init mindato = i dag og tilknyt change-handler
    const now = new Date();
    dateEl.min = `${now.getFullYear()}-${pad(now.getMonth()+1)}-${pad(now.getDate())}`;
    dateEl.addEventListener('change', onDateChange);
    onDateChange(); // sæt initiale grænser i forhold til evt. forudfyldt dato

    // 3) Betalingsmetode-toggle (cash/coins)
    const paySection = document.getElementById('payment-section');
    const methodRadios = paySection.querySelectorAll('input[name="payMethod"]'); // radioer for betalingsmetode
    const panels = paySection.querySelectorAll('.pay-panel');                    // indholdspaneler pr. metode

    // Vis kun det panel, der matcher valgt metode; skjul de andre
    function showPanel(val) {
        panels.forEach(p => p.hidden = p.getAttribute('data-panel') !== val);
    }
    // Skift panel når radio ændres
    methodRadios.forEach(r => r.addEventListener('change', () => showPanel(r.value)));
    // Initial visning
    showPanel(Array.from(methodRadios).find(r => r.checked)?.value || 'cash');

    // 4) Total-beregning og saldo-visning (placeholders)
    // Disse to værdier skal sættes fra din app (server-render eller fetch)
    let coinsBalanceCents = 0; // tilgængelig Olsker Coins saldo i øre/cent
    let itemsAmountCents = 0;  // varer i kurven i øre/cent

    // Elementer til visning
    const coinsEl = document.getElementById('coinsBalance'); // tekst for coins-saldo
    const sumItemsEl = document.getElementById('sumItems');  // tekst for vare-sum
    const sumShippingEl = document.getElementById('sumShipping'); // tekst for fragt
    const sumTotalEl = document.getElementById('sumTotal');  // tekst for total

    // Format helper: 12345 -> "123,45 kr"
    const fmt = v => (v/100).toFixed(2).replace('.', ',') + ' kr';

    // Genberegn totaler ved skift mellem levering/afhentning
    function recalc() {
        const shipping = deliveryRadio.checked ? 3900 : 0; // eksempel på fragtpris
        const total = itemsAmountCents + shipping;
        sumItemsEl.textContent = fmt(itemsAmountCents);
        sumShippingEl.textContent = fmt(shipping);
        sumTotalEl.textContent = fmt(total);
        coinsEl.textContent = fmt(coinsBalanceCents);
    }
    pickupRadio.addEventListener('change', recalc);
    deliveryRadio.addEventListener('change', recalc);
    recalc(); // initial visning

    // 5) Håndter "Betal ordre"-klik
    const payBtn = document.getElementById('payOrderBtn');
    const msg = document.getElementById('payMessage');

    payBtn.addEventListener('click', () => {
        const method = document.querySelector('input[name="payMethod"]:checked')?.value; // 'cash' eller 'coins'
        const isPickup = pickupRadio.checked;

        // Valider leveringsvalg
        if (isPickup) {
            // Afhentning kræver både dato og tid
            if (!dateEl.value || !timeEl.value) {
                msg.textContent = 'Vælg afhentningsdato og -tid.';
                return;
            }
        } else {
            // Levering kræver adressefelter udfyldt
            const street = document.getElementById('addrStreet').value.trim();
            const zip = document.getElementById('addrZip').value.trim();
            const city = document.getElementById('addrCity').value.trim();
            if (!street || !zip || !city) {
                msg.textContent = 'Udfyld leveringsadresse (vej, postnr., by).';
                return;
            }
        }

        // Beregn total med evt. fragt
        const shipping = deliveryRadio.checked ? 3900 : 0;
        const total = itemsAmountCents + shipping;

        // Branch på betalingsmetode
        if (method === 'coins') {
            // Tjek om saldo er tilstrækkelig
            if (coinsBalanceCents < total) {
                msg.textContent = 'Din saldo er for lav til denne ordre.';
                return;
            }
            msg.textContent = 'Trækker fra Balance...';
            // TODO: Kald backend for coins-betaling (opdatér balance og opret ordre)
            // fetch('/pay/coins', {method:'POST', body:...})
        } else {
            // Kontant betaling: blot opret ordre med status "kontant"
            msg.textContent = 'Ordre markeret til kontant betaling.';
            // TODO: Kald backend for kontant (opret ordre)
            // fetch('/pay/cash', {method:'POST', body:...})
        }
    });
})();