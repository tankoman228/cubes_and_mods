document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#header',
	    data: {
			email: Email,
	    },
	    created() {
			//alert(this.isAuthenticated);
	    },
	    methods: {
			goTo(page = "") {
			    window.location.href = page;
			},
			logOut(){
				axios.get('/users/logout')
				    .then(response => {
						alert("Вы вышли из системы");
						window.location.href = "/";
				    })
				    .catch(error => {
						alert(error);
				    });
			}
	    }
	});
});