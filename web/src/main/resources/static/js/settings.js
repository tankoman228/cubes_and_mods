document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			SrvId: SrvID,
			Settings: "",
	    },
	    created() {
			this.getFile()
	    },
	    methods: {
			getFile(){
				console.log(this.filePath);
				axios.post('/files/byPath?id_server=' + this.SrvId + "&path=/server.properties")
					.then(response =>{
						encodedData = response.data.contents_bytes;
					})
					.catch(error =>{
						alert(error);
					});
			},
	    }
	});
});