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
			    axios.get("http://localhost:8080"+page)
			        .then(response => {
			            console.log("Ответ:", response.data);
						window.location.href = "http://localhost:8080"+page;
			        })
			        .catch(error => {
						console.log(page);
						console.log(encodeURIComponent(page));
			            console.error("Ошибка при загрузке страницы:", error);
			            alert("Это отвалn\n" + error);
			        });
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