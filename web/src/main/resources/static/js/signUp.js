new Vue({
    el: '#app',
    data: {
		user: {
			email: '',
			password: '',
			banned: false,
			additional_info: "Bibki",
		},
		confirmPassword: '',
		isBlocked: false,
    },
    created() {
        
    },
    methods: {
		submit(){
			if(this.user.password != this.confirmPassword){
				alert("Пароль и его подтверждение не совпадают");
				return;
			}
			this.isBlocked = true;
			axios.post('/users/register', this.user)
			    .then(response => {
					window.location.href = "/checkMail";
			    })
			    .catch(error => {		
					this.isBlocked = false;		
					if (error.response) {
					    const statusCode = error.response.status;
						switch (statusCode) {
							case 422:
							    alert('Введен невалидный адрес электронной почты..');
							    break;
							case 411:
							    alert('Пароль должен быть не менее 3 символов.');
							    break;
				            case 409:
				                alert('Электронная почта уже занята.');
				                break;
				            default:
				                alert('Произошла внутренняя нутрення ошибка сервера');
								break;
							}
					} else if (error.request) {
					    console.error('Запрос был сделан, но нет ответа:', error.request);
					    alert('Ошибка сети. Пожалуйста, попробуйте позже.');
					} else {
					    console.error('Ошибка:', error.message);
					    alert('Произошла непредвиденная ошибка');
					}
			    });
			},
		cancelSignIn(){
			window.history.back();
		},
    }
});

