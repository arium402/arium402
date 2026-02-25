// 차트 초기화
let mileageChart;

function initChart(chartData) {
	const ctx = document.getElementById('mileageChart').getContext('2d');
  	// 기존 차트가 있으면 파괴
	if (mileageChart) {
		mileageChart.destroy();
	}
	//API에서 받은 데이터 사용
	const myMile = chartData.myMile || 0;
	const deptAvg = chartData.deptAvg || 0;
	const gradeAvg = chartData.gradeAvg || 0;
	const totalAvg = chartData.totalAvg || 0;
	//차트 최대값 계산 (가장 큰 값의 1.2배)
	const maxValue = Math.max(myMile, deptAvg, gradeAvg, totalAvg);
	const chartMax = Math.ceil(maxValue * 1.2 / 100) * 100; // 100 단위로 올림
	
	
	// 차트 최대값 계산 (가장 큰 값의 1.2배)
	mileageChart = new Chart(ctx, {
		type: 'bar',
		data: {
			labels: ['내 포인트', '학과평균', '학년평균', '전체평균'],
			datasets: [{
				label: '마일리지',
				data: [myMile, deptAvg, gradeAvg, totalAvg],
				backgroundColor: ['#4a6fa5', '#6c7b95', '#8e9db3', '#b0bdd1'],
				borderColor: ['#4a6fa5', '#6c7b95', '#8e9db3', '#b0bdd1'],
				borderWidth: 0
			}]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			devicePixelRatio: (window.devicePixelRatio || 1) * 2, //그래프 화질 선명하게
			plugins: {
				legend: {
					display: false
				}
			},
			scales: {
				y: {
					beginAtZero: true,
					max: chartMax,	//동적 최대값
					ticks: {
						callback: function(value) {
						return value + 'P';
						}
					},
					grid: {
						color: '#e0e0e0'
					}
				},
				x: {
					grid: {
						display: false
					}
				},
			},
			onHover: (event, activeElements) => {
				event.native.target.style.cursor = activeElements.length > 0 ? 'pointer' : 'default';
			},
			plugins: [{
				afterDatasetsDraw: function(chart) {
					const ctx = chart.ctx;
					chart.data.datasets.forEach((dataset, i) => {
						const meta = chart.getDatasetMeta(i);
						meta.data.forEach((bar, index) => {
							const data = dataset.data[index];
							ctx.fillStyle = '#333';
							ctx.font = 'bold 12px Arial';
							ctx.textAlign = 'center';
							ctx.textBaseline = 'bottom';
							ctx.fillText(data + 'P', bar.x, bar.y - 5);
						});
					});
				}
			}]
		}
	});
}

//차트 데이터 로드(API호출)
function loadChartData(){
	
	fetch('/student/mileage/api/chart')
			.then(response => response.json())
			.then(data => {
				if (data.success) {
					console.log('차트 데이터 로드 성공:', data);
					// 차트 초기화 (데이터 전달)
					initChart({
						myMile: data.myMile,
						deptAvg: data.deptAvg,
						gradeAvg: data.gradeAvg,
						totalAvg: data.totalAvg
					});
				} else {
					console.error('차트 데이터 로드 실패:', data.message);
					// 에러 시 기본값으로 차트 표시
					initChart({
						myMile: 0,
						deptAvg: 0,
						gradeAvg: 0,
						totalAvg: 0
					});
					if (data.redirectUrl) {
						window.location.href = data.redirectUrl;
					}
				}
			})
			.catch(error => {
				console.error('차트 데이터 API 호출 오류:', error);
				// 에러 시 기본값으로 차트 표시
				initChart({
					myMile: 0,
					deptAvg: 0,
					gradeAvg: 0,
					totalAvg: 0
				});
			});
}

