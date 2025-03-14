document.addEventListener('DOMContentLoaded', () => {
    const cartCount = document.querySelector('.cart-count');

    // Вземаме стойността на cartCount от localStorage (по подразбиране 0, ако няма)
    const totalItems = localStorage.getItem('cartCount') || 0;

    // Обновяваме брояча в UI
    if (cartCount) {
        cartCount.textContent = totalItems;
    }

    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const cartItemsContainer = document.getElementById('cartItems');
    const grandTotalElement = document.getElementById('grandTotal');
    let grandTotal = 0;

    // Показване на продуктите в таблицата с input за количество и бутон "Remove"
    cart.forEach((product, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><img src="${product.img}" alt="${product.name}" style="width: 50px;"></td>
            <td>${product.name}</td>
            <td>${product.size}</td>
            <td>$${product.price}</td>
            <td>
                <input type="number" class="quantity" data-index="${index}" value="${product.quantity}" min="1" style="width: 50px;">
            </td>
            <td class="total-price">$${(product.price * product.quantity).toFixed(2)}</td>
            <td>
                <button class="remove-btn" data-index="${index}" style="background-color: red; color: white; border: none; padding: 5px 10px; cursor: pointer;">Remove</button>
            </td>
        `;
        cartItemsContainer.appendChild(row);
        grandTotal += product.price * product.quantity;
    });

    // Обновяване на общата сума
    grandTotalElement.textContent = `Total: $${grandTotal.toFixed(2)}`;
});

// Функция за премахване на продукт от количката
document.addEventListener('click', (event) => {
    if (event.target.classList.contains('remove-btn')) {
        const index = event.target.getAttribute('data-index');
        removeItem(index);
    }
});

document.getElementById('clearCart').addEventListener('click', () => {
    localStorage.removeItem('cart'); // Изчистваме количката
    localStorage.setItem('cartCount', 0); // Нулираме брояча

    // Обновяваме брояча в UI
    const cartCountElement = document.querySelector('.cart-count');
    if (cartCountElement) {
        cartCountElement.textContent = "0";
    }

    window.location.reload(); // Презареждаме страницата, за да се обнови количката
});

function removeItem(index) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];

    // Премахваме избрания артикул
    cart.splice(index, 1);

    // Обновяваме LocalStorage
    localStorage.setItem('cart', JSON.stringify(cart));
    localStorage.setItem('cartCount', cart.length);

    // Обновяваме страницата
    window.location.reload();
}

// Обновяване на цената при промяна на количество
document.addEventListener('input', (event) => {
    if (event.target.classList.contains('quantity')) {
        const index = event.target.getAttribute('data-index');
        const newQuantity = parseInt(event.target.value, 10) || 1;

        let cart = JSON.parse(localStorage.getItem('cart')) || [];
        cart[index].quantity = newQuantity;
        localStorage.setItem('cart', JSON.stringify(cart));

        const row = event.target.closest('tr');
        const priceElement = row.querySelector('.total-price');
        priceElement.textContent = `$${(cart[index].price * newQuantity).toFixed(2)}`;

        updateGrandTotal();
    }
});

// Функция за обновяване на общата сума
function updateGrandTotal() {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    let grandTotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    document.getElementById('grandTotal').textContent = `Total: $${grandTotal.toFixed(2)}`;
}
//поръчки
document.getElementById('deliveryForm').addEventListener('submit', async (event) => {
    event.preventDefault(); // Спираме стандартното изпращане на формата

    // Вземаме CSRF токен от мета таговете (не трябва да се маха)
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Вземаме данните от формата за доставка
    const formData = new FormData(event.target);
    const deliveryDetails = Object.fromEntries(formData);

    // Вземаме текущото съдържание на количката от localStorage
    const cart = JSON.parse(localStorage.getItem('cart')) || [];

    if (cart.length === 0) {
        alert("Your cart is empty!");
        return;
    }

    // Вземаме актуалното количество от input полетата
    const updatedCart = cart.map((item, index) => {
        const quantityInput = document.querySelector(`.quantity[data-index="${index}"]`);
        return {
            id: item.id,
            quantity: parseInt(quantityInput.value, 10) || 1,
            size: item.size
        };
    });

    // Вземаме метода на плащане
    const paymentMethod = document.getElementById('paymentMethod').value;

    // Създаваме JSON заявка за поръчката
    const orderData = {
        country: deliveryDetails.country,
        address: deliveryDetails.address,
        city: deliveryDetails.city,
        phone: deliveryDetails.phone,
        paymentMethod: paymentMethod,
        cartItems: updatedCart
    };

    try {
        console.log("🚀 Submitting order:", JSON.stringify(orderData)); // ✅ Debugging log

        console.log("🚀 About to send fetch request to /checkout");

        const response = await fetch('/checkout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken // ✅ Изпращаме CSRF токена
            },
            body: JSON.stringify(orderData)
        });

        console.log("📡 Response status:", response.status); // ✅ Проверяваме статуса на отговора

        // Проверяваме дали сървърът връща redirect (HTML, а не JSON)
        if (response.redirected) {
            console.log("🔄 Redirecting to:", response.url);
            window.location.href = response.url;
            return;
        }

        // Проверяваме дали отговорът е JSON, за да избегнем грешки
        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            console.error("❌ Expected JSON but received:", contentType);
            alert("Unexpected response from the server.");
            return;
        }

        const responseData = await response.json();
        console.log("✅ Response data:", responseData);

        if (responseData.success) {
            alert(responseData.success); // Показваме съобщение за успешна поръчка

            // Изчистваме количката и брояча в localStorage
            localStorage.removeItem('cart');
            localStorage.setItem('cartCount', 0);

            // Обновяване на UI брояча
            const cartCountElement = document.querySelector('.cart-count');
            if (cartCountElement) {
                cartCountElement.textContent = "0";
            }

            // Пренасочваме потребителя към началната страница
            window.location.href = responseData.redirect || '/home';
        } else {
            alert("❌ Error: " + (responseData.error || "Something went wrong!"));
        }

    } catch (error) {
        console.error("🔥 Fetch request failed:", error);
        alert("There was a problem submitting your order. Please try again.");
    }

});
