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
			axios.post('http://127.0.0.1:8085/users/auth', this.user)
			    .then(response => {
			        this.isAuthenticated = response.data;
					axios.get('http://127.0.0.1:8085/users/generate_code', {
						params: {
						    email: this.user.email
						}
					})
					    .then(response => {
							//console.log(response);
							alert(response.data);					        
							axios.get("/checkMail")
								.then(response => {
									console.log(response);
									window.location.href = "http://127.0.0.1:8080/checkMail";
								})
								.catch(error => {
									console.error(error);
									alert(error.response.status);
								});
					    })
					    .catch(error => {
							console.error(error);
							alert("Ошибка при генерации пароля", error.response.status);
					    });
			    })
			    .catch(error => {				
					if (error.response.status == 404) {
					   alert('Нет такого пользователя!');
					 } else if (error.response.status == 400) {
					   alert('Данный пользователь заблокирован!');
					 } else if (error.response.status == 403) {
					   alert('Неверный пароль!');
					 } else {
					   alert('Произошла ошибка: ', error.response.status);
					 }
			    });
		},
		cancelSignIn(){
			window.history.back();
		},
    }
});
