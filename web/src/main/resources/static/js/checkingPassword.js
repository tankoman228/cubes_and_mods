document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			email: Email,
	    },
	    created() {
			this.check_code();
	    },
	    methods: {
			check_code(){
				if(this.email != null){
					window.location.href = "/";
				}
				else{
					alert("Произошла ошибка при проверке кода доступа");
				}
			}
	    }
	});
});