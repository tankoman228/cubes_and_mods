document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			userId: UserID,
			email: Email,
			versions: [],
			payload: {
				id_version: -1,
				id_mineserver: ServerID,
			},
	    },
	    created() {
			this.getMCVersions();
	    },
	    methods: {	
			getMCVersions(){
				axios.get('/versions/')
	                .then(response => {
	                    this.versions = response.data;
	                })
	                .catch(error => {
						alert("Ошибка: " + error);
	                });
			},
			chooseVersion(version) {
				if(this.payload.id_mineserver == -1){
					alert("Не получен id сервера");
					return;
				}
				
				this.payload.id_version = version.id;
				//this.payload.id_version = 7;
				axios.post('/root/unpack', this.payload)
				    .then(response => {
				        window.location.href = "/console?ServerId=" + this.payload.id_mineserver;
				    })
				    .catch(error => {
						alert(error);
				    });
				alert("Наберитесь терпения, создание сервера займет некоторое время");
			},
	    }
	});
});