// 윈도우 리사이즈시 차트 다시 그리기
function resizeChart() {
	console.log('resizeChart 호출됨');
	if (mileageChart) {
		console.log('차트 resize 실행');
		
		//  차트 영역과 테이블 영역 높이 동기화
		const chartArea = document.querySelector('.chart-area');
		const tableArea = document.querySelector('.table-area');
		
		if (chartArea && tableArea) {
			// 토글 상태 확인
			const guideCollapse = document.getElementById('mileageGuide');
			const isGuideOpen = guideCollapse && guideCollapse.classList.contains('show');
			
			if (!isGuideOpen) {
				// 토글 닫혔을 때: 테이블 높이에 맞춤
				const tableHeight = tableArea.offsetHeight;
				chartArea.style.height = tableHeight + 'px';
				console.log('토글 닫힘 - 차트 높이:', tableHeight);
			} else {
				// 토글 열렸을 때: 높이 제한 해제
				chartArea.style.height = 'auto';
				console.log('토글 열림 - 차트 높이: auto');
			}
		}
		
		mileageChart.resize();
		console.log('resize 완료');
	}
}
let currentPage = 0;
const pageSize = 10;

// 페이지 로드 시 내역 조회
document.addEventListener('DOMContentLoaded', function() {
	console.log('페이지 로드됨');
	//차트 데이터 로드 (API 호출)
	loadChartData();
	
	//알림 로드 추가
	loadNotis();
	
	console.log('차트 초기화 완료');
	setDefaultDateRange();
	
	//마일리지 내역 로드
	loadMileHistory(0);

	// 윈도우 리사이즈 이벤트 리스너 추가
	window.addEventListener('resize', function() {
		setTimeout(resizeChart, 100);
	});
  
	// 마일리지 가이드 콜랩스 이벤트 리스너
	const guideCollapse = document.getElementById('mileageGuide');
	if (guideCollapse) {
		guideCollapse.addEventListener('show.bs.collapse', function () {
			console.log('가이드 열기 시작');
			const arrow = document.getElementById('guideArrow');
			if (arrow) arrow.style.transform = 'rotate(180deg)';
		});

		guideCollapse.addEventListener('hide.bs.collapse', function () {
			console.log('가이드 닫기 시작');
			const arrow = document.getElementById('guideArrow');
			if (arrow) arrow.style.transform = 'rotate(0deg)';
		});
		// 토글 완료 후 차트 resize
		guideCollapse.addEventListener('shown.bs.collapse', function() {
			console.log('가이드 열기 완료 - 차트 resize 시작');
			setTimeout(() => {
				console.log('차트 resize 실행');
				resizeChart();
			}, 300);
		});

		guideCollapse.addEventListener('hidden.bs.collapse', function() {
			console.log('가이드 닫기 완료 - 차트 resize 시작');
			setTimeout(() => {
				console.log('차트 resize 실행');

				//차트 resize
				resizeChart();
			}, 300);
		});
	}

	document.getElementById('filterType')?.addEventListener('change', () => loadMileHistory(0));
	document.getElementById('filterStatus')?.addEventListener('change', () => loadMileHistory(0));
	
});

//마일리지 내역 조회
function loadMileHistory(page) {
	// 필터 값 가져오기
	const filterType = document.getElementById('filterType')?.value || '';
	const filterStatus = document.getElementById('filterStatus')?.value || '';
	const startDate = document.getElementById('startDate')?.value || '';
	const endDate = document.getElementById('endDate')?.value || '';
	
	let url = `/student/mileage/api/history?page=${page}&size=${pageSize}`;
	
	if (filterType) url += `&type=${encodeURIComponent(filterType)}`;
	if (filterStatus) url += `&status=${encodeURIComponent(filterStatus)}`;
	if (startDate) url += `&startDate=${startDate}`;
	if (endDate) url += `&endDate=${endDate}`;
	
	fetch(url)
		.then(response => response.json())
		.then(data => {
			if (data.success) {
				renderTable(data.histories, data.pagination);
				renderPagination(data.pagination);
			} else {
				console.error('내역 조회 실패:', data.message);
				if (data.redirectUrl) {
					window.location.href = data.redirectUrl;
				}
			}
		})
		.catch(error => {
			console.error('API 호출 오류:', error);
		});
}

