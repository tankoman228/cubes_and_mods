document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			SrvId: SrvID,
			filesData: [],
			currentFilesData: [],
			copyFilesData: [],
			isMove: false,
			selectedFile: 
			{
				path: "",
				contents: [],
				children: null,
				isDirectory: false,
				size: -1,
			},
			selectedFiles: [],
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
						console.log("Сервер выключен");
					})
					.catch(error => {
						alert(error);
						console.error(error);
					});
			},
			getAllFiles(isUpdate = false){
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
								.filter(item => item.path 
									&& item.path.length > 0  
									&& item.path.toLowerCase().includes(this.searchTerm.toLowerCase()));
						this.filesData = Data;
						if(isUpdate){
							this.getFile();
						}
						else{
							this.currentFilesData = Data;
							this.selectedFiles = [];
						}
						console.log(Data.children);
					})
					.catch(error =>{
						alert(error);
						console.error(error);
					});
			},
			getFile(){
				console.log(this.filePath);
				this.selectedFiles = [];
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
			filterFiles(files, keepDirectories = false) {
				return files.filter(file => file.isDirectory === keepDirectories);
			},
			downloadFile(){
				//const fileUrl = '/files/download?id_server=' + this.SrvId + "&path=" + file.path;
				//window.open(fileUrl, '_blank');
				var sf = this.filterFiles(this.selectedFiles, false);
				if(sf.length == 0){
					alert("Выберите файлы для скачивания!");
					return;
				}

				paths = [];
				sf.forEach(file => {
					paths.push(file.path);
				});

				axios.post(`/files/downloadZip?id_server=${this.SrvId}`, paths, {
					responseType: 'blob'
					}).then(response => {
						const blob = new Blob([response.data], { type: 'application/zip' });
						const link = document.createElement('a');
						link.href = URL.createObjectURL(blob);
						link.download = 'archive.zip';
						link.click();
						URL.revokeObjectURL(link.href);
					}).catch(error => {
						console.error('Ошибка при скачивании архива:', error);
					});
			},
			toggleFile(file) {
				const index = this.selectedFiles.findIndex(f => f.path === file.path)

				if (index === -1) {
					this.selectedFiles.push(file)
				} else {
					this.selectedFiles.splice(index, 1)
				}
				console.log('Selected files:', this.selectedFiles);
			},
			handleClick(file, event) {
				console.log('Left click on:', file.path);
			    if (event.ctrlKey || event.metaKey) {
					this.toggleFile(file);
				} else {
					this.selectedFiles = [file];
				}
				this.selectedFile = file;
				console.log(this.selectedFiles);
			},
			handleUlClick(event){
				if (event.target.tagName.toLowerCase() !== 'li' && !event.target.closest('li')) {
					console.log("Нажатие на ul");
					this.selectedFiles = [];
				}
			},
			handleRightClick(file, event) {
			    event.stopPropagation();
			    console.log('Right click on:', file.path);
			    console.log("Нажатие на li");
			    
			    if (this.isContextMenuVisible || this.isUlContextMenuVisible) return;
			    if (file == null) return;
			    
				if(this.selectedFiles.length <= 1){
					this.selectedFiles = [file];
				}
				else{
					const index = this.selectedFiles.findIndex(f => f.path === file.path)
					if (index === -1) this.selectedFiles.push(file);
				}

			    //this.selectedFile = file;
				//this.toggleFile(file);

			    const scrollY = window.scrollY;
			    this.contextMenuX = event.clientX;
			    this.contextMenuY = event.clientY + scrollY;

			    this.isContextMenuVisible = true;
			    console.log(this.isContextMenuVisible);
			},
			handleUlRightClick(event){
				if(this.isContextMenuVisible == true || this.isUlContextMenuVisible == true) return;
				this.selectedFiles = [];

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
			deleteFile(){
				result = confirm("Удалить?");
				if(result == false) return;
				if(this.selectedFiles.length == 0){
					alert("Выберите файлы для удаления!");
					return;
				}
				this.selectedFiles.forEach(file => {
					if(file.path.includes('user_jvm_args.txt') || file.path.includes('eula.txt') || file.path.includes('run.sh')){
						alert("Этот файл нельзя удалить!");
						return;
					}
				});
			
				paths = [];
				this.selectedFiles.forEach(file => {
					paths.push(file.path);
				});
				axios.post('/files/deleteRange?id_server=' + this.SrvId, paths)
					.then(response =>{
						if(this.filePath == ""){
							this.getAllFiles();
						}
						else{
							this.getAllFiles(true);
						}
						alert("Успешно");
					})
					.catch(error =>{
						alert(error);
					});

				/*axios.post('/files/delete?id_server=' + this.SrvId + "&path=" + /*this.filePath+"/"+file.path)
					.then(response =>{
						if(this.filePath == ""){
							this.getAllFiles();
						}
						else{
							this.getAllFiles(true);
						}
						alert("Успешно");
					})
					.catch(error =>{
						alert(error);
					});*/
			},
			renameFile(){
				if(this.selectedFiles.length == 0){
					alert("Выберите файлы для переименования!");
					return;
				}
				if(this.selectedFiles.length > 1){
					alert("Выберите только один файл для переименования!");
					return;
				}
				if(this.selectedFiles[0].path.includes('user_jvm_args.txt') || this.selectedFiles[0].path.includes('eula.txt') || this.selectedFiles[0].path.includes('run.sh')){
					alert("Этот файл нельзя переименовать!");
					return;
				}
				const newName = prompt("Введите новое имя файла:");
				if (newName === null) {
					return;
				}
				const newPath = this.selectedFiles[0].path.replace(this.selectedFiles[0].path.split('/').pop(), newName);
				paths = [];
				paths.push(this.selectedFiles[0].path);
				paths.push(newPath);
				axios.put('/files/move?id_server=' + this.SrvId, paths)
					.then(response =>{
						if(this.filePath == ""){
							this.getAllFiles();
						}
						else{
							this.getAllFiles(true);
						}
						alert("Успешно");
					})
					.catch(error =>{
						alert(error);
					});
			},
			moveFile(){
				var sf = this.filterFiles(this.selectedFiles, false);
				if(sf.length == 0){
					alert("Выберите файлы для перемещения!");
					return;
				}
				sf.forEach(file => {
					if(file.path.includes('user_jvm_args.txt') || file.path.includes('eula.txt') || file.path.includes('run.sh')){
						alert("Этот файл нельзя переместить!" + file.path);
						return;
					}
				});
				this.copyFilesData = sf;
				this.isMove = true;
			},
			copyFile(){
				var sf = this.filterFiles(this.selectedFiles, false);
				if(sf.length == 0){
					alert("Выберите файлы для копирования!");
					return;
				}
				sf.forEach(file => {
					if(file.path.includes('user_jvm_args.txt') || file.path.includes('eula.txt') || file.path.includes('run.sh')){
						alert("Этот файл нельзя скопировать! " + file.path);
						return;
					}
				});
				this.copyFilesData = sf;
				this.isMove = false;
			},
			pasteFile(){
				var cpfd = this.filterFiles(this.copyFilesData, false);
				if(cpfd.length == 0){
					alert("Выберите файлы для вставки!");
					return;
				}
				cpfd.forEach(file => {
					if(file.path.includes('user_jvm_args.txt') || file.path.includes('eula.txt') || file.path.includes('run.sh')){
						alert("Этот файл нельзя вставить!" + file.path);
						return;
					}
				});

				var query = '/files/copy?id_server=';
				if(this.isMove) query = '/files/move?id_server=';

				cpfd.forEach(file => {
					paths = [];
					paths.push(file.path);
					paths.push(this.filePath.substring(1) + "/" + file.path.split('/').pop());

					axios.put(query + this.SrvId, paths)
					.then(response =>{
						if(this.filePath == ""){
							this.getAllFiles();
						}
						else{
							this.getAllFiles(true);
						}
						alert("Успешно");
					})
					.catch(error =>{
						alert(error);
					});
				});
			},
			isSelected(file) {
				return this.selectedFiles.some(f => f.path === file.path);
			},
			handleFileUpload(event) {
			  	const files = event.target.files;
			  	console.log(event.target);
			  
			  	if (files.length > 0) {
			    	const fileArray = Array.from(files);
				
			    	fileArray.forEach(file => {
			      		const reader = new FileReader();

						const fileName = file.name;
						console.log('Имя файла:', fileName);
						
						if(file.path === 'user_jvm_args.txt' || file.path === 'eula.txt' || file.path === 'run.sh'){
							alert("Этот файл нельзя редактировать!");
							return;
						}
						
						reader.readAsArrayBuffer(file);

						reader.onload = () => {
							const arrayBuffer = reader.result; 
							const byteArray = new Uint8Array(arrayBuffer); 

							var path = this.filePath.substring(1);
							if(path.length > 0) path += "/";
							path += fileName;

							console.log('Массив байтов для', fileName, ':', byteArray); 
							newFileInfo = {
								isDirectory: false,
								children: [],
								contents: Array.from(byteArray),
								path: path,
								size: -1,
							};
							newFileInfo.size = newFileInfo.contents.length;
							this.parts = this.filePath.split('/').filter(part => part);
							//fs = this.makeDirRec(newFileInfo);

							axios.post('/files/upload?id_server=' + this.SrvId, newFileInfo)
								.then(response =>{
									if(this.filePath == ""){
										this.getAllFiles();
									}
									else{
										this.getAllFiles(true);
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
			makeDir(){
				dirName = this.filePath.substring(1) + "/" + prompt("Создать папку?");
				if(dirName == null) return;
				
				if(dirName.includes('user_jvm_args.txt') || dirName.includes('eula.txt') || dirName.includes('run.sh')){
					alert("Данное имя недопустимо для папки!");
					return;
				}
				
				console.log(dirName);
				
				//this.parts = this.filePath.split('/').filter(part => part);
					
				newDir = {
					isDirectory: true,
					children: [],
					contents: [],
					path: dirName,
					size: 0,
				}
				//dir = this.makeDirRec(newDir);
				
				axios.post('/files/upload?id_server=' + this.SrvId, newDir)
					.then(response =>{
						if(this.filePath == ""){
							this.getAllFiles();
						}
						else{
							this.getAllFiles(true);
						}
						alert("Успешно");
						console.log("обновление после сохдания папки");
					})
					.catch(error =>{
						alert(error);
						console.error(error);
					});
			},
			/*makeDirRec(file){
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
			},*/
	    }
	});
});