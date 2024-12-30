console.log('tariff import');

export let data = {
	tariffs: [],
	tariff: {
		name: '',
		cost_rub: 499,
		ram: 2,
		cpu_threads: 2,
		memory_limit: 10024000,
		hours_work_max: 100,
		max_players: 20,
		enabled: true
	}
}

export let methods = {
	async getAllTariffs() {
		await fetch('http://localhost:8082/tariffs')
			.then(response => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				}
				return response.json();
			})
			.then(data => {
				this.tariffs = data; // Сохранение всех тарифов в this.tariffs
				console.log('Получены тарифы:', this.tariffs);
			})
			.catch((error) => {
				console.error('Ошибка при получении тарифов:', error);
			});
	},
	addTariff() {
		fetch('http://localhost:8082/tariffs', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(this.tariff)
			})
			.then(response => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				}
				return response.json();
			})
			.then(data => {
				this.tariffs.push(data);
			})
			.catch((error) => {
				console.error('Ошибка при добавлении тарифа:', error);
			});
	},
	toggleTariff(t) {
		t.enabled = !t.enabled;
		fetch(`http://localhost:8082/tariffs/${t.id}`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(t)
		})
			.then(response => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				}
				return response.json();
			})
			.then(data => {
				console.log('Статус тарифа изменен:', data);
			})
			.catch((error) => {
				console.error('Ошибка при изменении статуса тарифа:', error);
			});
	},
	deleteTariff(t) {
		const confirmation = confirm("Вы уверены, что хотите удалить этот тариф?");
		if (!confirmation) return;

		fetch(`http://localhost:8082/tariffs/${t.id}`, {
			method: 'DELETE' // Удаляем тариф
		})
			.then(response => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				}
				console.log('Тариф успешно удален:', t);
				mounted.call(this);
			})
			.catch((error) => {
				console.error('Ошибка при удалении тарифа:', error);
			});
	}
}

export async function mounted() {

	await this.getAllTariffs();

	let onlyAvailable = []
	this.tariffs.forEach(t => {
		if (t.enabled == true) {
			onlyAvailable.push(t);
		}
	})
	this.tariffs = onlyAvailable;
}
