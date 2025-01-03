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
			axios.get("/mail/checkCode", {
			  params: {
			    code: this.code
			  }
			})
			.then(response => {
			  this.email = response.data;
			  
			  if(this.email != null){
			  	alert("Удачно!");
			  	window.location.href = "/";
			  }
			})
			.catch(error => {
			  console.error(error);
			  alert(error.message);
			});
				
		}
    }
});
