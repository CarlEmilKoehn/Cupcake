
document.addEventListener('DOMContentLoaded', function () {
    var openBtn = document.getElementById('balanceBtn');
    var modal = document.getElementById('balanceModal');
    var backdrop = modal ? modal.querySelector('.balance-modal__backdrop') : null;
    var closeBtn = modal ? modal.querySelector('.balance-modal__close') : null;

    if (!openBtn || !modal) return;

    function openModal() {
        modal.style.display = 'block';
    }
    function closeModal() {
        modal.style.display = 'none';
    }

    openBtn.addEventListener('click', openModal);
    if (backdrop) backdrop.addEventListener('click', closeModal);
    if (closeBtn) closeBtn.addEventListener('click', closeModal);

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') closeModal();
    });
});