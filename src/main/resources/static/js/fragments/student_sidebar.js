// 헤더
function logout() {
	if (confirm('로그아웃 하시겠습니까?')) {
		alert('로그아웃 되었습니다.');
	}
}

// 사이드바
document.addEventListener("DOMContentLoaded", function () {
	const path = window.location.pathname;

	// 현재 URL에 맞는 메뉴 자동 활성화
	document.querySelectorAll('.sidebar a').forEach(link => {
		const href = link.getAttribute('href');
		
		if (href && path.startsWith(href)) {
			link.classList.add('active');

			const li = link.closest('li');
			
			if (li) li.classList.add('active');

			const submenu = link.closest('.submenu');
			
			if (submenu) {
				const parentLi = submenu.closest('li');
				
				if (parentLi) {
					parentLi.classList.add('active');
					
					const submenuEl = parentLi.querySelector('.submenu');
					
					if (submenuEl) submenuEl.style.display = 'block'; // 자동 열기
				}
			}
		}
	});

	// 사이드바 메뉴 토글 함수 정의
	window.toggleSubmenu = function (element) {
		const parent = element.parentElement;
		const submenu = parent.querySelector(".submenu");

		// 다른 메뉴 닫기
		document.querySelectorAll('.sidebar li.active').forEach(item => {
			if (item !== parent) {
				item.classList.remove('active');
				
				const sub = item.querySelector('.submenu');
				
				if (sub) sub.style.display = 'none';
			}
		});

		// 현재 메뉴 열기/닫기
		const isOpen = submenu && submenu.style.display === 'block';
		
		if (submenu) {
			submenu.style.display = isOpen ? 'none' : 'block';
		}

		parent.classList.toggle('active', !isOpen);
	};
});