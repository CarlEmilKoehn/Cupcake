document.addEventListener('DOMContentLoaded', function () {
    const orderForm = document.getElementById('orderForm');
    const basketBtn = document.getElementById('basketBtn');
    const basketModal = document.getElementById('basketModal');
    const basketContent = basketModal.querySelector('.basket-modal__content');

    const basket = [];

    orderForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const amount = parseInt(document.getElementById('amount').value);
        const bottomSelect = document.getElementById('bottom');
        const toppingSelect = document.getElementById('topping');

        const bottomOption = bottomSelect.options[bottomSelect.selectedIndex];
        const toppingOption = toppingSelect.options[toppingSelect.selectedIndex];

        const bottomName = bottomOption.text.split(" - ")[0];
        const toppingName = toppingOption.text.split(" - ")[0];
        const bottomPrice = parseFloat(bottomOption.dataset.price);
        const toppingPrice = parseFloat(toppingOption.dataset.price);

        const cupcakePrice = bottomPrice + toppingPrice;
        const totalPrice = cupcakePrice * amount;

        const item = {
            amount,
            name: `${toppingName} + ${bottomName}`,
            price: cupcakePrice,
            totalPrice
        };

        basket.push(item);
        updateBasketPopup();
    });


    function updateBasketPopup() {
        if (basket.length === 0) {
            basketContent.innerHTML = "<p>Basket is empty.</p>";
            return;
        }

        let total = 0;
        basketContent.innerHTML = basket.map(item => {
            total += item.totalPrice;
            return `
            <div class="basket-item">
                <p>${item.amount} × ${item.name} — ${item.price.toFixed(2)} dkk each</p>
                <p><strong>Total:</strong> ${item.totalPrice.toFixed(2)} dkk</p>
            </div>
        `;
        }).join('');

        basketContent.innerHTML += `<hr><p><strong>Grand Total: ${total.toFixed(2)} dkk</strong></p>`;
    }


    const openBtn = document.getElementById('basketBtn');
    const backdrop = basketModal.querySelector('.basket-modal__backdrop');
    const closeBtn = basketModal.querySelector('.basket-modal__close');

    function openModal() {
        basketModal.style.display = 'block';
    }

    function closeModal() {
        basketModal.style.display = 'none';
    }

    openBtn.addEventListener('click', openModal);
    backdrop.addEventListener('click', closeModal);
    closeBtn.addEventListener('click', closeModal);
});