//테이블 렌더링
function renderTable(hists, paging) {
	const tbody = document.querySelector('.history-table tbody');
	
	if (!hists || hists.length === 0) {
		tbody.innerHTML = '<tr><td colspan="6" class="text-center">내역이 없습니다.</td></tr>';
		return;
	}
	
	tbody.innerHTML = hists.map((h, idx) => {
			//  [] 안의 텍스트만 색상 적용
			let notesHtml = h.notes;
			
			if (h.notes && h.notes.includes('[마일리지 적립]')) {
				notesHtml = h.notes.replace('[마일리지 적립]', '<span class="badge-earned">[마일리지 적립]</span>');
			} else if (h.notes && h.notes.includes('[전환 신청]')) {
				notesHtml = h.notes.replace('[전환 신청]', '<span class="badge-pending">[전환 신청]</span>');
			} else if (h.notes && h.notes.includes('[전환 완료]')) {
				notesHtml = h.notes.replace('[전환 완료]', '<span class="badge-completed">[전환 완료]</span>');
			}
			
			return `
				<tr>
					<td>${paging.totalElements - (paging.currentPage * pageSize + idx)}</td>
					<td>${h.mlgDtFmt}</td>
					<td class="${h.mlgScore > 0 ? 'score-positive' : h.mlgScore < 0 ? 'score-negative' : ''}">${h.mlgScoreFmt}</td>
					<td>${h.mlgType}</td>
					<td>${notesHtml}</td>
					<td><span class="status-badge ${h.statusClass}">${h.statusNm}</span></td>
				</tr>
			`;
		}).join('');
}

//페이지네이션 렌더링
function renderPagination(paging) {
	const paginationUl = document.querySelector('.pagination');
	
	if (!paging || paging.totalPages === 0) {
		paginationUl.innerHTML = '';
		return;
	}
	
	currentPage = paging.currentPage;
	
	let html = '';
	
	// 이전 버튼
	html += `
		<li class="page-item ${!paging.hasPrevious ? 'disabled' : ''}">
			<a class="page-link" href="#" onclick="changePage(${currentPage - 1}); return false;">이전</a>
		</li>
	`;
	
	// 페이지 번호 (최대 5개)
	const startPage = Math.max(0, currentPage - 2);
	const endPage = Math.min(paging.totalPages - 1, startPage + 4);
	
	for (let i = startPage; i <= endPage; i++) {
		html += `
			<li class="page-item ${i === currentPage ? 'active' : ''}">
				<a class="page-link" href="#" onclick="changePage(${i}); return false;">${i + 1}</a>
			</li>
		`;
	}
	
	// 다음 버튼
	html += `
		<li class="page-item ${!paging.hasNext ? 'disabled' : ''}">
			<a class="page-link" href="#" onclick="changePage(${currentPage + 1}); return false;">다음</a>
		</li>
	`;
	
	paginationUl.innerHTML = html;
}

//페이지 변경
function changePage(page) {
	loadMileHistory(page);
	window.scrollTo({ top: 0, behavior: 'smooth' });
}

//새로고침
function refreshData() {
	const refreshBtn = event.target.closest('button');
	const originalText = refreshBtn.innerHTML;
	refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>새로고침';
	refreshBtn.disabled = true;

	setTimeout(() => {
		loadMileHistory(currentPage);
		addNewNotification();
		
		refreshBtn.innerHTML = originalText;
		refreshBtn.disabled = false;
		
		alert('데이터가 새로고침되었습니다.');
	}, 1500);
}


// 기본 날짜 범위 설정 (최근 3개월)
function setDefaultDateRange() {
	//로컬 시간대 사용 (한국에서 접속하면 자동으로 KST)
	const formatDate = (date) => {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, '0');
		const day = String(date.getDate()).padStart(2, '0');
		return `${year}-${month}-${day}`;
	};
	
	const today = new Date();
	const threeMonthsAgo = new Date();
	threeMonthsAgo.setMonth(today.getMonth() - 3);

	document.getElementById('endDate').value = formatDate(today);
	document.getElementById('startDate').value = formatDate(threeMonthsAgo);
}

