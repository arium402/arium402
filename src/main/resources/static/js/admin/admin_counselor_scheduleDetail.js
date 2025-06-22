// 현재 년도와 월 변수
   let currentYear = 2024;
   let currentMonth = 6;

   // 월 이름 배열
   const monthNames = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];

   // 상담사 데이터 - 상담 시간 옵션 추가
   const counselorData = {
       1: {
           name: '김상담',
           empNo: 'EMP001',
           field: '심리상담',
           workTime: '09:00 ~ 18:00',
           workDays: '월, 화, 수, 목, 금',
           consultationTime: '오전 / 오후'
       },
       2: {
           name: '이상담',
           empNo: 'EMP002',
           field: '진로상담',
           workTime: '10:00 ~ 19:00',
           workDays: '월, 화, 목, 금',
           consultationTime: '09:00 ~ 13:00 / 14:00 ~ 18:00'
       },
       3: {
           name: '박상담',
           empNo: 'EMP003',
           field: '학습컨설팅',
           workTime: '09:30 ~ 17:30',
           workDays: '화, 수, 목, 금',
           consultationTime: '전체'
       },
       4: {
           name: '최상담',
           empNo: 'EMP004',
           field: '심리상담',
           workTime: '13:00 ~ 22:00',
           workDays: '월, 수, 목, 금, 토',
           consultationTime: '오후'
       },
       5: {
           name: '정상담',
           empNo: 'EMP005',
           field: '진로상담',
           workTime: '08:00 ~ 17:00',
           workDays: '월, 화, 수, 목, 금',
           consultationTime: '오전'
       }
   };

   // 상담 내역 샘플 데이터 (월별)
   const consultationData = {
       2024: {
           6: [
               { no: 1, date: '2024-06-03', time: '09:00', studentId: '202301001', studentName: '김학생' },
               { no: 2, date: '2024-06-03', time: '10:00', studentId: '202301002', studentName: '이학생' },
               { no: 3, date: '2024-06-04', time: '14:00', studentId: '202301003', studentName: '박학생' },
               { no: 4, date: '2024-06-05', time: '11:00', studentId: '202301004', studentName: '최학생' },
               { no: 5, date: '2024-06-06', time: '15:00', studentId: '202301005', studentName: '정학생' },
               { no: 6, date: '2024-06-07', time: '10:30', studentId: '202301006', studentName: '강학생' },
               { no: 7, date: '2024-06-10', time: '13:00', studentId: '202301007', studentName: '윤학생' },
               { no: 8, date: '2024-06-11', time: '16:00', studentId: '202301008', studentName: '임학생' }
           ],
           5: [
               { no: 1, date: '2024-05-02', time: '10:00', studentId: '202301009', studentName: '서학생' },
               { no: 2, date: '2024-05-03', time: '14:00', studentId: '202301010', studentName: '한학생' },
               { no: 3, date: '2024-05-08', time: '11:00', studentId: '202301011', studentName: '조학생' }
           ],
           7: [
               { no: 1, date: '2024-07-01', time: '09:00', studentId: '202301012', studentName: '신학생' },
               { no: 2, date: '2024-07-02', time: '15:00', studentId: '202301013', studentName: '오학생' }
           ]
       }
   };

   // 월 변경 함수
   function changeMonth(direction) {
       currentMonth += direction;
       
       if (currentMonth > 12) {
           currentMonth = 1;
           currentYear++;
       } else if (currentMonth < 1) {
           currentMonth = 12;
           currentYear--;
       }
       
       updateMonthDisplay();
       updateConsultationTable();
   }

   // 월 표시 업데이트
   function updateMonthDisplay() {
       document.getElementById('currentMonth').textContent = monthNames[currentMonth - 1];
   }

   // 상담 내역 테이블 업데이트
   function updateConsultationTable() {
       const tableBody = document.getElementById('consultationTableBody');
       const monthData = consultationData[currentYear]?.[currentMonth] || [];
       
       tableBody.innerHTML = '';
       
       if (monthData.length === 0) {
           const row = document.createElement('tr');
           row.innerHTML = `
               <td colspan="5" style="text-align: center; padding: 40px; color: #7f8c8d; font-style: italic;">
                   ${currentYear}년 ${currentMonth}월 상담 내역이 없습니다.
               </td>
           `;
           tableBody.appendChild(row);
       } else {
           monthData.forEach((consultation, index) => {
               const row = document.createElement('tr');
               row.innerHTML = `
                   <td class="col-no">${index + 1}</td>
                   <td class="col-date">${consultation.date}</td>
                   <td class="col-time">${consultation.time}</td>
                   <td class="col-student-id">${consultation.studentId}</td>
                   <td class="col-student-name">${consultation.studentName}</td>
               `;
               tableBody.appendChild(row);
           });
       }
   }

   // 날짜 선택 모달 열기
   function openDateModal() {
       document.getElementById('yearSelect').value = currentYear;
       document.getElementById('monthSelect').value = currentMonth;
       document.getElementById('dateModal').style.display = 'block';
   }

   // 날짜 선택 모달 닫기
   function closeDateModal() {
       document.getElementById('dateModal').style.display = 'none';
   }

   // 날짜 변경 확인
   function confirmDateChange() {
       const selectedYear = parseInt(document.getElementById('yearSelect').value);
       const selectedMonth = parseInt(document.getElementById('monthSelect').value);
       
       currentYear = selectedYear;
       currentMonth = selectedMonth;
       
       updateMonthDisplay();
       updateConsultationTable();
       closeDateModal();
   }

   // 모달 외부 클릭 시 닫기
   window.onclick = function(event) {
       const modal = document.getElementById('dateModal');
       if (event.target === modal) {
           closeDateModal();
       }
   }

   // ESC 키로 모달 닫기
   document.addEventListener('keydown', function(event) {
       if (event.key === 'Escape') {
           closeDateModal();
       }
   });

   // 페이지 로드 시 상담사 정보 로드
   document.addEventListener('DOMContentLoaded', function() {
       const urlParams = new URLSearchParams(window.location.search);
       const counselorId = urlParams.get('id') || '1';
       
       // 상담사 정보 로드
       const counselor = counselorData[counselorId] || counselorData['1'];
       
       document.getElementById('counselorName').textContent = counselor.name;
       document.getElementById('counselorEmpNo').textContent = counselor.empNo;
       document.getElementById('counselorField').textContent = counselor.field;
       document.getElementById('workTime').textContent = counselor.workTime;
       document.getElementById('workDays').textContent = counselor.workDays;
       document.getElementById('consultationTime').textContent = counselor.consultationTime;
       
       // 초기 상담 내역 로드
       updateConsultationTable();
   });