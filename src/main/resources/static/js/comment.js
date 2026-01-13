// ========== 댓글 관련 기능 ==========
document.addEventListener('DOMContentLoaded', function () {

    // ✅ 댓글 작성 폼 검증
    const commentForm = document.getElementById('comment-form');
    if (commentForm) {
        commentForm.addEventListener('submit', function (e) {
            const textarea = document.getElementById('comment-content');
            if (!textarea || textarea.value.trim() === '') {
                alert('댓글 내용을 입력해주세요.');
                e.preventDefault();
                return false;
            }
        });
    }

    // ✅ 댓글 삭제 확인
    document.querySelectorAll('.btn-comment-delete').forEach(btn => {
        const form = btn.closest('form');
        if (!form) return;

        form.addEventListener('submit', function (e) {
            if (!confirm('정말 이 댓글을 삭제하시겠습니까?')) {
                e.preventDefault();
                return false;
            }
        });
    });

    // ✅ 댓글 수정 모드 열기
    document.querySelectorAll('.btn-comment-edit').forEach(btn => {
        btn.addEventListener('click', function () {
            const item = this.closest('.comment-item');
            if (!item) return;

            const view = item.querySelector('.comment-content-view');
            const editBox = item.querySelector('.comment-edit-box');
            if (!editBox) return;

            if (view) view.classList.add('hidden');
            editBox.classList.remove('hidden');

            // UX: 커서 포커스
            const textarea = editBox.querySelector('.comment-edit-textarea');
            if (textarea) {
                textarea.focus();
                textarea.setSelectionRange(textarea.value.length, textarea.value.length);
            }
        });
    });

    // ✅ 댓글 수정 취소
    document.querySelectorAll('.btn-edit-cancel').forEach(btn => {
        btn.addEventListener('click', function () {
            const item = this.closest('.comment-item');
            if (!item) return;

            const view = item.querySelector('.comment-content-view');
            const editBox = item.querySelector('.comment-edit-box');
            if (!editBox) return;

            editBox.classList.add('hidden');
            if (view) view.classList.remove('hidden');
        });
    });

    // ✅ 댓글 수정 저장: 서버 form submit (비어있을 때만 제출 막기)
    document.querySelectorAll('.btn-edit-save').forEach(btn => {
        btn.addEventListener('click', function (e) {
            const item = this.closest('.comment-item');
            if (!item) return;

            const textarea = item.querySelector('.comment-edit-textarea');
            const newContent = textarea ? textarea.value.trim() : '';

            if (!newContent) {
                alert('수정할 내용을 입력해주세요.');
                e.preventDefault();
                return;
            }
            // ✅ 유효하면 그대로 submit
        });
    });

    // ✅ 신고 버튼 (일단 UI만, 모달/API는 다음 단계)
    document.querySelectorAll('.btn-comment-report').forEach(btn => {
        btn.addEventListener('click', function () {
            const item = this.closest('.comment-item');
            const commentId = item ? item.getAttribute('data-comment-id') : null;
            alert(`(UI) 댓글 신고: ${commentId ?? ''} (다음 단계에서 모달/서버 연결)`);
        });
    });

    // ✅ 댓글 글자 수 카운트(작성폼)
    const commentTextarea = document.getElementById('comment-content');
    const countEl = document.getElementById('comment-char-count');
    if (commentTextarea && countEl) {
        const renderCount = () => {
            const len = commentTextarea.value.length;
            countEl.textContent = `${len}/500`;
        };
        commentTextarea.addEventListener('input', renderCount);
        renderCount();
    }
});
