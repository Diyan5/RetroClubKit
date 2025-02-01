function toggleFilters() {
    var filtersOptions = document.getElementById("filtersOptions");
    if (filtersOptions.style.display === "block") {
        filtersOptions.style.display = "none";
    } else {
        filtersOptions.style.display = "block";
    }
}
// Функция за сортиране на продуктите
function sortProducts(criteria) {
    const productList = document.querySelector('.product-list');
    const products = Array.from(productList.querySelectorAll('.product-card'));

    products.sort((a, b) => {
        const priceA = parseFloat(a.querySelector('p').textContent.replace('Price: ', '').replace('$', ''));
        const priceB = parseFloat(b.querySelector('p').textContent.replace('Price: ', '').replace('$', ''));
        const nameA = a.querySelector('h3').textContent.toLowerCase();
        const nameB = b.querySelector('h3').textContent.toLowerCase();

        switch (criteria) {
            case 'price-asc':
                return priceA - priceB;
            case 'price-desc':
                return priceB - priceA;
            case 'name-asc':
                return nameA.localeCompare(nameB);
            case 'name-desc':
                return nameB.localeCompare(nameA);
            default:
                return 0;
        }
    });

    // Премахване на текущите продукти от контейнера
    productList.innerHTML = '';

    // Добавяне на сортираните продукти обратно в контейнера
    products.forEach(product => productList.appendChild(product));
}


function addToCart(event) {

    const button = event.target;
    const productCard = button.closest('.product-card'); // Получаваме бутона
    const productId = button.getAttribute('data-id');
    const productName = button.getAttribute('data-name');
    const productPrice = parseFloat(button.getAttribute('data-price'));
    const productImage = productCard.querySelector('img').getAttribute('src');

    // Извличаме избрания размер от селектора
    const sizeSelect = button.closest('.product-card').querySelector('select[name="size"]');
    const selectedSize = sizeSelect ? sizeSelect.value : 'M'; // Ако няма избран размер, вземаме M по подразбиране

    let cart = JSON.parse(localStorage.getItem('cart')) || [];

    // Проверяваме дали този продукт със съответния размер вече съществува в количката
    const existingProduct = cart.find(item => item.id === productId && item.size === selectedSize);

    if (existingProduct) {
        // Ако продуктът със същия размер вече съществува, увеличаваме количеството само с 1
        existingProduct.quantity += 1;
    } else {
        // Ако продуктът не съществува в количката, добавяме нов
        cart.push({
            id: productId,
            name: productName,
            price: productPrice,
            size: selectedSize,
            quantity: 1,
            img: productImage // Задайте пътя към изображението тук
        });
    }

    // Записваме количката в localStorage
    localStorage.setItem('cart', JSON.stringify(cart));

    // Обновяване на броя на продуктите в количката
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    localStorage.setItem('cartCount', totalItems);

    // Обновяване на брояча в UI
    const cartCount = document.querySelector('.cart-count');
    if (cartCount) {
        cartCount.textContent = totalItems;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const cartCount = document.querySelector('.cart-count');

    // Вземаме стойността на cartCount от localStorage (по подразбиране 0, ако няма)
    const totalItems = localStorage.getItem('cartCount') || 0;

    // Обновяваме брояча в UI
    if (cartCount) {
        cartCount.textContent = totalItems;
    }
});