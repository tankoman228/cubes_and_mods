new Vue({
    el: '#app',
    data: {
		//isAuthenticated: false,
		confirmPassword: '',
		user: {
			email: "",
			password: "",
			banned: false,
			additional_info: "Bibki"
		},
		isBlocked: false,
    },
    created() {
        //alert("Скрипт подключен");
    },
    methods: {
		submit(){
			this.isBlocked = true;
			axios.post('/users/auth', this.user)
			    .then(response => {
					window.location.href = "/";
			    })
			    .catch(error => {				
					if (error.response) {
						this.isBlocked = false;
						const statusCode = error.response.status;
						const message = error.response.data;
						console.error(statusCode + " : " + message);
						alert("Неверные данные пользователя");
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
