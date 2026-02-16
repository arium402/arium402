let selectedContactMethod = 'phone';
       
       function selectContactMethod(method) {
           // 모든 옵션 비활성화
           document.querySelectorAll('.contact-option').forEach(option => {
               option.classList.remove('active');
           });
           
           // 모든 입력 필드 숨기기
           document.querySelectorAll('.contact-input').forEach(input => {
               input.classList.remove('active');
           });
           
           // 선택된 방법 활성화
           event.target.classList.add('active');
           document.getElementById(method + '-input').classList.add('active');
           
           selectedContactMethod = method;
       }
       
       document.getElementById('findPasswordForm').addEventListener('submit', function(e) {
           e.preventDefault();
           
           // 입력값 검증
           const counselorId = document.getElementById('counselorId').value.trim();
           const name = document.getElementById('counselorName').value.trim();
           
           // 에러 메시지 초기화
           clearErrorMessages();
           
           let hasError = false;
           let contactValue = '';
           
           // 기본 정보 검증
           if (!counselorId) {
               showError('counselorIdError', '아이디를 입력해주세요.');
               hasError = true;
           }
           
           if (!name) {
               showError('nameError', '이름을 입력해주세요.');
               hasError = true;
           }
           
           // 연락처 검증
           if (selectedContactMethod === 'phone') {
               contactValue = document.getElementById('counselorPhone').value.trim();
               if (!contactValue) {
                   showError('phoneError', '휴대폰 번호를 입력해주세요.');
                   hasError = true;
               } else if (!validatePhone(contactValue)) {
                   showError('phoneError', '올바른 휴대폰 번호 형식이 아닙니다.');
                   hasError = true;
               }
           } else {
               contactValue = document.getElementById('counselorEmail').value.trim();
               if (!contactValue) {
                   showError('emailError', '이메일을 입력해주세요.');
                   hasError = true;
               } else if (!validateEmail(contactValue)) {
                   showError('emailError', '올바른 이메일 형식이 아닙니다.');
                   hasError = true;
               }
           }
           
           if (hasError) return;
           
           // 서버로 데이터 전송
           sendTemporaryPassword(counselorId, name, selectedContactMethod, contactValue);
       });
       
       function sendTemporaryPassword(counselorId, name, method, contact) {
           // 로딩 표시
           document.getElementById('loading').style.display = 'block';
           document.querySelector('.btn-group').style.display = 'none';
           
           // 서버 API 호출 시뮬레이션
           setTimeout(() => {
               document.getElementById('loading').style.display = 'none';
               
               // 성공 케이스
               const methodText = method === 'phone' ? '휴대폰' : '이메일';
               const resultText = `입력하신 ${methodText}(${contact})으로 임시 비밀번호가 전송되었습니다.\n\n임시 비밀번호로 로그인 후 반드시 비밀번호를 변경해주세요.`;
               
               showResult(resultText);
           }, 2000);
       }
       
       function showResult(message) {
           document.getElementById('findPasswordForm').style.display = 'none';
           document.getElementById('resultSection').style.display = 'block';
           document.getElementById('resultContent').textContent = message;
       }
       
       function showError(elementId, message) {
           const errorElement = document.getElementById(elementId);
           errorElement.textContent = message;
           errorElement.style.display = 'block';
       }
       
       function clearErrorMessages() {
           const errorElements = document.querySelectorAll('.error-message');
           errorElements.forEach(element => {
               element.style.display = 'none';
               element.textContent = '';
           });
       }
       
       function validatePhone(phone) {
           const phoneRegex = /^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$/;
           return phoneRegex.test(phone.replace(/\s/g, ''));
       }
       
       function validateEmail(email) {
           const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
           return emailRegex.test(email);
       }
       
       // 전화번호 자동 포맷팅
       document.getElementById('counselorPhone').addEventListener('input', function(e) {
           let value = e.target.value.replace(/[^0-9]/g, '');
           if (value.length >= 3 && value.length <= 7) {
               value = value.substring(0, 3) + '-' + value.substring(3);
           } else if (value.length > 7) {
               value = value.substring(0, 3) + '-' + value.substring(3, 7) + '-' + value.substring(7, 11);
           }
           e.target.value = value;
       });