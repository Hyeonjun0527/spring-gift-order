document.addEventListener('DOMContentLoaded', () => {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken || accessToken === 'undefined') {
        localStorage.removeItem('accessToken');
        window.location.href = '/login';
        return;
    }

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${accessToken}`
    };

    const productList = document.getElementById('product-list');
    const productPagination = document.getElementById('product-pagination');
    const wishList = document.getElementById('wish-list');
    const wishPagination = document.getElementById('wish-pagination');
    const logoutButton = document.getElementById('logout-button');
    const addProductForm = document.getElementById('add-product-form');
    const adminLink = document.getElementById('admin-link');

    let currentProductPage = 0;
    let currentWishPage = 0;
    let userRole = null;

    const validateToken = async () => {
        try {
            const response = await fetch('/api/products?page=0&size=1', { headers });
            if (response.status === 401) {
                localStorage.removeItem('accessToken');
                window.location.href = '/login';
                return false;
            }
            return true;
        } catch (error) {
            console.error('Token validation error:', error);
            localStorage.removeItem('accessToken');
            window.location.href = '/login';
            return false;
        }
    };

    const checkUserRole = async () => {
        try {
            const response = await fetch('/api/admin/members', { headers });
            if (response.ok) {
                userRole = 'ADMIN';
                adminLink.style.display = 'inline-block';
            } else {
                userRole = 'USER';
                adminLink.style.display = 'none';
            }
        } catch (error) {
            userRole = 'USER';
            adminLink.style.display = 'none';
        }
    };

    const handleApiError = (response) => {
        if (response.status === 401) {
            localStorage.removeItem('accessToken');
            window.location.href = '/login';
            return true;
        }
        return false;
    };

    const fetchProducts = async (page = 0) => {
        try {
            const validPage = isNaN(page) || page < 0 ? 0 : page;
            const response = await fetch(`/api/products?page=${validPage}&size=5`, { headers });
            if (handleApiError(response)) return;
            
            const data = await response.json();
            renderProducts(data.content);
            renderPagination(productPagination, data, fetchProducts);
            currentProductPage = validPage;
        } catch (error) {
            console.error('Error fetching products:', error);
        }
    };

    const fetchWishes = async (page = 0) => {
        try {
            const validPage = isNaN(page) || page < 0 ? 0 : page;
            const response = await fetch(`/api/wishes?page=${validPage}&size=5`, { headers });
            if (handleApiError(response)) return;
            
            const data = await response.json();
            renderWishes(data.content);
            renderPagination(wishPagination, data, fetchWishes);
            currentWishPage = validPage;
        } catch (error) {
            console.error('Error fetching wishes:', error);
        }
    };

    const renderProducts = (products) => {
        productList.innerHTML = '';
        products.forEach(product => {
            const item = document.createElement('div');
            item.className = 'product-item';
            item.innerHTML = `
                <div>
                    <strong>${product.name}</strong> - ${product.price}원
                </div>
                <button onclick="addWish(${product.id})">위시리스트에 추가</button>
            `;
            productList.appendChild(item);
        });
    };

    const renderWishes = (wishes) => {
        wishList.innerHTML = '';
        wishes.forEach(wish => {
            const item = document.createElement('div');
            item.className = 'wish-item';
            item.innerHTML = `
                <div>
                    <strong>${wish.productName}</strong> - ${wish.productPrice}원
                </div>
                <div>
                    <input type="number" value="${wish.quantity}" min="1" onchange="updateWishQuantity(${wish.wishId}, this.value)">
                    <button onclick="deleteWish(${wish.wishId})">삭제</button>
                </div>
            `;
            wishList.appendChild(item);
        });
    };

    /*
    const renderPagination = (container, pageData, fetchFunction) => {
        container.innerHTML = '';
        if (pageData.totalPages > 1) {
            if (!pageData.first) {
                const prevButton = document.createElement('button');
                prevButton.innerText = '이전';
                const prevPage = (pageData.number || 0) - 1;
                prevButton.onclick = () => fetchFunction(Math.max(0, prevPage));
                container.appendChild(prevButton);
            }
            if (!pageData.last) {
                const nextButton = document.createElement('button');
                nextButton.innerText = '다음';
                const nextPage = (pageData.number || 0) + 1;
                nextButton.onclick = () => fetchFunction(Math.min(pageData.totalPages - 1, nextPage));
                container.appendChild(nextButton);
            }
        }
    };
    */

    addProductForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const imageUrl = document.getElementById('product-imageUrl').value.trim();
        const productData = {
            name: document.getElementById('product-name').value,
            price: parseInt(document.getElementById('product-price').value),
            imageUrl: imageUrl || null
        };

        try {
            const response = await fetch('/api/products', {
                method: 'POST',
                headers,
                body: JSON.stringify(productData)
            });

            if (response.ok) {
                alert('상품이 추가되었습니다.');
                addProductForm.reset();
                fetchProducts(0);
            } else {
                const errorData = await response.json();
                if (errorData.errors && Array.isArray(errorData.errors)) {
                    const messages = errorData.errors.map(e => e.message).join('\n');
                    alert(messages);
                } else if (errorData.detail) {
                    alert(errorData.detail);
                } else {
                    alert('상품 추가에 실패했습니다.');
                }
            }
        } catch (error) {
            console.error('Error adding product:', error);
            alert('오류가 발생했습니다.');
        }
    });

    window.addWish = async (productId) => {
        try {
            const response = await fetch('/api/wishes', {
                method: 'POST',
                headers,
                body: JSON.stringify({ productId: productId, quantity: 1 })
            });
            if (response.ok) {
                alert('위시리스트에 추가되었습니다.');
                fetchWishes(currentWishPage);
            } else {
                alert('추가 실패');
            }
        } catch (error) {
            console.error('Error adding wish:', error);
        }
    };

    window.updateWishQuantity = async (wishId, quantity) => {
        try {
            const response = await fetch(`/api/wishes/${wishId}/quantity`, {
                method: 'PUT',
                headers,
                body: JSON.stringify({ quantity: parseInt(quantity) })
            });
            if (response.ok) {
                alert('수량이 변경되었습니다.');
                fetchWishes(currentWishPage);
            } else {
                alert('수량 변경 실패');
            }
        } catch (error) {
            console.error('Error updating quantity:', error);
        }
    };

    window.deleteWish = async (wishId) => {
        if (!confirm('정말로 삭제하시겠습니까?')) return;
        try {
            const response = await fetch(`/api/wishes/${wishId}`, {
                method: 'DELETE',
                headers
            });
            if (response.ok) {
                alert('삭제되었습니다.');
                fetchWishes(currentWishPage);
            } else {
                alert('삭제 실패');
            }
        } catch (error) {
            console.error('Error deleting wish:', error);
        }
    };
    
    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        window.location.href = '/login';
    });

    const initializeApp = async () => {
        const isValid = await validateToken();
        if (!isValid) return;
        
        checkUserRole();
        fetchProducts();
        fetchWishes();
    };

    initializeApp();
}); 