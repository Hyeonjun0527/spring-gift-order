document.addEventListener('DOMContentLoaded', () => {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) {
        window.location.href = '/login';
        return;
    }

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${accessToken}`
    };

    const productListBody = document.getElementById('product-list-body');
    const productPagination = document.getElementById('product-pagination');
    const memberList = document.getElementById('member-list');
    const logoutButton = document.getElementById('logout-button');

    const productForm = document.getElementById('product-form');
    const productIdInput = document.getElementById('product-id');
    const productNameInput = document.getElementById('product-name');
    const productPriceInput = document.getElementById('product-price');
    const productImageUrlInput = document.getElementById('product-imageUrl');
    const optionsContainer = document.getElementById('options-container');
    const addOptionBtn = document.getElementById('add-option-btn');

    const clearFormButton = document.getElementById('clear-form-button');
    const productTableHeader = document.querySelector("#product-table thead");

    const state = {
        currentProductPage: 0,
        sortBy: 'id',
        order: 'asc',
    };

    const fetchProducts = async (page = 0) => {
        state.currentProductPage = page;
        const { sortBy, order } = state;
        const sortParam = `${sortBy},${order}`;
        
        try {
            const response = await fetch(`/api/products?page=${page}&size=5&sort=${sortParam}`, { headers });
            if(response.status === 401) {
                 localStorage.removeItem('accessToken');
                 window.location.href = '/login';
                 return;
            }
            const data = await response.json();
            renderProducts(data.content);
            renderPagination(productPagination, data, fetchProducts);
            updateSortIndicator();
        } catch (error) {
            console.error('Error fetching products:', error);
        }
    };

    const fetchMembers = async () => {
        try {
            const response = await fetch('/api/admin/members', { headers });
            if(response.status === 401) {
                 localStorage.removeItem('accessToken');
                 window.location.href = '/login';
                 return;
            }
             if(response.status === 403) {
                memberList.innerHTML = '<p>회원 목록을 볼 권한이 없습니다.</p>';
                return;
            }
            const data = await response.json();
            renderMembers(data);
        } catch (error) {
            console.error('Error fetching members:', error);
        }
    };

    function renderProducts(products) {
        productListBody.innerHTML = '';
        products.forEach(product => {
            const optionsHtml = product.options.map(opt =>
                `<li>${opt.name} (${opt.quantity}개)</li>`
            ).join('');

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>${product.price}</td>
                <td><img src="${product.imageUrl}" alt="${product.name}" width="80"></td>
                <td><ul>${optionsHtml}</ul></td>
                <td>
                    <button onclick="editProduct(${product.id})">수정</button>
                    <button onclick="deleteProduct(${product.id})">삭제</button>
                </td>
            `;
            productListBody.appendChild(row);
        });
    }
    
    const renderMembers = (members) => {
        memberList.innerHTML = '';
        members.forEach(member => {
            const item = document.createElement('div');
            item.className = 'member-item';
            item.innerHTML = `
                <div>
                    <strong>${member.email}</strong> (${member.role})
                </div>
            `;
            memberList.appendChild(item);
        });
    };

    /*
    const renderPagination = (container, pageData, fetchFunction) => {
        container.innerHTML = '';
        if (pageData.totalPages > 1) {
            const currentPage = pageData.number || 0;
            if (!pageData.first) {
                const prevButton = document.createElement('button');
                prevButton.innerText = '이전';
                prevButton.onclick = () => fetchFunction(currentPage - 1);
                container.appendChild(prevButton);
            }
            if (!pageData.last) {
                const nextButton = document.createElement('button');
                nextButton.innerText = '다음';
                nextButton.onclick = () => fetchFunction(currentPage + 1);
                container.appendChild(nextButton);
            }
        }
    };
    */
    
    productForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = productIdInput.value;
        const options = Array.from(optionsContainer.children).map(field => {
            const nameInput = field.querySelector('.option-name');
            const quantityInput = field.querySelector('.option-quantity');
            return {
                name: nameInput.value,
                quantity: parseInt(quantityInput.value, 10)
            };
        });

        const productData = {
            name: productNameInput.value,
            price: parseInt(productPriceInput.value),
            imageUrl: productImageUrlInput.value,
            options: options // 옵션 데이터 추가
        };

        const url = id ? `/api/products/${id}` : '/api/products';
        const method = id ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, { method, headers, body: JSON.stringify(productData) });
            if (response.ok) {
                alert('상품이 저장되었습니다.');
                clearForm();
                fetchProducts(state.currentProductPage);
            } else {
                alert('저장 실패');
            }
        } catch (error) {
            console.error('Error saving product:', error);
        }
    });

    const clearForm = () => {
        productIdInput.value = '';
        productNameInput.value = '';
        productPriceInput.value = '';
        productImageUrlInput.value = '';
        optionsContainer.innerHTML = '';
        addOptionField();
    }
    
    clearFormButton.addEventListener('click', clearForm);

    function addOptionField(option = { name: '', quantity: 1 }) {
        const optionField = document.createElement('div');
        optionField.classList.add('option-field');
        optionField.innerHTML = `
            <input type="text" class="option-name" placeholder="옵션명" value="${option.name}" required style="width: 60%;">
            <input type="number" class="option-quantity" placeholder="수량" value="${option.quantity}" min="1" required style="width: 25%;">
            <button type="button" class="remove-option-btn" style="width: 10%;">삭제</button>
        `;
        optionsContainer.appendChild(optionField);
    }

    addOptionBtn.addEventListener('click', () => addOptionField());

    optionsContainer.addEventListener('click', function(event) {
        if (event.target.classList.contains('remove-option-btn')) {

            if (optionsContainer.children.length > 1) {
                event.target.closest('.option-field').remove();
            } else {
                alert('상품에는 최소 1개의 옵션이 필요합니다.');
            }
        }
    });

    window.editProduct = async (id) => {
        try {
            const response = await fetch(`/api/products/${id}`, { headers });
            if (!response.ok) throw new Error('상품 정보를 불러오는 데 실패했습니다.');
            const product = await response.json();

            productIdInput.value = product.id;
            productNameInput.value = product.name;
            productPriceInput.value = product.price;
            productImageUrlInput.value = product.imageUrl;

            optionsContainer.innerHTML = '';
            if (product.options && product.options.length > 0) {
                product.options.forEach(option => addOptionField(option));
            } else {
                addOptionField();
            }

            window.scrollTo(0, 0);
        } catch (error) {
            console.error('상품 정보 로드 오류:', error);
            alert(error.message);
        }
    };

    window.deleteProduct = async (id) => {
        if (!confirm('정말로 삭제하시겠습니까?')) return;
        try {
            const response = await fetch(`/api/products/${id}`, { method: 'DELETE', headers });
            if (response.ok) {
                alert('삭제되었습니다.');
                clearForm();
                fetchProducts(state.currentProductPage);
            } else {
                const errorData = await response.json();
                alert('삭제 실패: ' + (errorData.message || '알 수 없는 오류'));
            }
        } catch (error) {
            console.error('Error deleting product:', error);
        }
    };

    const handleSort = (event) => {
        const target = event.target;
        if (target.tagName !== 'TH' || !target.dataset.sort) {
            return;
        }

        const newSortBy = target.dataset.sort;
        if (state.sortBy === newSortBy) {
            state.order = state.order === 'asc' ? 'desc' : 'asc';
        } else {
            state.sortBy = newSortBy;
            state.order = 'asc';
        }
        fetchProducts(0); // 정렬 시 첫 페이지부터 조회
    };

    const updateSortIndicator = () => {
        productTableHeader.querySelectorAll('th').forEach(th => {
            th.classList.remove('sorted-asc', 'sorted-desc');
            if (th.dataset.sort === state.sortBy) {
                th.classList.add(state.order === 'asc' ? 'sorted-asc' : 'sorted-desc');
            }
        });
    };
    
    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        window.location.href = '/login';
    });

    productTableHeader.addEventListener('click', handleSort);
    fetchProducts();
    fetchMembers();
    addOptionField(); // 페이지 로드 시 기본 옵션 필드 1개 추가
});
 