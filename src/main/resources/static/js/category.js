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

// Примерни данни за продуктите
const products = [
    { id: 1, name: 'Chelsea retro kit 2005', price: 140, size: 'M', img: '/images/ch.png' },
    { id: 2, name: 'Aston Villa retro kit 1990', price: 130, size: 'L', img: '/images/ast.png' },
    { id: 3, name: 'Real Madrid 1998', price: 100, size: 'S', img: '/images/rea.png' },
    { id: 4, name: 'Real Madrid 1998', price: 100, size: 'S', img: '/images/rea.png' },
    { id: 5, name: 'Real Madrid 1998', price: 100, size: 'S', img: '/images/rea.png' },
    { id: 6, name: 'Real Madrid 1998', price: 100, size: 'S', img: '/images/rea.png' },
];

// Взимане на количката от LocalStorage (или създаване на празна, ако не съществува)
let cart = JSON.parse(localStorage.getItem('cart')) || [];

// Функция за обновяване на брояча на количката
const updateCartCount = () => {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const cartCount = document.querySelector('.cart-count');
    if (cartCount) {
        const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
        cartCount.textContent = totalItems; // Обновяване на брояча
    }
};

// Обновяване на брояча при зареждане на страницата
document.addEventListener('DOMContentLoaded', updateCartCount);

// Слушане за промени в LocalStorage (например изчистване на количката от "Checkout")
window.addEventListener('storage', (event) => {
    if (event.key === 'cartUpdated') {
        updateCartCount(); // Обновяване на брояча при промяна
    }
});


// Функция за обновяване на LocalStorage
const updateLocalStorage = () => {
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartCount(); // Синхронизация на брояча след обновяване на LocalStorage
};

// Добавяне на обработчик за всички бутони "Добави в количка"
document.addEventListener('DOMContentLoaded', () => {
    const addToCartButtons = document.querySelectorAll('.btn'); // Намиране на бутоните
    addToCartButtons.forEach((button, index) => {
        button.addEventListener('click', () => {
            // Добавяне на продукта в количката
            const product = products[index];
            const existingProduct = cart.find(item => item.id === product.id);

            if (existingProduct) {
                existingProduct.quantity += 1; // Увеличаване на количеството
            } else {
                cart.push({ ...product, quantity: 1 }); // Добавяне на нов продукт
            }

            // Обновяване на LocalStorage и брояча
            updateLocalStorage();
        });
    });

    // Обновяване на брояча при зареждане на страницата
    updateCartCount();
});