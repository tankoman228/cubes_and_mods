new Vue({
    el: '#app',
    data: {
		isAuthenticated: false,
		user: {
			email: "",
			password: "",
			banned: false,
		},
    },
    created() {
        //alert("Скрипт подключен");
    },
    methods: {
		submit(){
			axios.post('/users/auth', this.user)
			    .then(response => {
			        this.isAuthenticated = response.data;
					axios.get('/mail/sendCode', {
						params: {
						    email: this.user.email
						}
					})
					    .then(response => {
							alert(response.data);					        
							axios.get("/checkMail")
								.then(response => {
									console.log(response);
									window.location.href = "/checkMail";
								})
								.catch(error => {
									console.error(error);
									alert(error.response.status);
								});
					    })
					    .catch(error => {
							console.error(error);
							alert("Ошибка при генерации кода", error.message);
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
						    case 404:
						        alert('Пользователь не найден.');
						        break;
						    case 400:
						        alert('Пользователь заблокирован или неверные данные.');
						        break;
						    case 403:
						        alert('Неверный пароль.');
						        break;
						    default:
						        alert('Произошла ошибка: ' + error.response.data);
								break;
							}
					} else if (error.request) {
					    console.error('Запрос был сделан, но нет ответа:', error.request);
					    alert('Ошибка сети. Пожалуйста, попробуйте позже.');
					} else {
					    console.error('Ошибка:', error.message);
					    alert('Произошла ошибка: ' + error.message);
					}
			    });
		},
		cancelSignIn(){
			window.history.back();
		},
    }
});
