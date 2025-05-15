document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			userId: UserID,
			email: Email,
			serverCores: [],
			gameVersions: [],
			selectedServerCore: "",
			selectedGameVer: "",
			search: "",
			versions: [],
			currentPage: 1,
			itemsPerPage: 10,
			payload: {
				id_version: -1,
				id_mineserver: ServerID,
			},
	    },
	    created() {
			this.getMCVersions();
	    },
		computed: {
		  totalPages() {
		    return Math.ceil(this.filteredVersions.length / this.itemsPerPage);
		  },
		  filteredVersions() {
		    let data = this.versions;
		    data = this.where(data, this.selectedServerCore);
		    data = this.where(data, this.selectedGameVer);
		    data = this.where(data, this.search);
		    return data;
		  },
		  paginatedVersions() {
		    const start = (this.currentPage - 1) * this.itemsPerPage;
		    return this.filteredVersions.slice(start, start + this.itemsPerPage);
		  }
		},
	    methods: {	
			getMCVersions(){
				axios.get('/versions/')
	                .then(response => {
	                    data = response.data;

						this.serverCores = [...new Set(data.map(item => {
						    const match = item.name.match(/^\[(.+?)\]/);
						    return match ? match[1] : null;
						}).filter(Boolean))];

						this.gameVersions = [...new Set(data.map(item => {
						    const match = item.name.match(/\[(?:.+?)\] (\d+\.\d+\.\d+)/);
						    return match ? match[1] : null;
						}).filter(Boolean))];
							
						console.log(this.serverCores);
						console.log(this.gameVersions);
						
						
						data = this.where(data, this.selectedServerCore);
						data = this.where(data, this.selectedGameVer);
						
						data = this.where(data, this.search);
						
						this.versions = data;
	                })
	                .catch(error => {
						alert("Ошибка: " + error);
						console.log(error);
	                });
			},
			changePage(page) {
			  if (page < 1 || page > this.totalPages) return; // Проверка на допустимые значения
			  this.currentPage = page;
			},
			where(data, search){
				if(search != ""){
					return data.filter(version => version.name.includes(search));
				}
				return data;
			},
			chooseVersion(version) {
				if(this.payload.id_mineserver == -1){
					alert("Не получен id сервера");
					return;
				}
				
				this.payload.id_version = version.id;
				//this.payload.id_version = 7;
				console.log(this.payload.id_version);
				console.log(this.payload.id_mineserver)
				axios.post('/root/unpack', this.payload)
				    .then(response => {
						console.log("OK");
				        window.location.href = "/console?ServerId=" + this.payload.id_mineserver;
				    })
				    .catch(error => {
						console.error(error);
						alert(error);
				    });
				alert("Наберитесь терпения, создание сервера займет некоторое время");
			},
	    }
	});
});