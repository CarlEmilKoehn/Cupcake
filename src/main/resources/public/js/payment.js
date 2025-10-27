
    function ok(msg){ alert(msg); window.location.href = '/homepage'; }
    function err(msg){ alert(msg); }

    document.getElementById('mpPayBtn')?.addEventListener('click', () => {
    const phone = (document.getElementById('mpPhone').value || '').replace(/\D/g,'');
});

    document.getElementById('cardPayBtn')?.addEventListener('click', () => {
    const n = (document.getElementById('ccNumber').value || '').replace(/\s+/g,'');
    const e = (document.getElementById('ccExpiry').value || '').trim();
    const c = (document.getElementById('ccCvv').value || '').trim();
});
