new Vue({
    el: '#app',
    data: {
		email: null,
		code: null,
    },
    created() {
        
    },
    methods: {
		check_code(){
			axios.get("http://localhost:8080/mail/checkCode", {
			  params: {
			    code: this.code
			  }
			})
			.then(response => {
			  this.email = response.data;
			  
			  if(this.email != null){
			  	alert("Удачно!");
			  	axios.get("http://localhost:8080/",{
			  		params: {
			  		    isAuthenticated: true
			  		}
			  	})
			  		.then(response =>{
			  			window.location.href = "http://localhost:8080/?isAuthenticated=true";
			  		})
			  		.catch(error =>{
			  			console.error(error);
			  			alert("Ошибка при аутентификации: ", error.message);
			  		});
			  }
			})
			.catch(error => {
			  console.error(error);
			  alert(error.message);
			});
				
		}
    }
});
