// ========== 이미지 미리보기 ==========
function previewImages(event) {
    const files = event.target.files;
    const preview = document.getElementById('imagePreview');
    if (!preview) return;

    preview.innerHTML = '';
    if (!files || files.length === 0) return;

    Array.from(files).forEach(file => {
        if (!file.type.startsWith('image/')) return;

        const reader = new FileReader();
        reader.onload = function(e) {
            const div = document.createElement('div');
            div.className = 'preview-item';

            const img = document.createElement('img');
            img.src = e.target.result;

            div.appendChild(img);
            preview.appendChild(div);
        };
        reader.readAsDataURL(file);
    });
}

// ========== 게시글 삭제 (view.html 버튼에서 호출) ==========
function deletePost(postId) {
    if (!postId) return;
    if (!confirm('정말 이 게시글을 삭제하시겠습니까?')) return;

    // 서버에 POST로 보내기 (CSRF 쓰면 토큰도 같이 보내야 함 - 지금은 미사용 기준)
    const form = document.createElement('form');
    form.method = 'post';
    form.action = `/posts/${postId}/delete`;
    document.body.appendChild(form);
    form.submit();
}

// ========== 폼 제출 전 확인 ==========
document.addEventListener('DOMContentLoaded', function() {
    const createForm = document.getElementById('createForm');
    const editForm = document.getElementById('editForm');

    if (createForm) {
        createForm.addEventListener('submit', function(e) {
            const title = document.getElementById('title')?.value.trim();
            const category = document.getElementById('category')?.value;

            if (!title) {
                alert('제목을 입력해주세요.');
                e.preventDefault();
                return;
            }

            if (!category) {
                alert('카테고리를 선택해주세요.');
                e.preventDefault();
                return;
            }

            // ✅ 이미지 업로드는 다음 단계라서 "필수 검사" 잠시 비활성화
            // const imagesEl = document.getElementById('images');
            // const images = imagesEl ? imagesEl.files : null;
            //
            // if (images && images.length === 0) {
            //     alert('이미지를 최소 1장 선택해주세요.');
            //     e.preventDefault();
            //     return;
            // }
            //
            // // 이미지 크기 확인 (10MB)
            // for (let i = 0; i < images.length; i++) {
            //     if (images[i].size > 10 * 1024 * 1024) {
            //         alert('이미지 크기는 10MB를 초과할 수 없습니다.');
            //         e.preventDefault();
            //         return;
            //     }
            // }
        });
    }

    if (editForm) {
        editForm.addEventListener('submit', function(e) {
            const title = document.getElementById('title')?.value.trim();
            const category = document.getElementById('category')?.value;

            if (!title) {
                alert('제목을 입력해주세요.');
                e.preventDefault();
                return;
            }

            if (!category) {
                alert('카테고리를 선택해주세요.');
                e.preventDefault();
                return;
            }
        });
    }
});
