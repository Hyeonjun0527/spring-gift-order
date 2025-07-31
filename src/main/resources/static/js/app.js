document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const kakaoAuthCode = urlParams.get('code');

    if (kakaoAuthCode) {
        await handleKakaoLogin(kakaoAuthCode);
        return;
    }

    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken || accessToken === 'undefined') {
        localStorage.removeItem('accessToken');
        if (window.location.pathname !== '/login') {
            window.location.href = '/login';
        }
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
    const addOptionButton = document.getElementById('add-option-btn');
    const optionsContainer = document.getElementById('options-container');

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

    const checkUserRole = () => {
        const userRole = localStorage.getItem('userRole');
        if (userRole === 'ADMIN') {
            adminLink.style.display = 'inline-block';
        } else {
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
            
            const optionsHtml = product.options.length > 0
                ? `<select id="option-select-${product.id}">
                        ${product.options.map(option => `<option value="${option.id}">${option.name} (${option.quantity}개 남음)</option>`).join('')}
                   </select>`
                : '<span>옵션 없음</span>';

            item.innerHTML = `
                <div>
                    <strong>${product.name}</strong> - ${product.price}원
                </div>
                <div class="product-controls">
                    ${optionsHtml}
                    <button ${product.options.length === 0 ? 'disabled' : ''} onclick="addWish(${product.id})">위시리스트에 추가</button>
                </div>
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
                    <strong>${wish.productName}</strong> (${wish.optionName}) - ${wish.productPrice}원
                </div>
                <div class="wish-item-controls">
                    <input type="number" value="${wish.quantity}" min="1" onchange="updateWishQuantity(${wish.wishId}, this.value)" style="width: 50px;">
                    <button onclick="placeOrder(${wish.optionId}, this.previousElementSibling.value)">주문하기</button>
                    <button onclick="deleteWish(${wish.wishId})">삭제</button>
                </div>`;
            wishList.appendChild(item);
        });
    };

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

    addOptionButton.addEventListener('click', () => {
        const optionRow = document.createElement('div');
        optionRow.className = 'option-row';
        optionRow.innerHTML = `
            <input type="hidden" name="optionId" class="option-id" value="">
            <input type="text" placeholder="옵션명 (예: L 사이즈)" class="option-name" required>
            <input type="number" placeholder="수량" class="option-quantity" required min="1">
            <button type="button" onclick="this.parentElement.remove()">×</button>
        `;
        optionsContainer.appendChild(optionRow);
    });

    addProductForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const optionRows = optionsContainer.querySelectorAll('.option-row');
        const options = Array.from(optionRows).map(row => ({
            id : row.querySelector('.option-id').value ? parseInt(row.querySelector('.option-id').value) : null,
            name: row.querySelector('.option-name').value,
            quantity: parseInt(row.querySelector('.option-quantity').value)
        }));

        const imageUrl = document.getElementById('product-imageUrl').value.trim();
        const productData = {
            name: document.getElementById('product-name').value,
            price: parseInt(document.getElementById('product-price').value),
            imageUrl: imageUrl || null,
            options: options
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
                optionsContainer.innerHTML = '<h4>옵션</h4>'; 
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
        const optionSelect = document.getElementById(`option-select-${productId}`);
        const optionId = optionSelect.value;
        
        try {
            const response = await fetch('/api/wishes', {
                method: 'POST',
                headers,
                body: JSON.stringify({ optionId: parseInt(optionId), quantity: 1 })
            });
            if (response.ok) {
                alert('위시리스트에 추가되었습니다.');
                fetchWishes(currentWishPage);
            } else {
                const errorData = await response.json();
                alert(errorData.message || '추가 실패');
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

    window.placeOrder = async (optionId, quantity) => {
        const messageInput = document.getElementById('order-message');
        const message = messageInput.value.trim();

        if (!confirm(`주문하시겠습니까?`)) return;

        try {
            const response = await fetch('/api/orders', {
                method: 'POST',
                headers,
                body: JSON.stringify({
                    optionId: optionId,
                    quantity: parseInt(quantity, 10),
                    message: message
                })
            });

            if (response.status === 201) {
                alert('주문이 완료되었습니다! 카카오톡 메시지를 확인해주세요.');
                messageInput.value = ''; // 메시지 입력란 초기화
                await fetchWishes(); // 위시리스트 갱신
            } else {
                const errorData = await response.json();
                alert(errorData.detail || '주문에 실패했습니다.');
            }
        } catch (error) {
            console.error('주문 처리 중 오류 발생:', error);
            alert('주문 처리 중 오류가 발생했습니다.');
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

async function handleKakaoLogin(code) {
    try {
        const response = await fetch(`/api/members/kakao/callback?code=${code}`);
        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('accessToken', data.token);
            localStorage.setItem('userRole', data.role); // 역할 정보 저장
            window.history.replaceState({}, document.title, "/");
            window.location.href = '/';
        } else {
            console.error('카카오 로그인 실패:', await response.text());
            alert('카카오 로그인에 실패했습니다.');
            window.location.href = '/login';
        }
    } catch (error) {
        console.error('카카오 로그인 처리 중 오류 발생:', error);
        alert('로그인 처리 중 오류가 발생했습니다.');
        window.location.href = '/login';
    }
} 