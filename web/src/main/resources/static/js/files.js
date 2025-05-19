document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			SrvId: SrvID,
			filesData: [],
			currentFilesData: [],

			selectedFile: 
			{
				path: "",
				contents: [],
				children: null,
				isDirectory: false,
				size: -1,
			},
			filePath: "",
			isContextMenuVisible: false,
			isUlContextMenuVisible: false,
			contextMenuX: 0,
			contextMenuY: 0,
			parts: [],
			searchTerm: "",
	    },
	    created() {
			this.isAlive();
			this.getAllFiles();
	    },
		mounted() {
		  window.addEventListener('click', this.hideContextMenu);
		},
		beforeDestroy() {
		  window.removeEventListener('click', this.hideContextMenu);
		},
	    methods: {
			isAlive(){
				axios.post('/root/is_alive',this.SrvId,
					{
				    headers: {
				        'Content-Type': 'application/json'
				    }
					})
					.then(response => {
						if(response.data == true){
							alert('Сервер сейчас активен, вы будете перенаправлены к консоли сервера, подключитесь к нему и остановите, а затем повторите попытку.')
							window.location.href = '/console?ServerId=' + this.SrvId;
						}
					})
					.catch(error => {
						alert(error);
					});
			},
			getAllFiles(){
				axios.post('/files/all?id_server=' + this.SrvId)
					.then(response =>{
						Data = response.data;
						//console.log(Data.children);
						if(Data.children == null || Data.children.length === 0){
							this.filesData = [];
							this.currentFilesData = [];
							return;
						};	
						Data.children = Data.children
								.filter(item => item.path.toLowerCase()
								.includes(this.searchTerm.toLowerCase()));
						this.filesData = Data;
						this.currentFilesData = Data;
						console.log(this.children);
					})
					.catch(error =>{
						alert(error);
					});
			},
			getFile(){
				console.log(this.filePath);
				this.currentFilesData = this.travel();
			},
			travel(){
				var parts = this.filePath.split('/').filter(Boolean);
				if(parts != null && parts.length > 0){
					return this.recursiveTravel(parts, this.filesData, null);
				}
				else{
					return this.filesData;
				}
			},
			recursiveTravel(parts, files, fileName){
				if(fileName == null){
					fileName = parts.shift();
				}
				else{
					fileName += "/" + parts.shift();
				}
				var file = files.children.find(file => file.path === fileName);
				if(parts == null || parts.length == 0){
					return file;
				}
				else{
					return this.recursiveTravel(parts, file, fileName)
				}
			},
			search(){
				const lastSlashIndex = this.filePath.lastIndexOf('/')
				if (lastSlashIndex > 0) {
				  this.getFile();
				} else {
				  this.filePath = '';
				  this.getAllFiles();
				}
			},
			goBack(){
				const lastSlashIndex = this.filePath.lastIndexOf('/');

				if (lastSlashIndex > 0) {
				  this.filePath = this.filePath.substring(0, lastSlashIndex);
				  this.getFile();
				} else {
				  this.filePath = '';
				  this.getAllFiles();
				}
			},
			downloadFile(file){
					const fileUrl = '/files/download?id_server=' + this.SrvId + "&path=" + this.filePath + "/" + file.path;
					window.open(fileUrl, '_blank');
			},
			handleClick(file) {
				console.log('Left click on:', file.path);
				this.selectedFile = file;
			},
			handleRightClick(file, event) {
			    event.stopPropagation();
			    console.log('Right click on:', file.path);
			    console.log("Нажатие на li");
			    
			    if (this.isContextMenuVisible || this.isUlContextMenuVisible) return;
			    if (file == null) return;
			    
			    this.selectedFile = file;

			    const scrollY = window.scrollY;
			    this.contextMenuX = event.clientX;
			    this.contextMenuY = event.clientY + scrollY;

			    this.isContextMenuVisible = true;
			    console.log(this.isContextMenuVisible);
			},
			handleUlRightClick(event){
				if(this.isContextMenuVisible == true || this.isUlContextMenuVisible == true) return;
				
				const scrollY = window.scrollY;
				this.contextMenuX = event.clientX;
				this.contextMenuY = event.clientY  + scrollY;
				console.log("Нажатие на ul");
				this.isUlContextMenuVisible = true;
			},
			hideContextMenu() {
				this.isContextMenuVisible = false;
				this.isUlContextMenuVisible = false;
			},
			handleDoubleClick(file) {
  				console.log('Double click on:', file.path);
				if(file.path.includes('user_jvm_args.txt') || file.path.includes('eula.txt') || file.path.includes('run.sh')){
					alert("Этот файл нельзя редактировать!");
					return;
				}
				console.log(file.isDirectory == false);
				if(file.isDirectory == false){
					filename = file.path;
					fileExtension = filename.split('.').pop().toLowerCase();	
					
					if(fileExtension === "txt" || fileExtension === "json" || fileExtension === "properties" || fileExtension === "log" ){

					fileUrl = '/textReader?ServerId=' + this.SrvId + "&path=" + /*this.filePath + "/" +*/ file.path;
					window.open(fileUrl, '_blank');
					}
					else{
						alert("Этот тип файла нельзя редактировать!");
						return;
					}
				}
				else
				{
					//console.log(fileExtension);
					this.filePath = "/" + file.path;
					this.getFile()
				}
			},
			deleteFile(file){
				if(file.path.includes('user_jvm_args.txt') || file.path.includes('eula.txt') || file.path.includes('run.sh')){
					alert("Этот файл нельзя удалить!");
					return;
				}
				result = confirm("Удалить файл " + file.path + "?");
				if(result == false) return;
				axios.post('/files/delete?id_server=' + this.SrvId + "&path=" + /*this.filePath+"/"+*/file.path)
					.then(response =>{
						if(this.filePath == ""){
							this.getAllFiles();
						}
						else{
							this.getFile();
						}
						alert("Успешно");
					})
					.catch(error =>{
						alert(error);
					});
			},
			makeDir(){
				dirName = this.filePath.substring(1) + "/" + prompt("Создать папку?");
				if(dirName == null) return;
				
				if(file.path.includes('user_jvm_args.txt') || file.path.includes('eula.txt') || file.path.includes('run.sh')){
					alert("Данное имя недопустимо для папки!");
					return;
				}
				
				console.log(dirName);
				
				this.parts = this.filePath.split('/').filter(part => part);
					
				//this.parts.push(dirName);
				newDir = {
					isDirectory: true,
					children: [],
					contents: [],
					path: dirName,
					size: 0,
				}
				dir = this.makeDirRec(newDir);
				
				axios.post('/files/upload?id_server=' + this.SrvId, dir)
					.then(response =>{
						if(this.filePath == ""){
							this.getAllFiles();
						}
						else{
							this.getFile();
						}
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
					children: [],
					contents: [],
					path: currentName,
					size: -1,
				}
				
				if(this.parts.length > 0) dir.children.push(makeDirRec(file));
				else dir.children = file;
				
				return dir;
			},
			
			//пока не работает
			handleFileUpload(event) {
			  const files = event.target.children;
			  
			  if (files.length > 0) {
			    const fileArray = Array.from(files);
				
			    fileArray.forEach(file => {
			      const reader = new FileReader();

			      const fileName = file.path;
			      console.log('Имя файла:', fileName);
				  
				  if(file.path === 'user_jvm_args.txt' || file.path === 'eula.txt' || file.path === 'run.sh'){
				  	alert("Этот файл нельзя редактировать!");
					return;
				  }
				  
			      reader.readAsArrayBuffer(file);

			      reader.onload = () => {
			        const arrayBuffer = reader.result; 
			        const byteArray = new Uint8Array(arrayBuffer); 

			        console.log('Массив байтов для', fileName, ':', byteArray); 
			        newFileInfo = {
						isDirectory: false,
						children: [],
						contents: Array.from(byteArray),
						path: fileName,
						size,
					};
					
					this.parts = this.filePath.split('/').filter(part => part);
					fs = this.makeDirRec(newFileInfo);

					axios.post('/files/upload?id_server=' + this.SrvId, fs)
						.then(response =>{
							if(this.filePath == ""){
								this.getAllFiles();
							}
							else{
								this.getFile();
							}
						})
						.catch(error =>{
							alert(error);
						});
			      };

			      reader.onerror = (error) => {
			        console.error('Ошибка чтения файла:', error);
			      };
			    });
			  } else {
			    console.log('Файлы не выбраны');
			  }
			},
	    }
	});
});