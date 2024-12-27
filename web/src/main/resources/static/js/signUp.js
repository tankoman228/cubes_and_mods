new Vue({
    el: '#app',
    data: {
		user: {
			email: '',
			password: '',
			banned: false,
		},
    },
    created() {
        
    },
    methods: {
		submit(){
				axios.post('/users/register', this.user)
				    .then(response => {
						axios.get("/signIn")
							.then(response => {
								console.log(response);
								window.location.href = "/signIn";
							})
							.catch(error => {
								console.error(error);
								alert(error.response.status);
							});
				    })
				    .catch(error => {				
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