// 날짜 필터 적용
function applyDateFilter() {
	const startDate = document.getElementById('startDate').value;
	const endDate = document.getElementById('endDate').value;

	if (startDate && endDate) {
		if (new Date(startDate) > new Date(endDate)) {
			alert('시작 날짜는 종료 날짜보다 빨라야 합니다.');
			return;
		}
		// 0페이지부터 다시 조회
		loadMileHistory(0);
		// 테이블 데이터 필터링 로직이 여기에 들어갈 것
		console.log('날짜 필터 적용:', startDate, '~', endDate);
	}
	else {
		alert('시작 날짜와 종료 날짜를 모두 선택해주세요.');
	}
}

// 알림 로드 (API 호출)
function loadNotis() {
	console.log('알림 로드 시작');
	
	fetch('/student/mileage/api/notis')
		.then(response => response.json())
		.then(data => {
			if (data.success) {
				renderNotis(data.notis);
			} else {
				console.error('알림 로드 실패:', data.message);
				// 에러 시 빈 알림 영역
				document.getElementById('notificationArea').innerHTML = '';
			}
		})
		.catch(error => {
			console.error('알림 API 호출 오류:', error);
			document.getElementById('notificationArea').innerHTML = '';
		});
}

// 알림 렌더링
function renderNotis(notis) {
	const notiArea = document.getElementById('notificationArea');
	
	if (!notis || notis.length === 0) {
		notiArea.innerHTML = '';
		return;
	}
	
	const html = notis.map((noti) => {
		// 타입별 클래스 및 메시지 생성
		let typeClass = '';
		let message = '';
		
		if (noti.type === 'earned') {
			typeClass = 'notification-new';
			// "새로운 마일리지 적립! 리더십 개발 프로그램 참여로 +60P가 적립되었습니다."
			message = `<strong>${noti.title}</strong> ${noti.scoreText}가 적립되었습니다.`;
			
		} else if (noti.type === 'pending') {
			typeClass = '';
			// "마일리지 장학금 전환 신청! -200P 전환 신청이 완료되었습니다."
			message = `<strong>${noti.title}</strong> ${noti.scoreText} 전환 신청이 완료되었습니다.`;
			
		} else {  // converted
			typeClass = '';
			// "마일리지 장학금 전환 완료! 2,000원이 지급되었습니다."
			message = `<strong>${noti.title}</strong> ${noti.scoreText}이 지급되었습니다.`;
		}
		
		const timeAgo = getTimeAgo(noti.dt);
		
		return `
			<div class="notification-item ${typeClass}">
				<div class="notification-content">
					<i class="${noti.icon} notification-icon"></i>
					<div>
						<p class="notification-text">${message}</p>
						<span class="notification-time">${timeAgo}</span>
					</div>
				</div>
				<button class="notification-close" onclick="closeNotification(this)">
					<i class="fas fa-times"></i>
				</button>
			</div>
		`;
	}).join('');
	
	notiArea.innerHTML = html;
}

// 시간 차이 계산 (X초/분/시간/일 전)
function getTimeAgo(dt) {
	try {
		// 날짜 파싱
		const eventTime = new Date(dt.replace(' ', 'T'));
		const now = new Date();
		
		// 밀리초 차이 계산
		const diff = now - eventTime;
		const seconds = Math.floor(diff / 1000);
		const minutes = Math.floor(seconds / 60);
		const hours = Math.floor(minutes / 60);
		const days = Math.floor(hours / 24);
		
		// 시간 단위 결정
		if (seconds < 60) {
			return `${seconds}초 전`;
		} else if (minutes < 60) {
			return `${minutes}분 전`;
		} else if (hours < 24) {
			return `${hours}시간 전`;
		} else {
			return `${days}일 전`;
		}
	} catch (error) {
		console.error('시간 계산 오류:', error);
		return '방금 전';
	}
}

// 알림 닫기
function closeNotification(button) {
	const notification = button.closest('.notification-item');
	notification.style.animation = 'slideOut 0.3s ease-in forwards';
	setTimeout(() => {
		notification.remove();
	}, 300);
}

