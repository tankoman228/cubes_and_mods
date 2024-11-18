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
			axios.post('http://127.0.0.1:8085/users/register', this.user)
			    .then(response => {
			        this.isAuthenticated = response.data;
					axios.get('http://127.0.0.1:8085/users/generate_code', this.user.email)
					    .then(response => {
							console.log(response.data);	
							alert(response.data);					        
							axios.get("/checkMail")
								.then(response => {
									console.log(response);
									window.location.href = "http://127.0.0.1:8080/"+response.data;
								})
								.catch(error => {
									console.error(error);
									alert(error);
								});
					    })
					    .catch(error => {
							console.error(error);
							alert("Ошибка при генерации пароля", error.response.status);
					    });
			    })
			    .catch(error => {				
					   if (error.response.status === 409) {
					   alert('Данный пользователь уже существует!');
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

