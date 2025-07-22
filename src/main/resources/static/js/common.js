/**
 * 페이지네이션 컨트롤을 렌더링하는 공통 함수
 * @param {HTMLElement} container - 페이지네이션 버튼이 들어갈 컨테이너 요소
 * @param {object} pageData - API로부터 받은 페이지네이션 데이터 객체 (content, totalPages, number, first, last 등)
 * @param {function} fetchFunction - 특정 페이지를 가져오는 데 사용될 함수
 */
function renderPagination(container, pageData, fetchFunction) {
    container.innerHTML = '';
    if (!pageData || pageData.totalPages <= 1) {
        return;
    }

    const currentPage = pageData.number || 0;

    // 이전 버튼
    if (!pageData.first) {
        const prevButton = document.createElement('button');
        prevButton.innerText = '이전';
        const prevPage = currentPage - 1;
        prevButton.onclick = () => fetchFunction(Math.max(0, prevPage));
        container.appendChild(prevButton);
    }

    // 다음 버튼
    if (!pageData.last) {
        const nextButton = document.createElement('button');
        nextButton.innerText = '다음';
        const nextPage = currentPage + 1;
        nextButton.onclick = () => fetchFunction(Math.min(pageData.totalPages - 1, nextPage));
        container.appendChild(nextButton);
    }
} 