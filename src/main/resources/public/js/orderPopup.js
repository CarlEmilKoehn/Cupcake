
document.addEventListener('DOMContentLoaded', function () {
    var openBtn = document.getElementById('orderBtn');
    var modal = document.getElementById('orderModal');
    var backdrop = modal ? modal.querySelector('.order-modal__backdrop') : null;
    var closeBtn = modal ? modal.querySelector('.order-modal__close') : null;

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