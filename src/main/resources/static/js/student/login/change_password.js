let currentStep = 1;
      let selectedVerificationMethod = 'phone';
      let verificationTimer;
      let timeLeft = 300; // 5분

      function goToStep(step) {
          // 현재 단계 숨기기
          document.querySelectorAll('.step-content').forEach(content => {
              content.classList.remove('active');
          });
          
          // 단계 표시기 업데이트
          document.querySelectorAll('.step').forEach((stepEl, index) => {
              stepEl.classList.remove('active');
              if (index + 1 < step) {
                  stepEl.classList.add('completed');
              } else {
                  stepEl.classList.remove('completed');
              }
          });
          
          // 새 단계 표시
          document.getElementById(`step${step}-content`).classList.add('active');
          document.getElementById(`step${step}`).classList.add('active');
          
          currentStep = step;
      }

      function goToStep1() { goToStep(1); }
      function goToStep2() { 
          const userId = document.getElementById('userId').value.trim();
          const email = document.getElementById('userEmail').value.trim();
          
          clearErrorMessages();
          
          if (!userId) {
              showError('userIdError', '아이디/학번을 입력해주세요.');
              return;
          }
          
          if (!email) {
              showError('emailError', '이메일을 입력해주세요.');
              return;
          }
          
          if (!validateEmail(email)) {
              showError('emailError', '올바른 이메일 형식이 아닙니다.');
              return;
          }
          
          // 이메일 확인 필드에 값 설정
          document.getElementById('verificationEmail').value = email;
          
          goToStep(2);
      }
      
      function goToStep3() { goToStep(3); }

      function selectVerificationMethod(method) {
          document.querySelectorAll('.contact-option').forEach(option => {
              option.classList.remove('active');
          });
          
          event.target.classList.add('active');
          selectedVerificationMethod = method;
          
          if (method === 'phone') {
              document.getElementById('phone-verification').style.display = 'block';
              document.getElementById('email-verification').style.display = 'none';
          } else {
              document.getElementById('phone-verification').style.display = 'none';
              document.getElementById('email-verification').style.display = 'block';
          }
      }

      function sendVerificationCode() {
          clearErrorMessages();
          
          if (selectedVerificationMethod === 'phone') {
              const phone = document.getElementById('phoneNumber').value.trim();
              if (!phone) {
                  showError('phoneError', '휴대폰 번호를 입력해주세요.');
                  return;
              }
              if (!validatePhone(phone)) {
                  showError('phoneError', '올바른 휴대폰 번호 형식이 아닙니다.');
                  return;
              }
          }
          
          // 로딩 표시
          document.getElementById('sendingCode').style.display = 'block';
          document.querySelector('#step2-content .btn-group').style.display = 'none';
          
          setTimeout(() => {
              document.getElementById('sendingCode').style.display = 'none';
              goToStep(3);
              startTimer();
          }, 2000);
      }

      function startTimer() {
          timeLeft = 300;
          updateTimer();
          
          verificationTimer = setInterval(() => {
              timeLeft--;
              updateTimer();
              
              if (timeLeft <= 0) {
                  clearInterval(verificationTimer);
                  alert('인증시간이 만료되었습니다. 다시 시도해주세요.');
                  goToStep(2);
              }
          }, 1000);
      }

      function updateTimer() {
          const minutes = Math.floor(timeLeft / 60);
          const seconds = timeLeft % 60;
          document.getElementById('timer').textContent = 
              `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
      }

      function verifyCode() {
          const code = document.getElementById('verificationCode').value.trim();
          
          if (!code) {
              showError('codeError', '인증코드를 입력해주세요.');
              return;
          }
          
          if (code.length !== 6) {
              showError('codeError', '인증코드는 6자리입니다.');
              return;
          }
          
          // 임시로 성공 처리 (실제로는 서버 검증)
          if (code === '123456') {
              clearInterval(verificationTimer);
              goToStep(4);
          } else {
              showError('codeError', '인증코드가 일치하지 않습니다.');
          }
      }

      function resendCode() {
          clearInterval(verificationTimer);
          sendVerificationCode();
      }

      function changePassword() {
          const newPassword = document.getElementById('newPassword').value;
          const confirmPassword = document.getElementById('confirmPassword').value;
          const userId = document.getElementById('userId').value.trim();
          
          clearErrorMessages();
          
          if (!newPassword) {
              showError('newPasswordError', '새 비밀번호를 입력해주세요.');
              return;
          }
          
          if (!validatePassword(newPassword, userId)) {
              showError('newPasswordError', '비밀번호가 조건에 맞지 않습니다.');
              return;
          }
          
          if (!confirmPassword) {
              showError('confirmPasswordError', '비밀번호 확인을 입력해주세요.');
              return;
          }
          
          if (newPassword !== confirmPassword) {
              showError('confirmPasswordError', '비밀번호가 일치하지 않습니다.');
              return;
          }
          
          // 로딩 표시
          document.getElementById('changingPassword').style.display = 'block';
          document.querySelector('#step4-content .btn-group').style.display = 'none';
          
          setTimeout(() => {
              document.getElementById('changingPassword').style.display = 'none';
              showSuccess();
          }, 2000);
      }

      function showSuccess() {
          document.querySelector('.step-indicator').style.display = 'none';
          document.querySelectorAll('.step-content').forEach(content => {
              content.style.display = 'none';
          });
          document.getElementById('success-section').style.display = 'block';
      }

      function validateEmail(email) {
          const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
          return emailRegex.test(email);
      }

      function validatePhone(phone) {
          const phoneRegex = /^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$/;
          return phoneRegex.test(phone.replace(/\s/g, ''));
      }

      function validatePassword(password, userId) {
          // 길이 검증
          if (password.length < 8 || password.length > 16) {
              return false;
          }
          
          // 아이디 포함 검증
          if (password.toLowerCase().includes(userId.toLowerCase())) {
              return false;
          }
          
          // 복잡성 검증 (3가지 이상 조합)
          let complexity = 0;
          if (/[a-z]/.test(password)) complexity++;
          if (/[A-Z]/.test(password)) complexity++;
          if (/[0-9]/.test(password)) complexity++;
          if (/[^a-zA-Z0-9]/.test(password)) complexity++;
          
          return complexity >= 3;
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

      // 전화번호 자동 포맷팅
      document.getElementById('phoneNumber').addEventListener('input', function(e) {
          let value = e.target.value.replace(/[^0-9]/g, '');
          if (value.length >= 3 && value.length <= 7) {
              value = value.substring(0, 3) + '-' + value.substring(3);
          } else if (value.length > 7) {
              value = value.substring(0, 3) + '-' + value.substring(3, 7) + '-' + value.substring(7, 11);
          }
          e.target.value = value;
      });

      // 인증코드는 숫자만 입력
      document.getElementById('verificationCode').addEventListener('input', function(e) {
          e.target.value = e.target.value.replace(/[^0-9]/g, '');
      });