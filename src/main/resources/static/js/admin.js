function deleteProduct(id) {
      if (!confirm('정말 삭제하시겠습니까?')) {
          return;
      }
      fetch(`/admin/products/${id}/delete`, {
          method: 'POST'
      })
      .then(response => {
          if (response.ok) {
              document.querySelector(`tr[data-id="${id}"]`).remove();
              alert('상품이 삭제되었습니다.');
          } else {
              return response.text().then(text => { throw new Error(text); });
          }
      })
      .catch(error => {
          alert('삭제 실패: ' + error.message);
      });
  }