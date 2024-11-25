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
						    console.error('Ошибка при регистрации:', error.response.data);
						    alert(error.response.data);
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

