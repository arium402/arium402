// 비공개 설정 안내 토글
function togglePrivacyNotice() {
	const privateRadio = document.getElementById('private');
	const notice = document.getElementById('privateNotice');

	if (privateRadio.checked) {
		notice.classList.add('show');
	}
	else {
		notice.classList.remove('show');
	}
}

// 제목 글자 수 카운터
function updateTitleCounter() {
	const titleInput = document.getElementById('title');
	const counter = document.getElementById('titleCounter');
	const currentLength = titleInput.value.length;
	const maxLength = 100;

	counter.textContent = `${currentLength} / ${maxLength}`;

	if (currentLength > maxLength * 0.9) {
		counter.classList.add('warning');
	}
	else {
		counter.classList.remove('warning');
	}
}

// 내용 글자 수 카운터
function updateContentCounter() {
	const contentTextarea = document.getElementById('content');
	const counter = document.getElementById('contentCounter');
	const currentLength = contentTextarea.value.length;
	const maxLength = 2000;

	counter.textContent = `${currentLength} / ${maxLength}`;

	if (currentLength > maxLength * 0.9) {
		counter.classList.add('warning');
	}
	else {
		counter.classList.remove('warning');
	}
}

// 폼 유효성 검사
function validateForm() {
	let isValid = true;

	// 기존 에러 메시지 제거
	document.querySelectorAll('.form-group').forEach(group => {
		group.classList.remove('error');
	});
	document.querySelectorAll('.error-message').forEach(msg => {
		msg.remove();
	});

	// 유형 선택 검사
	const category = document.getElementById('category');
	if (!category.value) {
		showError(category, '유형을 선택해주세요.');
		isValid = false;
	}

	// 제목 검사
	const title = document.getElementById('title');
	if (!title.value.trim()) {
		showError(title, '제목을 입력해주세요.');
		isValid = false;
	}
	else if (title.value.trim().length < 5) {
		showError(title, '제목은 5자 이상 입력해주세요.');
		isValid = false;
	}

	// 내용 검사
	const content = document.getElementById('content');
	if (!content.value.trim()) {
		showError(content, '내용을 입력해주세요.');
		isValid = false;
	}
	else if (content.value.trim().length < 10) {
		showError(content, '내용은 10자 이상 입력해주세요.');
		isValid = false;
	}

	return isValid;
}

// 에러 메시지 표시
function showError(element, message) {
	const formGroup = element.closest('.form-group');
	formGroup.classList.add('error');

	const errorDiv = document.createElement('div');
	errorDiv.className = 'error-message';
	errorDiv.textContent = message;

	element.parentNode.appendChild(errorDiv);
}

// 글 등록 처리
function submitPost(event) {
	event.preventDefault();

	if (!validateForm()) {
		return;
	}

	const submitBtn = document.getElementById('submitBtn');
	submitBtn.disabled = true;
	submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 등록 중...';

	// 폼 데이터 수집
	const formData = {
		category: document.getElementById('category').value,
		privacy: document.querySelector('input[name="privacy"]:checked').value,
		title: document.getElementById('title').value.trim(),
		content: document.getElementById('content').value.trim()
	};

	// 실제로는 서버에 데이터 전송
	setTimeout(() => {
		alert('문의글이 성공적으로 등록되었습니다.');

		// 부모 창의 목록 새로고침 (실제 구현 시)
		if (window.opener && window.opener.renderBoardList) {
			window.opener.renderBoardList();
		}

		// 창 닫기 또는 목록으로 이동
		if (window.opener) {
			window.close();
		}
		else {
			// 새 창이 아닌 경우 목록 페이지로 이동
			alert('목록 페이지로 이동합니다.');
			location.href = /*[[@{/student/notice/asklist}]]*/ '/student/notice/asklist';
		}
	}, 1000);
}

// 취소 처리
function cancelWrite() {
	const hasContent = document.getElementById('title').value.trim() || document.getElementById('content').value.trim();

	if (hasContent) {
		if (confirm('작성 중인 내용이 있습니다. 정말 취소하시겠습니까?')) {
			if (window.opener) {
				window.close();
			}
			else {
				// 새 창이 아닌 경우 목록 페이지로 이동
				alert('목록 페이지로 이동합니다.');
				location.href = /*[[@{/student/notice/asklist}]]*/ '/student/notice/asklist';
			}
		}
	}
	else {
		if (window.opener) {
			window.close();
		}
		else {
			alert('목록 페이지로 이동합니다.');
			location.href = /*[[@{/student/notice/asklist}]]*/ '/student/notice/asklist';
		}
	}
}

// 창 닫기 전 확인 (새 창인 경우만)
window.addEventListener('beforeunload', function(event) {
	if (window.opener) {
		const hasContent = document.getElementById('title').value.trim() || document.getElementById('content').value.trim();
	
		if (hasContent) {
			event.preventDefault();
			event.returnValue = '';
		}
	}
});

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
	// 유형 선택란에 포커스
	document.getElementById('category').focus();

	// 초기 카운터 설정
	updateTitleCounter();
	updateContentCounter();
});