// 새로운 알림 추가 (최대 2개 유지)
function addNewNotification(message, type = 'new') {
	const notificationArea = document.getElementById('notificationArea');
	
	// 기존 알림 개수 확인
	const existingNotis = notificationArea.querySelectorAll('.notification-item');
	
	// 2개 이상이면 가장 오래된 알림 제거
	if (existingNotis.length >= 2) {
		const oldestNoti = existingNotis[existingNotis.length - 1];
		oldestNoti.style.animation = 'slideOut 0.3s ease-in forwards';
		setTimeout(() => {
			oldestNoti.remove();
		}, 300);
	}
	
	// 새 알림 생성
	const newNotification = `
		<div class="notification-item ${type === 'new' ? 'notification-new' : ''}">
			<div class="notification-content">
				<i class="fas fa-bell notification-icon"></i>
				<div>
					<p class="notification-text">${message}</p>
					<span class="notification-time">방금 전</span>
				</div>
			</div>
			<button class="notification-close" onclick="closeNotification(this)">
				<i class="fas fa-times"></i>
			</button>
		</div>
	`;
	
	// 맨 위에 추가
	notificationArea.insertAdjacentHTML('afterbegin', newNotification);
}

function showMileageHistory() {
	console.log('현재 마일리지 내역 페이지입니다.');
}

function showConvertModal() {
	const modal = new bootstrap.Modal(document.getElementById('convertModal'));
	modal.show();
}

function applyConvert() {
	const convertAmount = parseInt(document.getElementById('convertAmount').value) || 0;
	const bankAccount = document.getElementById('bankAccount').value;
	const bankName = document.getElementById('bankName').value;
	const depositor = document.getElementById('depositor').value;
	const availableAmount = parseInt(
	        document.querySelector('.current-mileage-value span').textContent
	    );

	if (convertAmount <= 0) {
		alert('신청할 마일리지를 입력해주세요.');
		return;
	}
	
	if (convertAmount < 100) {
		alert('최소 전환 단위는 100P입니다.');
	    return;
	}
	
	if (convertAmount % 100 !== 0) {
	    alert('100P 단위로 입력해주세요.');
	    return;
	}

	if (convertAmount > availableAmount) {
	    alert('보유 마일리지를 초과했습니다.');
		return;
	}


	// 전환 금액 계산 (1000p = 10,000원)
	const convertedAmount = (convertAmount / 10) * 100;

	if (confirm(`${convertAmount.toLocaleString()}P를 ${convertedAmount.toLocaleString()}원으로 전환하시겠습니까?\n\n입금 계좌: ${bankName} ${bankAccount}\n예금주: ${depositor}\n\n신청 후 3~5일 내 입금됩니다.`)) {
		if (confirm('주의: 전환 신청 후에는 취소할 수 없습니다.\n\n정말로 신청하시겠습니까?')) {
		
		//서버로 데이터 전송
		fetch('/student/mileage/api/convert',{
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded'
			},
			body: `convertAmount=${convertAmount}&bankName=${bankName}&bankAccount=${bankAccount}&depositor=${depositor}`
		})
		.then(response => response.json())
		.then(data =>{
			if(data.success){
				alert("마일리지 전환 신청이 완료되었습니다.");
				//모달 닫고 페이지 새로고침
				const modal = bootstrap.Modal.getInstance(document.getElementById('convertModal'));
				modal.hide();
				resetConvertForm();
				
				//새로고침 > 마일리지 업데이트 반영
				window.location.reload();
			}else{
				//서버 검증 실패 시 서버 메시지 표시
				alert(data.message);
			}
		})
		.catch(error => {
			console.error('Error:', error);
			alert('서버 오류가 발생했습니다.');
		});
		
		} 
	}
}

function cancelConvert() {
	const modal = bootstrap.Modal.getInstance(document.getElementById('convertModal'));
	modal.hide();
}

function resetConvertForm() {
	document.getElementById('convertAmount').value = '';
}