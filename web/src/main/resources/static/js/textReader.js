document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {			
			srvId: SrvID,
			filePath: Path,
			email: Email,
			file: "",
			parts: [],
	    },
	    created() {
			this.getFile();
	    },
	    methods: {
			getFile(){
				axios.post('/files/getText?id_server=' + this.srvId + "&path=" + this.filePath)
					.then(response =>{
						this.file = response.data;
					})
					.catch(error =>{
						alert(error);
					});
			},
			save(){
				result = confirm("Сохранить файл?");
				if(result == false) return;

				this.parts = this.filePath.split('/').filter(part => part);
				
				file = {
					name: "",
					contents_bytes: [],
					files: null,
					isDirectory: false,
				}
				
				file.name = this.parts.pop();

				const encoder = new TextEncoder();

				const byteArray = encoder.encode(this.file.text);

				file.contents_bytes = Array.from(byteArray);
				
				console.log("Файл сформирован")
				
				fs = this.makeDirRec(file);

				axios.post('/files/upload?id_server=' + this.srvId, fs)
					.then(response =>{
						alert("Настройки сохранены!")
					})
					.catch(error =>{
						alert(error);
					});
			},
			makeDirRec(file){
				if(this.parts.length <= 0) return file;
				
				currentName = this.parts.shift();
				dir = {
					isDirectory: true,
					files: [],
					contents_bytes: [],
					name: currentName,
				}
				
				if(this.parts.length > 0) dir.files.push(makeDirRec(file));
				else dir.files = file;
				
				return dir;
			},
	    }
	});
});