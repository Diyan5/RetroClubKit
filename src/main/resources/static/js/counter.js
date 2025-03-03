document.addEventListener('DOMContentLoaded', () => {
    const cartCount = document.querySelector('.cart-count');

    // Вземаме стойността на cartCount от localStorage (по подразбиране 0, ако няма)
    const totalItems = localStorage.getItem('cartCount') || 0;

    // Обновяваме брояча в UI
    if (cartCount) {
        cartCount.textContent = totalItems;
    }
});