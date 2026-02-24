// 차트 초기화
let mileageChart;

function initChart() {
	const ctx = document.getElementById('mileageChart').getContext('2d');
  	// 기존 차트가 있으면 파괴
	if (mileageChart) {
		mileageChart.destroy();
	}

	mileageChart = new Chart(ctx, {
		type: 'bar',
		data: {
			labels: ['내 포인트', '학과평균', '학년평균', '전체평균'],
			datasets: [{
				label: '마일리지',
				data: [1850, 1245, 1180, 965],
				backgroundColor: ['#4a6fa5', '#6c7b95', '#8e9db3', '#b0bdd1'],
				borderColor: ['#4a6fa5', '#6c7b95', '#8e9db3', '#b0bdd1'],
				borderWidth: 0
			}]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			plugins: {
				legend: {
					display: false
				}
			},
			scales: {
				y: {
					beginAtZero: true,
					max: 2000,
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

// 윈도우 리사이즈시 차트 다시 그리기
function resizeChart() {
	if (mileageChart) {
		mileageChart.resize();
	}
}

let currentPage = 0;
const pageSize = 10;

// 페이지 로드 시 내역 조회
document.addEventListener('DOMContentLoaded', function() {
	console.log('페이지 로드됨');
	initChart();
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
			const arrow = document.getElementById('guideArrow');
			if (arrow) arrow.style.transform = 'rotate(180deg)';
		});

		guideCollapse.addEventListener('hide.bs.collapse', function () {
			const arrow = document.getElementById('guideArrow');
			if (arrow) arrow.style.transform = 'rotate(0deg)';
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
	
	tbody.innerHTML = hists.map((h, idx) => `
		<tr>
			<td>${paging.totalElements - (paging.currentPage * pageSize + idx)}</td>
			<td>${h.mlgDtFmt}</td>
			<td class="${h.mlgScore > 0 ? 'score-positive' : h.mlgScore < 0 ? 'score-negative' : ''}">${h.mlgScoreFmt}</td>
			<td>${h.mlgType}</td>
			<td>${h.notes}</td>
			<td><span class="status-badge ${h.statusClass}">${h.statusNm}</span></td>
		</tr>
	`).join('');
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
	const today = new Date();
	const threeMonthsAgo = new Date();
	threeMonthsAgo.setMonth(today.getMonth() - 3);

	document.getElementById('endDate').value = today.toISOString().split('T')[0];
	document.getElementById('startDate').value = threeMonthsAgo.toISOString().split('T')[0];
}

// 알림 닫기
function closeNotification(button) {
	const notification = button.closest('.notification-item');
	notification.style.animation = 'slideOut 0.3s ease-in forwards';
	setTimeout(() => {
		notification.remove();
	}, 300);
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

// 새로운 알림 추가
function addNewNotification() {
	const notificationArea = document.getElementById('notificationArea');
	const newNotification = `
		<div class="notification-item notification-new">
			<div class="notification-content">
				<i class="fas fa-bell notification-icon"></i>
				<div>
					<p class="notification-text"><strong>새로운 마일리지 적립!</strong> 창의적 사고력 개발 프로그램 참여로 +45P가 적립되었습니다.</p>
					<span class="notification-time">방금 전</span>
				</div>
			</div>
			<button class="notification-close" onclick="closeNotification(this)">
				<i class="fas fa-times"></i>
			</button>
		</div>
	`;

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