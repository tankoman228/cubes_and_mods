new Vue({
    el: '#app',
    data: {
		confirmPassword: '',
		user: {
			email: "",
			password: "",
			banned: false,
		},
		disabled: false,
    },
    created() {
        //alert("Скрипт подключен");
    },
    methods: {
		submit(){
			disabled = true;
			axios.post('/users/auth', this.user)
			    .then(response => {
					window.location.href = "/checkMail";
					disabled = false;
			    })
			    .catch(error => {				
					if (error.response) {
						const statusCode = error.response.status;
						const message = error.response.data;
						console.error(statusCode + " : " + message);
						alert(message)
					} else if (error.request) {
					    console.error('Запрос был сделан, но нет ответа:', error.request);
					    alert('Ошибка сети. Пожалуйста, попробуйте позже.');
					} else {
					    console.error('Ошибка:', error.message);
					    alert('Произошла ошибка: ' + error.message);
					}
					disabled = false;
			    });
		},
		cancelSignIn(){
			window.history.back();
		},
    }
});
